package utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mapper.AuthMapper;
import generator.domain.entity.User;
import org.springframework.stereotype.Component;

/**
 * 身份工具类
 */
@Component
public class IdentityUtil {

    private final AuthMapper authMapper;

    public IdentityUtil(AuthMapper authMapper) {
        this.authMapper = authMapper;
    }

    /**
     * 确认用户身份
     * @param userId
     * @return true: 是管理员
     * false: 不是管理员
     */
    public Boolean confirmIdentity(Long userId){

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getId, userId);
        User user = authMapper.selectOne(lambdaQueryWrapper);
        return user.getIdentity() == 1;
    }
}
