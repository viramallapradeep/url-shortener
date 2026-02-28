package com.urlshortener.ratelimiter;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component("fixedWindow")
public class FixedWindowRateLimiter implements RateLimiter {
	 private final StringRedisTemplate redisTemplate;
	    private final int maxRequests;
	    private final long windowSizeSeconds;

	    public FixedWindowRateLimiter(StringRedisTemplate redisTemplate) {
	        this.redisTemplate = redisTemplate;
	        this.maxRequests = 1;
	        this.windowSizeSeconds = 3;
	    }

	    @Override
	    public boolean allowRequest(String key) {

	        String redisKey = "rate:fixed:" + key;

	        Long count = redisTemplate.opsForValue().increment(redisKey);

	        if (count == 1) {
	            redisTemplate.expire(redisKey, windowSizeSeconds, TimeUnit.SECONDS);
	        }

	        return count <= maxRequests;
	    }
}
