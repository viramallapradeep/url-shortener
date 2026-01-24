package com.urlshortener.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.entity.UrlMapping;
import com.urlshortener.service.UrlShortenerService;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService service;

    public UrlShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    // Create short URL
    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String longUrl) {
        UrlMapping mapping = service.shortenUrl(longUrl);
        return ResponseEntity.ok(mapping.getShortKey());
    }

    // Redirect to long URL
    @GetMapping("/goto/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {
        UrlMapping mapping = service.getLongUrl(shortKey);
        return ResponseEntity
                .status(302)
                .location(URI.create(mapping.getLongUrl()))
                .build();
    }
}
