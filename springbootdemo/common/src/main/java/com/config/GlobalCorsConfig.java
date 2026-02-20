package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        // 1. 创建 CORS 配置对象
        CorsConfiguration config = new CorsConfiguration();
        // 允许的跨域来源（* 表示允许所有，也可以指定为前端地址：http://localhost:5173）
        config.addAllowedOrigin("http://localhost:5173");
        // 允许的请求头（* 表示所有）
        config.addAllowedHeader("*");
        // 允许的请求方法（GET/POST/PUT/DELETE 等，* 表示所有）
        config.addAllowedMethod("*");
        // 允许携带 Cookie（如果前端需要传 token 到 Cookie 里，必须开启）
        config.setAllowCredentials(true);
        // 预检请求的有效期（秒），避免频繁发 OPTIONS 请求
        config.setMaxAge(3600L);

        // 2. 配置生效的 URL 路径（/** 表示所有接口）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // 3. 返回 CORS 过滤器
        return new CorsFilter(source);
    }
}