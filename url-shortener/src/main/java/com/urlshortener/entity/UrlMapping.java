package com.urlshortener.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "url_mapping")
public class UrlMapping {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;	

@Column(name = "short_key", nullable = false, unique = true, length = 10)
private String shortKey;

@Column(name = "long_url",nullable = false,length = 2048)
private String longUrl;

@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;

protected UrlMapping() {}

public UrlMapping(String shortKey, String longUrl) {
    this.shortKey = shortKey;
    this.longUrl = longUrl;
    this.createdAt = LocalDateTime.now();
}

public Long getId() {
    return id;
}

public String getShortKey() {
    return shortKey;
}

public String getLongUrl() {
    return longUrl;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}
	
}
