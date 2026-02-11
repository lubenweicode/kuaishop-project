package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.auth.LoginAuthDTO;
import generator.domain.auth.RegisterAuthDTO;
import generator.domain.auth.UserInfoVO;
import generator.domain.entity.User;
import generator.domain.response.Result;

import java.util.Map;

public interface AuthService extends IService<User> {
    Result<Object> register(RegisterAuthDTO registerAuthDTO);

    Result<Map<String, Object>> login(LoginAuthDTO loginAuthDTO);

    Result<UserInfoVO> getCurrentUser(String authorization);
}
