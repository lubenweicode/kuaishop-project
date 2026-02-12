package com.controller;

import com.service.AuthService;
import com.utils.IpUtil;
import generator.domain.auth.LoginAuthDTO;
import generator.domain.auth.RegisterAuthDTO;
import generator.domain.auth.UserInfoVO;
import generator.domain.response.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 注册
     *
     * @param registerAuthDTO 注册参数
     * @return
     */
    @PostMapping("/register")
    public Result<Object> register(@Valid @RequestBody RegisterAuthDTO registerAuthDTO) {
        return authService.register(registerAuthDTO);
    }

    /**
     * 登录
     *
     * @param loginAuthDTO 登录参数
     * @return
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginAuthDTO loginAuthDTO, HttpServletRequest  request) {
        String clientIp = IpUtil.getClientIp(request);
        return authService.login(loginAuthDTO, clientIp);
    }

    /**
     * 获取当前用户信息
     *
     * @param authorization 授权信息
     * @return
     */
    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return authService.getCurrentUser(authorization);
    }
}
