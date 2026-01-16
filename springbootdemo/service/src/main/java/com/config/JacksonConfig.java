package com.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        // 全局禁用空bean序列化失败
        builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        // 配置日期格式（可选）
        builder.simpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return builder;
    }
}