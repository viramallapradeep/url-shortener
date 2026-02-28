package com.urlshortener.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.components.DelegatingRateLimiter;

@RestController
@RequestMapping("/admin/ratelimiter")
public class RateLimiterAdminController {
	  private final DelegatingRateLimiter delegatingLimiter;

	    public RateLimiterAdminController(DelegatingRateLimiter delegatingLimiter) {
	        this.delegatingLimiter = delegatingLimiter;
	    }

	    @PostMapping("/switch")
	    public String switchLimiter(@RequestParam String type) {
	        delegatingLimiter.switchLimiter(type);
	        return "Switched to " + type;
	    }
}