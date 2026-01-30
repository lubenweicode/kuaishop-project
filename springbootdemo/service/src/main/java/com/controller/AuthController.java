package com.controller;

import com.service.AuthService;
import generator.domain.auth.LoginAuthDTO;
import generator.domain.auth.RegisterAuthDTO;
import generator.domain.auth.UserInfoVO;
import generator.domain.demo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result<Object> register(@RequestBody RegisterAuthDTO registerAuthDTO){
        return authService.register(registerAuthDTO);
    }

    @PostMapping("/login")
    public Result<Map<String,Object>> login(@RequestBody LoginAuthDTO loginAuthDTO){
        return authService.login(loginAuthDTO);
    }

    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization){
        return authService.getCurrentUser(authorization);
    }
}
