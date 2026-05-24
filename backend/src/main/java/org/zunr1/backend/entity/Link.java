package org.zunr1.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Link {
    private Long id;
    private Long userId;
    private String name;
    private String longUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
