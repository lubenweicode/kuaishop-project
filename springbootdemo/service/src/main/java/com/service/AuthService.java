package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import domain.auth.LoginAuthDTO;
import domain.auth.RegisterAuthDTO;
import domain.auth.UserInfoVO;
import domain.entity.User;
import domain.response.Result;

import java.util.Map;

public interface AuthService extends IService<User> {
    Result<Object> register(RegisterAuthDTO registerAuthDTO);

    Result<Map<String, Object>> login(LoginAuthDTO loginAuthDTO, String clientIp);

    Result<UserInfoVO> getCurrentUser(String authorization);
}
