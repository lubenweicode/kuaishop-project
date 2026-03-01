package com.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户级令牌桶限流工具
 */
@Component
public class SeckillRateLimiter {

    // 存储每个用户的令牌桶,key：用户ID,value：令牌桶 (每秒1个令牌,限制用户高频下单)
    private static final ConcurrentHashMap<Long, RateLimiter> USER_RATE_LIMITER = new ConcurrentHashMap<>();

    /**
     * 获取令牌桶
     *
     * @param userId 用户ID
     * @return 令牌桶
     */
    public RateLimiter getRateLimiter(Long userId) {
        return USER_RATE_LIMITER.computeIfAbsent(userId, k -> RateLimiter.create(1.0));
    }

    /**
     * 尝试获取令牌
     *
     * @param userId 用户ID
     * @return 是否获取成功
     */
    public boolean tryAcquire(Long userId) {
        return getRateLimiter(userId).tryAcquire();
    }
}
