package com.urlshortener.entity;

public class RateLimiterRequest {

    private String type;

    private Integer maxRequests;
    private Long windowSizeMs;

    private Integer capacity;
    private Integer refillRate;
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getMaxRequests() {
		return maxRequests;
	}
	public void setMaxRequests(Integer maxRequests) {
		this.maxRequests = maxRequests;
	}
	public Long getWindowSizeMs() {
		return windowSizeMs;
	}
	public void setWindowSizeMs(Long windowSizeMs) {
		this.windowSizeMs = windowSizeMs;
	}
	public Integer getCapacity() {
		return capacity;
	}
	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
	public Integer getRefillRate() {
		return refillRate;
	}
	public void setRefillRate(Integer refillRate) {
		this.refillRate = refillRate;
	}

   
}