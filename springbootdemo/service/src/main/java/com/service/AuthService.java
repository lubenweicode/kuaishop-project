package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import generator.domain.Entity.User;
import generator.domain.auth.LoginAuthDTO;
import generator.domain.auth.RegisterAuthDTO;
import generator.domain.auth.UserInfoVO;
import generator.domain.demo.Result;

import java.util.Map;

public interface AuthService extends IService<User>{
    Result<Object> register(RegisterAuthDTO registerAuthDTO);

    Result<Map<String,Object>> login(LoginAuthDTO loginAuthDTO);

    Result<UserInfoVO> getCurrentUser(String authorization);
}
