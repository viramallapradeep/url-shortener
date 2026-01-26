package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urlshortener.util.SnowflakeIdGenerator;

@Configuration
public class SnowflakeConfig {

    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(1); // machineId
    }
}
