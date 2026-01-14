package com.controller;

import generator.domain.demo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping("/ping")
    public Result<String> ping(){
        log.info("ping方法被调用!!!");
        return Result.success();
    }

    @GetMapping("/exception/global")
    public Result<Void> testGlobalException(){
        log.info("全局异常处理器测试方法被调用!");
        return Result.success();
    }

    @GetMapping("/exception/argument")
    public Result<Void> testIllegalArgumentException(String id){
        log.info("参数错误测试方法被调用");
        if(id == null || id.isEmpty()){
            throw new IllegalArgumentException("id参数不能为空");
        }
        return Result.success();
    }
}
