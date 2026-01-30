package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.AuthMapper;
import com.service.AuthService;
import com.utils.JwtUtil;
import com.utils.Sha256Util;
import generator.domain.entity.User;
import generator.domain.auth.*;
import generator.domain.demo.Result;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final Sha256Util sha256Util;

    private final String CACHE_JWT_TOKEN = "jwt:";

    public AuthServiceImpl(JwtUtil jwtUtil, StringRedisTemplate redisTemplate, Sha256Util sha256Util) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.sha256Util = sha256Util;
    }

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

        // 密码加密
        String pwd = Sha256Util.encrypt(registerAuthDTO.getPassword());

        User user = new User();
        user.setUsername(registerAuthDTO.getUsername());
        user.setPassword(pwd);
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

        String pwd = Sha256Util.encrypt(loginAuthDTO.getPassword());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getUsername,loginAuthDTO.getUsername());
        lambdaQueryWrapper.eq(User::getPassword,pwd);

        boolean validUser = this.exists(lambdaQueryWrapper);

        if(!validUser){
            return Result.error(400,"用户名或密码错误");
        }

        User user = this.getOne(lambdaQueryWrapper);

        Map<String,Object> result = null;
        try {
            String token = jwtUtil.generateToken(String.valueOf(user.getId()));
            LoginAuthVO loginAuthVO = new LoginAuthVO();
            BeanUtils.copyProperties(user,loginAuthVO);

            result = new HashMap<>();
            result.put("token",token);
            result.put("userInfo",loginAuthVO);

            // 将用户token写入Redis
            String cacheAccessToken = CACHE_JWT_TOKEN + user.getId()+":"+"access";
            redisTemplate.opsForValue().set(cacheAccessToken,token,10, TimeUnit.DAYS);
            String cacheRefreshToken = CACHE_JWT_TOKEN + user.getId()+":"+"refresh";
            redisTemplate.opsForValue().set(cacheRefreshToken,token,30, TimeUnit.DAYS);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败：{}", e.getMessage());
            return Result.error(500,"Redis连接失败，请稍后重试");
        } catch (BeansException e){
            log.error("Bean异常：{}", e.getMessage());
            return Result.error(500,"Bean异常，请稍后重试");
        }
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
