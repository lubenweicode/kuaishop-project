package com.config;

import com.interceptor.JwtInterceptor;
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

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userRateLimitInterceptor)
                .addPathPatterns(rateLimitProperties.getInterceptor().getIncludePaths())
                .excludePathPatterns(rateLimitProperties.getInterceptor().getExcludePaths());
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/public/**");
                // 添加其他拦截器
    }
}
