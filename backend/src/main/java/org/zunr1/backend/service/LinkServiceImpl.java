package org.zunr1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zunr1.backend.dto.LinkResponse;
import org.zunr1.backend.dto.Result;
import org.zunr1.backend.entity.Link;
import org.zunr1.backend.exception.BadRequestException;
import org.zunr1.backend.exception.NotFoundException;
import org.zunr1.backend.mapper.LinkMapper;
import org.zunr1.backend.utils.LinkConverter;
import org.zunr1.backend.utils.ShortCodeGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class LinkServiceImpl implements LinkService{
    @Autowired
    private LinkMapper linkMapper;
    @Autowired
    private ShortCodeGenerator shortCodeGenerator;

    @Override
    public String getLongUrl(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()){
            return null;
        }
        Link link = linkMapper.selectLinkByShortCode(shortCode);
        if (link != null){
            link.setClickCount(link.getClickCount() + 1);//todo异步处理
            linkMapper.updateClickCount(link.getClickCount(), link.getId());
            return link.getLongUrl();
        }
        return null;
    }

    @Override
    public LinkResponse getLinkByShortCode(String shortCode) {
        if (shortCode == null || shortCode.isEmpty()) {
            throw new BadRequestException("短码不能为空");
        }
        Link link = linkMapper.selectLinkByShortCode(shortCode);
        if (link == null) {
            throw new NotFoundException("短链不存在");
        }

        link.setClickCount(link.getClickCount() + 1);
        linkMapper.updateClickCount(link.getClickCount(), link.getId());

        LinkResponse linkResponse = LinkConverter.convertFromLink(link);
        linkResponse.setExisting(true);
        return linkResponse;
    }

    @Override
    public String switchUrl(String longUrl, String name, String expireAt, Long userId) {
        // 参数校验
        if (longUrl == null || longUrl.isEmpty()) {
            throw new BadRequestException("URL不能为空");
        }

        // 生成短码（带冲突检测）
        String shortCode;
        int retryCount = 0;
        do {
            shortCode = shortCodeGenerator.generateSecureCode(longUrl, userId);

            // 检查短码是否已存在
            Link existingByCode = linkMapper.selectLinkByShortCode(shortCode);
            if (existingByCode == null) {
                break;
            }

            retryCount++;
            if (retryCount >= 3) {
                // 加入时间戳再次生成，避免死循环
                shortCode = shortCodeGenerator.generateSecureCode(
                        longUrl + System.currentTimeMillis() + userId,
                        userId
                );
                break;
            }
        } while (true);

        // 创建链接实体
        Link link = new Link();
        link.setLongUrl(longUrl);
        link.setName(name);
        link.setUserId(userId);
        link.setShortCode(shortCode);
        link.setClickCount(0L);

        // 处理过期时间
        if (expireAt != null && !expireAt.isEmpty()) {
            try {
                link.setExpireAt(LocalDateTime.parse(expireAt));
            } catch (DateTimeParseException e) {
                throw new BadRequestException("过期时间格式错误，请使用 yyyy-MM-dd'T'HH:mm:ss");
            }
        }

        // 存入数据库
        int rows = linkMapper.insertLink(link);
        if (rows <= 0) {
            throw new RuntimeException("创建短链失败");
        }

        return shortCode;
    }

    @Override
    public List<LinkResponse> getUserLinks(Long userId) {
        List<Link> links = linkMapper.selectLinksByUserId(userId);
        return links.stream()
                .map(LinkConverter::convertFromLink)
                .toList();
    }
}
