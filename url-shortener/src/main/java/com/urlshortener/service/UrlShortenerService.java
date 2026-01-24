package com.urlshortener.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    // 1️ Create short URL
    public UrlMapping shortenUrl(String longUrl) {
        String shortKey = generateShortKey();

        UrlMapping mapping = new UrlMapping(shortKey, longUrl);
        return repository.save(mapping);
    }

    // 2️⃣ Fetch long URL
    public UrlMapping getLongUrl(String shortKey) {
        return repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    }

    // Helper method
    private String generateShortKey() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
