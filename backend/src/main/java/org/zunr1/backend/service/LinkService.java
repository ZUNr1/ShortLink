package org.zunr1.backend.service;


import org.zunr1.backend.dto.LinkResponse;
import org.zunr1.backend.dto.Result;

import java.util.List;

public interface LinkService {
    // 跳转用：返回长链接，不存在返回null
    String getLongUrl(String shortCode);
    // 创建短链：返回短码
    String switchUrl(String longUrl,String name,String expireAt,Long userId);
    // 查询详情：返回LinkResponse，不存在抛异常
    LinkResponse getLinkByShortCode(String shortCode);
    // 添加方法
    List<LinkResponse> getUserLinks(Long userId);
}
