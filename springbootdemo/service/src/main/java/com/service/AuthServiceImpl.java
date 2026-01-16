package com.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.AuthMapper;
import com.utils.JwtUtil;
import generator.domain.Entity.User;
import generator.domain.auth.*;
import generator.domain.demo.Result;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService{

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 注册
     * @param registerAuthDTO
     * @return
     */
    @Override
    public Result<Object> register(RegisterAuthDTO registerAuthDTO) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,registerAuthDTO.getUsername());

        boolean existUser = this.exists(lambdaQueryWrapper);

        if(existUser){
            return Result.error(400,"用户名已存在");
        }

        User user = new User();
        user.setUsername(registerAuthDTO.getUsername());
        user.setPassword(registerAuthDTO.getPassword());
        user.setEmail(registerAuthDTO.getEmail());
        user.setPhone(registerAuthDTO.getPhone());

        boolean saveUser = this.save(user);
        if(!saveUser){
            return Result.error(500,"注册失败,请稍后重试");
        }

        LambdaQueryWrapper<User> registerSuccessWrapper = new LambdaQueryWrapper<>();
        registerSuccessWrapper.eq(User::getUsername,registerAuthDTO.getUsername());
        User registerUser = this.getOne(registerSuccessWrapper);
        RegisterAuthVO registerAuthVO = new RegisterAuthVO();
        BeanUtils.copyProperties(registerUser,registerAuthVO);
        return Result.success(registerAuthVO);
    }

    /**
     * 登录
     * @param loginAuthDTO
     * @return
     */
    @Override
    public Result<Map<String,Object>> login(LoginAuthDTO loginAuthDTO) {

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,loginAuthDTO.getUsername());
        lambdaQueryWrapper.eq(User::getPassword,loginAuthDTO.getPassword());

        boolean validUser = this.exists(lambdaQueryWrapper);

        if(!validUser){
            return Result.error(400,"用户名或密码错误");
        }

        User user = this.getOne(lambdaQueryWrapper);

        String token = jwtUtil.generateToken(String.valueOf(user.getId()));
        LoginAuthVO loginAuthVO = new LoginAuthVO();
        BeanUtils.copyProperties(user,loginAuthVO);

        Map<String,Object> result = new HashMap<>();
        result.put("token",token);
        result.put("userInfo",loginAuthVO);
        return Result.success(result);
    }

    /**
     * 获取当前用户信息
     * @param authorization
     * @return
     */
    @Override
    public Result<UserInfoVO> getCurrentUser(String authorization) {

        // 1.校验Token是否存在
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.warn("获取当前用户信息失败：请求头无有效Token");
            return Result.error(401, "未登录，请先登录");
        }

        // 2. 提取纯Token字符串(去掉前缀"Bearer ")
        String token = authorization.substring(7).trim();

        // 3. 验证Token有效性
        if (!jwtUtil.validateToken(token)) {
            log.warn("获取当前用户信息失败：Token无效或已过期");
            return Result.error(401, "登录已过期，请重新登录");
        }

        // 4. 解析Token获取用户ID
        Claims claims = jwtUtil.parseToken(token);
        String userIdStr = claims.getSubject(); // 对应生成Token时的user.getId()
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.error("解析用户ID失败：{}", userIdStr, e);
            return Result.error(401, "Token解析失败");
        }

        // 5. 查询用户详情(从数据库获取完整信息)
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId,userId);
        User user = this.getOne(lambdaQueryWrapper);
        if (user == null) {
            log.warn("用户ID{}不存在", userId);
            return Result.error(404, "用户不存在");
        }

        // 6. 封装用户信息VO（屏蔽敏感字段：密码、盐值等）
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        log.info("用户ID{}获取个人信息成功", userId);
        return Result.success(userInfoVO);
    }
}
