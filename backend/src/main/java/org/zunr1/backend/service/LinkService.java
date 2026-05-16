package org.zunr1.backend.service;


public interface LinkService {
    String getLongUrl(String shortCode);
    String switchUrl(String longUrl);
}
