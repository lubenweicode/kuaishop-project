package com.controller;

import com.utils.UserRateLimiter;
import generator.domain.demo.Result;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private UserRateLimiter userRateLimiter;

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

    @GetMapping("/exception/limiter")
    public Result<String> testLimiterException(HttpServletRequest request){
        log.info("限流测试方法被调用");
        if (!userRateLimiter.allowRequest(request)){
            return Result.error(429, "请求过于频繁，请稍后再试");
        }
        return Result.success("限流测试方法成功");
    }
}
