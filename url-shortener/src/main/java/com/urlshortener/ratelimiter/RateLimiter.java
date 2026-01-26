package com.urlshortener.ratelimiter;

public interface RateLimiter {
 public boolean isAllowed(String ip);
}
