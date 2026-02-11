package com.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 扫描所有模块的组件（覆盖 com/XLin.com 前缀）
@SpringBootApplication(scanBasePackages = {"com"})
// 扫描 repository 模块的 Mapper
@MapperScan("com.repository.mapper")
public class SpringBootDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}