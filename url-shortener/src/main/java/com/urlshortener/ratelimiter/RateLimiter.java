package com.urlshortener.ratelimiter;

public interface RateLimiter {
	boolean allowRequest(String key);
}