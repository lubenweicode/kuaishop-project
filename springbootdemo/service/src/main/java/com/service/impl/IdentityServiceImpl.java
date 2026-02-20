
package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.repository.mapper.AuthMapper;
import generator.domain.entity.User;
import org.springframework.stereotype.Service;

/**
 * 身份判断服务类（业务层，可正常依赖Mapper）
 */
@Service
public class IdentityServiceImpl {

    private final AuthMapper authMapper;

    // 构造器注入（Spring 推荐方式）
    public IdentityServiceImpl(AuthMapper authMapper) {
        this.authMapper = authMapper;
    }

    /**
     * 确认用户是否为管理员
     *
     * @param userId 用户ID
     * @return true: 是管理员，false: 不是管理员
     */
    public Boolean confirmIdentity(Long userId) {
        // 空值校验（增强代码健壮性）
        if (userId == null) {
            return false;
        }

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, userId);
        User user = authMapper.selectOne(lambdaQueryWrapper);

        // 防止user为null导致空指针
        return user != null && user.getIdentity() == 1;
    }
}