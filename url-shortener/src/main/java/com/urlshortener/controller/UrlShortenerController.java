package com.urlshortener.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.service.RateLimiterService;
import com.urlshortener.service.UrlShortenerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortService;
    private final RateLimiterService ratelimService;

    public UrlShortenerController(UrlShortenerService service,RateLimiterService ratelimService) {
        this.urlShortService = service;
        this.ratelimService=ratelimService;
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
    	String ipAdd=request.getRemoteAddr();
    	if(!ratelimService.isAllowed(ipAdd)) {
    		return ResponseEntity.status(429).build();
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
