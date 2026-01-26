package com.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import com.urlshortener.util.Base62Encoder;
import com.urlshortener.util.SnowflakeIdGenerator;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    
    private final StringRedisTemplate redisTemp;

    private final SnowflakeIdGenerator snowflakeGen;
    public UrlShortenerService(UrlMappingRepository repository,
    		                   StringRedisTemplate redisTemp,
    		                   SnowflakeIdGenerator snowflakeGen) {
        this.repository = repository;
        this.redisTemp=redisTemp;
        this.snowflakeGen=snowflakeGen;
    }

    public UrlMapping shortenUrl(String longUrl) {
    	long id = snowflakeGen.nextId();
    	System.out.println("====Snowflake ID===="+id);
    	String shortKey = Base62Encoder.encode(id);
        UrlMapping mapping = new UrlMapping(id,shortKey, longUrl);
        UrlMapping saved = repository.save(mapping);
        System.out.println("====shortKey ID===="+shortKey);

        return saved;
    }

    public String getLongUrl(String shortKey) {
    	String cachedUrl = null;
    	
    	try {
    		cachedUrl=redisTemp.opsForValue().get(shortKey);
		} catch (Exception e) {
			System.out.println("===cache is down, falling back to DB===");
		}
    			
    			
    	if(cachedUrl!=null) {
    		System.out.println("====Fetched from redis cache===="+cachedUrl);
    		return cachedUrl;
    	}
    	
    	 UrlMapping mapping = repository.findByShortKey(shortKey)
                .orElse(null);
    	 
        if(mapping!=null) {
        	System.out.println("====Fetched from DB===="+mapping.getLongUrl());
    	 try {
    		 System.out.println("====Writing to cache===="+mapping.getLongUrl());
    		 redisTemp.opsForValue().set(shortKey, mapping.getLongUrl(),Duration.ofHours(24));
		} catch (Exception e) {
			//writing to cache
		}
        }
    	 
        return mapping==null?null:mapping.getLongUrl();
    }
}
