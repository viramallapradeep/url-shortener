package com.urlshortener.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.util.Base62Encoder;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerService(UrlMappingRepository repository) {
        this.repository = repository;
    }

    // 1️ Create short URL
    public UrlMapping shortenUrl(String longUrl) {

        // Step 1: save without shortKey
        UrlMapping mapping = new UrlMapping(null, longUrl);
        UrlMapping saved = repository.save(mapping);

        // Step 2: generate shortKey from ID
        System.out.println("====ID from DB===="+saved.getId());
        String shortKey = Base62Encoder.encode(saved.getId());
        saved.setShortKey(shortKey);

        return repository.save(saved);
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
