package com.urlshortener.ratelimiter;

import java.util.List;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component
public class TokenBucketRateLimiter{

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> luaScript;

    public TokenBucketRateLimiter(StringRedisTemplate redisTemplate,
            DefaultRedisScript<Long> tokenBucketScript) {
    		this.redisTemplate = redisTemplate;
    		this.luaScript = tokenBucketScript;
}

    public boolean isAllowed(String key, int capacity, int refillRate) {
        long now = System.currentTimeMillis() / 1000;

        try {
            Long result = redisTemplate.execute(
                    luaScript,
                    List.of(key),
                    String.valueOf(capacity),
                    String.valueOf(refillRate),
                    String.valueOf(now)
            );
            return result != null && result == 1;
        } catch (Exception e) {
            // Redis down → fail-open
            return true;
        }
    }

}
