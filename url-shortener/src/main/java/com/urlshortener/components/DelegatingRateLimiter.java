package com.urlshortener.components;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.urlshortener.ratelimiter.RateLimiter;

@Component
public class DelegatingRateLimiter implements RateLimiter {
	private final Map<String, RateLimiter> limiters;

	private volatile RateLimiter currentLimiter;

	public DelegatingRateLimiter(Map<String, RateLimiter> limiters) {
		this.limiters = limiters;

		// default Redis implementation
		System.out.println("======leakyBucket======");
		this.currentLimiter = limiters.get("leakyBucket");
	}

	public void switchLimiter(String type) {

		RateLimiter newLimiter = limiters.get(type);

		if (newLimiter == null) {
			throw new IllegalArgumentException("Invalid limiter type");
		}

		this.currentLimiter = newLimiter;
	}

	@Override
	public boolean allowRequest(String key) {
		return currentLimiter.allowRequest(key);
	}
}