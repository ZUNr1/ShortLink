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

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class LinkServiceImpl implements LinkService{
    @Autowired
    private LinkMapper linkMapper;
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
    public String switchUrl(String longUrl,String name,String expireAt,Long userId) {


        Link existing = linkMapper.selectLinkByLongUrl(longUrl);
        if (existing != null){
            return existing.getShortCode();
        }
        Link link = new Link();
        link.setLongUrl(longUrl);
        link.setName(name);
        link.setUserId(userId);
        link.setShortCode(UUID.randomUUID().toString().replace("-",""));//todo
        try {
            link.setExpireAt(LocalDateTime.parse(expireAt));
        } catch (DateTimeParseException e) {
            throw new BadRequestException("过期时间格式错误，请使用 yyyy-MM-dd'T'HH:mm:ss");
        }
        linkMapper.insertLink(link);
        return link.getShortCode();
    }
}
