package com.config;

import com.interceptor.UserRateLimitInterceptor;
import com.properties.RateLimitProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserRateLimitInterceptor userRateLimitInterceptor;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userRateLimitInterceptor)
                .addPathPatterns(rateLimitProperties.getInterceptor().getIncludePaths())
                .excludePathPatterns(rateLimitProperties.getInterceptor().getExcludePaths());
    }
}
