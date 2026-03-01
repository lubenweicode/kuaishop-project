package com.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 全局配置（解决JSON序列化/反序列化问题，适配List<自定义类>场景）
 * 核心：1. 避免泛型类型信息注入 2. 统一ObjectMapper实例 3. 解决常见序列化问题
 */
@Configuration
public class JacksonConfig {

    /**
     * 统一构建ObjectMapper（通过Spring官方Builder，避免重复实例）
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder
                // 禁用：空Bean序列化时抛出异常（比如实体类无字段的情况）
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 全局日期格式（统一返回给前端的日期格式）
                .simpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .build();

        // 解决Long类型前端精度丢失问题（比如商品ID、用户ID）
        SimpleModule customModule = new SimpleModule();
        // Long/long 类型序列化为字符串，避免前端解析成Number时精度丢失
        customModule.addSerializer(Long.class, ToStringSerializer.instance);
        customModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(customModule);

        return objectMapper;
    }
}