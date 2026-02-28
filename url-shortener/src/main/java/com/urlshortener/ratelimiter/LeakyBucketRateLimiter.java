package com.urlshortener.ratelimiter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


@Component("leakyBucket")
public class LeakyBucketRateLimiter implements RateLimiter {
    private final StringRedisTemplate redisTemplate;

    private final int capacity = 1;          // max bucket size
    private final double leakRate = 0;     // requests per second

    public LeakyBucketRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String key) {
    	
    	System.out.println("leakybucket=====allowRequest");
        String redisKey = "rate:leaky:" + key;
        long currentTime = System.currentTimeMillis() / 1000;

        // 1️⃣ Get current state
        List<Object> values = redisTemplate.opsForHash()
                .multiGet(redisKey, Arrays.asList("water", "lastLeakTime"));

        double water;
        long lastLeakTime;

        if (values.get(0) == null || values.get(1) == null) {
            water = 0;
            lastLeakTime = currentTime;
        } else {
            water = Double.parseDouble(values.get(0).toString());
            lastLeakTime = Long.parseLong(values.get(1).toString());
        }

        // 2️⃣ Calculate leaked water
        long delta = currentTime - lastLeakTime;
        double leaked = delta * leakRate;

        water = Math.max(0, water - leaked);

        boolean allowed = false;

        // 3️⃣ Try to add new request
        if (water < capacity) {
            water += 1;
            allowed = true;
        }

        // 4️⃣ Save updated state
        Map<String, String> updated = new HashMap<>();
        updated.put("water", String.valueOf(water));
        updated.put("lastLeakTime", String.valueOf(currentTime));

        redisTemplate.opsForHash().putAll(redisKey, updated);
        redisTemplate.expire(redisKey, 1, TimeUnit.HOURS);

        return allowed;
    }
}
