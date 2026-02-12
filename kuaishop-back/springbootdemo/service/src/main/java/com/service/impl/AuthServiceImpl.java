package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constant.Seckill.AdminConstants;
import com.repository.mapper.AuthMapper;
import com.service.AuthService;
import com.utils.IpUtil;
import com.utils.JwtUtil;
import com.utils.Sha256Util;
import generator.domain.auth.LoginAuthDTO;
import generator.domain.auth.RegisterAuthDTO;
import generator.domain.auth.RegisterAuthVO;
import generator.domain.auth.UserInfoVO;
import generator.domain.entity.User;
import generator.domain.response.Result;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AuthServiceImpl extends ServiceImpl<AuthMapper, User> implements AuthService {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final IdentityServiceImpl identityService;

    private final String CACHE_JWT_TOKEN = "jwt:";

    public AuthServiceImpl(JwtUtil jwtUtil, StringRedisTemplate redisTemplate, IdentityServiceImpl identityService) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.identityService = identityService;
    }
    /**
     * 注册
     *
     * @param registerAuthDTO
     * @return
     */
    @Override
    public Result<Object> register(RegisterAuthDTO registerAuthDTO) {

        // 1. 入参非空校验
        if (registerAuthDTO == null) {
            return Result.error(AdminConstants.PARAM_ERROR, AdminConstants.PARAM_ERROR_MESSAGE);
        }
        // 1.1 用户名非空校验
        if (registerAuthDTO.getUsername() == null || registerAuthDTO.getUsername().trim().isEmpty()) {
            return Result.error(AdminConstants.PARAM_ERROR, AdminConstants.PARAM_ERROR_USERNAME_NULL_MESSAGE);
        }
        // 1.2 密码非空校验
        if (registerAuthDTO.getPassword() == null || registerAuthDTO.getPassword().trim().isEmpty()) {
            return Result.error(AdminConstants.PARAM_ERROR, AdminConstants.PARAM_ERROR_PASSWORD_NULL_MESSAGE);
        }

        // 2. 数据库查询,校验唯一字段【正确写法：无嵌套OR，先判断非空再加条件】
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(User::getId, User::getUsername, User::getPhone, User::getEmail);

        // 1. 先加用户名条件（必填项，基础条件）
        lambdaQueryWrapper.eq(User::getUsername, registerAuthDTO.getUsername());

        // 2. 手机号非空时，添加 OR + 手机号条件
        if (StringUtils.hasText(registerAuthDTO.getPhone())) {
            lambdaQueryWrapper.or().eq(User::getPhone, registerAuthDTO.getPhone());
        }

        // 3. 邮箱非空时，添加 OR + 邮箱条件
        if (StringUtils.hasText(registerAuthDTO.getEmail())) {
            lambdaQueryWrapper.or().eq(User::getEmail, registerAuthDTO.getEmail());
        }

        // 查询数据库的用户列表(只有一条)
        List<User> existUserList = this.list(lambdaQueryWrapper);

        if (!existUserList.isEmpty()) {
            User existUser = existUserList.get(0);
            String reqPhone = registerAuthDTO.getPhone();
            String reqEmail = registerAuthDTO.getEmail();

            // 校验用户名重复（优先，因为用户名是必填）
            if (registerAuthDTO.getUsername().equals(existUser.getUsername())) {
                return Result.error(AdminConstants.USERNAME_EXIST, AdminConstants.USERNAME_EXIST_MESSAGE);
            }

            // 校验手机号重复（选填，传了才校验）
            if (StringUtils.hasText(reqPhone) && StringUtils.hasText(existUser.getPhone())) {
                if (reqPhone.equals(existUser.getPhone())) {
                    return Result.error(AdminConstants.PHONE_EXIST, AdminConstants.PHONE_EXIST_MESSAGE);
                }
            }

            // 校验邮箱重复（选填，传了才校验）
            if (StringUtils.hasText(reqEmail) && StringUtils.hasText(existUser.getEmail())) {
                if (reqEmail.equals(existUser.getEmail())) {
                    return Result.error(AdminConstants.EMAIL_EXIST, AdminConstants.EMAIL_EXIST_MESSAGE);
                }
            }
        }

        try {
            // 4. 密码加密
            String salt = Sha256Util.generateSalt();
            String pwd = Sha256Util.encrypt(registerAuthDTO.getPassword(),salt);

            // 5. 构建用户对象
            User user = new User();
            user.setUsername(registerAuthDTO.getUsername());
            user.setPassword(pwd);
            user.setEmail(registerAuthDTO.getEmail());
            user.setPhone(registerAuthDTO.getPhone());
            user.setSalt(salt);

            // 6. 保存用户
            boolean saveUser = this.save(user);
            if (!saveUser) {
                return Result.error(AdminConstants.REGISTER_FAILED, AdminConstants.REGISTER_FAILED_MESSAGE);
            }

            // 7. 封装返回结果
            RegisterAuthVO registerAuthVO = new RegisterAuthVO();
            BeanUtils.copyProperties(user, registerAuthVO);

            return Result.success(registerAuthVO);
        } catch (Exception e) {
            log.error("用户注册失败，用户名：{}，异常信息：", registerAuthDTO.getUsername(), e);
            if (e instanceof org.springframework.dao.DuplicateKeyException) {
                return Result.error(AdminConstants.USERNAME_PHONE_EMAIL_EXIST, AdminConstants.USERNAME_PHONE_EMAIL_EXIST_MESSAGE);
            }
            return Result.error(AdminConstants.REGISTER_FAILED, AdminConstants.REGISTER_FAILED_MESSAGE);
        }
    }

    /**
     * 登录
     *
     * @param loginAuthDTO
     * @return
     */
    @Override
    public Result<Map<String, Object>> login(LoginAuthDTO loginAuthDTO,String clientIp) {
        String username = loginAuthDTO.getUsername();
        String password = loginAuthDTO.getPassword();

        // 1. 入参非空校验
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Result.error(AdminConstants.PARAM_ERROR, AdminConstants.PARAM_ERROR_MESSAGE);
        }

        // 2. 第一步：先查询用户名是否存在（仅查用户名，不查密码）
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.select(User::getId, User::getUsername, User::getSalt)
                .eq(User::getUsername, username);
        User existUser = this.getOne(usernameWrapper);

        // 2.1 用户名不存在 → 直接返回错误，不触发次数限制
        if (existUser == null) {
            log.warn("登录失败：用户名{}不存在", username);
            return Result.error(400, "用户名或密码错误"); // 提示保持模糊，避免信息泄露
        }

        // 3. 检查账号是否被锁定(优先判断,避免无效密码校验)
        String lockKey = AdminConstants.LOGIN_LOCK_KEY_PREFIX + username;
        try {
            Boolean isLock = redisTemplate.hasKey(lockKey);
            if (Boolean.TRUE.equals(isLock)) {
                // 计算剩余锁定时间
                Long remainSeconds = redisTemplate.getExpire(lockKey);
                int remainMinutes = remainSeconds != null && remainSeconds > 0
                        ? (int) (remainSeconds / 60)
                        : 30;
                String lockMsg = String.format(AdminConstants.LOGIN_LOCKED_TIP, remainMinutes);
                return Result.error(403, lockMsg);
            }
        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败（检查锁定状态）：{}", e.getMessage());
        }

        // 4. 密码加密 + 用户校验
        String encryptedPwd = Sha256Util.encrypt(password, existUser.getSalt());
        LambdaQueryWrapper<User> pwdWrapper = new LambdaQueryWrapper<>();
        pwdWrapper.eq(User::getId, existUser.getId());
        pwdWrapper.eq(User::getPassword, encryptedPwd);
        boolean pwdCorrect = this.exists(pwdWrapper);


        // 5. 登录失败处理(累加次数+判断锁定)
        if (!pwdCorrect) {
            try {
                String failCountKey = AdminConstants.LOGIN_FAIL_COUNT_KEY_PREFIX + username;
                // 累加失败次数
                Long failCount = redisTemplate.opsForValue().increment(failCountKey, 1);
                // 第一次失败设置过期时间
                if (failCount != null && failCount == 1) {
                    redisTemplate.expire(failCountKey, AdminConstants.LOGIN_LOCK_DURATION, TimeUnit.SECONDS);
                }

                // 计算剩余可尝试次数
                int remainTimes = AdminConstants.MAX_LOGIN_FAIL_TIMES - (failCount != null ? failCount.intValue() : 0);
                if (remainTimes <= 0) {
                    // 达到最大失败次数,触发锁定
                    redisTemplate.opsForValue().set(lockKey, "1", AdminConstants.LOGIN_LOCK_DURATION, TimeUnit.SECONDS);
                    redisTemplate.delete(failCountKey);
                    return Result.error(403, AdminConstants.LOGIN_LOCKED_MSG);
                } else {
                    return Result.error(400, String.format("%s，剩余%d次尝试机会", AdminConstants.USER_PWD_ERROR_MSG, remainTimes));
                }
            } catch (RedisConnectionFailureException e) {
                log.error("Redis连接失败（登录失败次数累加）：{}", e.getMessage());
                return Result.error(400, AdminConstants.USER_PWD_ERROR_MSG);
            }
        }

        // 5. 登录成功处理
        try {
            String failCountKey = AdminConstants.LOGIN_FAIL_COUNT_KEY_PREFIX + username;
            redisTemplate.delete(failCountKey);
            redisTemplate.delete(lockKey);

            // 6. 生成JWT Token + 封装返回结果
            String token = jwtUtil.generateToken(String.valueOf(existUser.getId()));
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(existUser, userInfoVO);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userInfo", userInfoVO);

            // 7. Redis存储Token
            String cacheAccessToken = CACHE_JWT_TOKEN + existUser.getId() + ":" + "access";
            redisTemplate.opsForValue().set(cacheAccessToken, token, 10, TimeUnit.DAYS);
            String cacheRefreshToken = CACHE_JWT_TOKEN + existUser.getId() + ":" + "refresh";
            redisTemplate.opsForValue().set(cacheRefreshToken, token, 30, TimeUnit.DAYS);

            log.info("用户登录成功，用户ID：{}", existUser.getId());

            // 8. 更新用户登录信息
            // 8.1 获取客户端真实IP
            // 8.2 获取当前时间
            LocalDateTime loginTime = LocalDateTime.now();
            // 8.3 更新用户表的最后登录时间和IP
            User updateUser = new User();
            updateUser.setId(existUser.getId());
            updateUser.setLastLoginTime(loginTime);
            updateUser.setLastLoginIp(clientIp);
            this.updateById(updateUser);

            return Result.success(result);

        } catch (RedisConnectionFailureException e) {
            log.error("Redis连接失败（存储Token）：{}", e.getMessage());
            return Result.error(500, "Redis连接失败，请稍后重试");
        } catch (BeansException e) {
            log.error("Bean拷贝异常（用户信息封装）：{}", e.getMessage());
            return Result.error(500, "系统异常，请稍后重试");
        } catch (Exception e) {
            log.error("用户{}登录异常：{}", username, e.getMessage());
            return Result.error(500, "登录失败，请稍后重试");
        }
    }

    /**
     * 获取当前用户信息
     *
     * @param authorization
     * @return
     */
    @Override
    public Result<UserInfoVO> getCurrentUser(String authorization) {

        // 1.校验Token是否存在
        if (authorization == null || !authorization.startsWith("Bearer ")) {
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
        lambdaQueryWrapper.eq(User::getId, userId);
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
