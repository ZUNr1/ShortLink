package org.zunr1.backend.utils;

import org.zunr1.backend.dto.LinkResponse;
import org.zunr1.backend.entity.Link;

public class LinkConverter {
    public static LinkResponse convertFromLink(Link link){
        LinkResponse linkResponse = LinkResponse.builder()
                .id(link.getId())
                .name(link.getName())
                .longUrl(link.getLongUrl())
                .shortCode(link.getShortCode())
                .clickCount(link.getClickCount())
                .expireAt(link.getExpireAt())
                .createdAt(link.getCreatedAt())
                .updatedAt(link.getUpdatedAt())
                .build();
        return linkResponse;
    }
    public static Link convertFromLinkResponse(LinkResponse response){
        return null;
    }
}
