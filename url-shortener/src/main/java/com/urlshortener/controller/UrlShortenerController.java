package com.urlshortener.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.ratelimiter.TokenBucketRateLimiter;
import com.urlshortener.service.UrlShortenerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortService;
  //  private final RateLimiterService ratelimService;
    private final TokenBucketRateLimiter tokBucLimiter;
    
    

    public UrlShortenerController(UrlShortenerService service,TokenBucketRateLimiter tokBucLimiter) {
        this.urlShortService = service;
        this.tokBucLimiter=tokBucLimiter;
    }

    // Create short URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        UrlMapping mapping = urlShortService.shortenUrl(longUrl);
        return ResponseEntity.ok(mapping.getShortKey());
    }

    // Redirect to long URL
    @GetMapping("/goto/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey,HttpServletRequest request) {
    	String ip=request.getRemoteAddr();
    	try {
    		// 2️ Per-IP rate limit
    		if (!tokBucLimiter.isAllowed("tb:ip:" + ip, 1, 1)) {
    			System.out.println("====IP RATE LIMIT HIT for " + ip + "====");
    			return ResponseEntity.status(429).build();
    		}
    		
    		// 1️ Global rate limit
	    	if(!tokBucLimiter.isAllowed("tb:global",1,1)) {
	    		System.out.println("====too many requests====");
	    		return ResponseEntity.status(429).build();
	    	}
	    	
	    	
    	}catch(Exception e){
    		//ignore this
    	}
    	
        String longUrl = urlShortService.getLongUrl(shortKey);
        if(longUrl==null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity
                .status(302)
                .location(URI.create(longUrl))
                .build();
    }
}
