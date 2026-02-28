package com.urlshortener.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.urlshortener.components.DelegatingRateLimiter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class UrlInterceptor implements HandlerInterceptor {
	private final DelegatingRateLimiter rateLimiter;

	public UrlInterceptor(DelegatingRateLimiter rateLimiter) {
		this.rateLimiter = rateLimiter;
	}
	
    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isEmpty()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String ip=getClientIp(request);

		System.out.println("????????????????????????????????????==="+ip);
		
		if (!rateLimiter.allowRequest(ip)) {
			response.sendError(429, "Too many requests");
			return false;
		}

		return true;
	}
}
