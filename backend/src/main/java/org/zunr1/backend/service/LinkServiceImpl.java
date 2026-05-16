package org.zunr1.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zunr1.backend.entity.Link;
import org.zunr1.backend.mapper.LinkMapper;

import java.util.UUID;

@Service
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
            return link.getLongUrl();
        }
        return null;
    }

    @Override
    public String switchUrl(String longUrl) {
        if (longUrl == null || longUrl.isEmpty()){
            return null;
        }
        Link existing = linkMapper.selectLinkByLongUrl(longUrl);
        if (existing != null){
            return existing.getShortCode();
        }
        Link link = new Link();
        link.setLongUrl(longUrl);
        link.setUserId(null);//todo
        link.setShortCode(UUID.randomUUID().toString().replace("-",""));//todo
        linkMapper.insertLink(link);
        return link.getShortCode();
    }
}
