package com.urlshortener.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.util.Base62Encoder;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    
    private final StringRedisTemplate redisTemp;

    public UrlShortenerService(UrlMappingRepository repository,StringRedisTemplate redisTemp) {
        this.repository = repository;
        this.redisTemp=redisTemp;
    }

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

    public String getLongUrl(String shortKey) {
    	String cachedUrl = redisTemp.opsForValue().get(shortKey);
    	if(cachedUrl!=null) {
    		System.out.println("====cachedUrl from redis cache===="+cachedUrl);
    		return cachedUrl;
    	}
    	
    	 UrlMapping mapping = repository.findByShortKey(shortKey)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
    	 
    	 System.out.println("====URl from DB===="+mapping.getLongUrl());
        
        redisTemp.opsForValue()
        .set(shortKey, mapping.getLongUrl(),Duration.ofHours(24));
        
        return mapping.getLongUrl();
    }

    private String generateShortKey() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
