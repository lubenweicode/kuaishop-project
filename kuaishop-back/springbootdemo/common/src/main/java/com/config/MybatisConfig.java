package com.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * MyBatis全局配置类 - 注册所有JSON类型处理器
 * 解决Map/List类型JSON字段的序列化/反序列化问题
 */
@Configuration
public class MybatisConfig {

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> {
            // 注册Map类型处理器（处理JSON对象，如specifications字段）
            configuration.getTypeHandlerRegistry().register(Map.class, JacksonTypeHandler.class);
            // 注册List类型处理器（处理JSON数组，如images字段）
            configuration.getTypeHandlerRegistry().register(List.class, JacksonTypeHandler.class);
        };
    }
}