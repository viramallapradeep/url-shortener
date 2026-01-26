package com.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS = 3;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final StringRedisTemplate redisTemplate;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String ip) {
        String key = "rl:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);
        System.out.println("IP=" + ip + " count=" + count);
        if (count == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        return count <= MAX_REQUESTS;
    }
}
