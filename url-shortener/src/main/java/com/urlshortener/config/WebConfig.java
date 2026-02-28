package com.urlshortener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.urlshortener.interceptor.UrlInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UrlInterceptor interceptor;

    public WebConfig(UrlInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/goto/**");
    }
}