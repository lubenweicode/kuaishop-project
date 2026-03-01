package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有接口跨域
        registry.addMapping("/**")
                // 关键：允许前端源（Spring Boot 3.x 用 allowedOriginPatterns 替代 allowedOrigins）
                .allowedOriginPatterns("*")
                // 允许所有请求方法（GET/POST/PUT/DELETE/OPTIONS）
                .allowedMethods("*")
                // 允许所有请求头（Token、Content-Type 等）
                .allowedHeaders("*")
                // 允许携带 Cookie/Token 等凭证
                .allowCredentials(true)
                // 预检请求有效期（3600秒，避免频繁发 OPTIONS 请求）
                .maxAge(3600);
    }
}