package com.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单个用户10秒内最多5次请求的限流工具类
 */
@Slf4j
@Component
public class UserRateLimiter {
    // 存储用户-接口的请求记录：key=用户标识+接口路径，value=该用户在该接口的请求时间列表
    private final ConcurrentHashMap<String, List<Long>> userRequestMap = new ConcurrentHashMap<>();

    // 限流规则：10秒内最多5次请求
    private static final int MAX_REQUESTS = 5; // 最大请求数
    private static final long TIME_WINDOW = 10 * 1000; // 时间窗口：10秒（毫秒）

    private final JwtUtil jwtUtil;

    public UserRateLimiter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 判断单个用户是否允许请求
     * @param request HTTP请求（用于获取用户标识和接口路径）
     * @return true=允许，false=限流
     */
    public boolean allowRequest(HttpServletRequest request) {
        // 1. 获取用户唯一标识（优先用户ID，无则用IP）
        String userId = getUserIdFromRequest(request);
        // 2. 获取请求接口路径
        String path = request.getRequestURI();
        // 3. 构建限流Key：用户标识 + 接口路径（确保不同用户/接口独立计数）
        String limitKey = userId + ":" + path;

        // 4. 线程安全地处理请求记录
        long now = System.currentTimeMillis();
        userRequestMap.compute(limitKey, (key, requestTimes) -> {
            if (requestTimes == null) {
                // 首次请求：初始化列表，添加当前时间
                List<Long> newTimes = new ArrayList<>();
                newTimes.add(now);
                return newTimes;
            }

            // 5. 清理时间窗口外的过期请求记录（滑动窗口核心）
            Iterator<Long> iterator = requestTimes.iterator();
            while (iterator.hasNext()) {
                Long time = iterator.next();
                if (now - time > TIME_WINDOW) {
                    iterator.remove(); // 删除10秒前的请求记录
                }
            }

            // 6. 添加当前请求时间
            requestTimes.add(now);
            return requestTimes;
        });

        // 7. 判断当前窗口内请求数是否超过阈值
        List<Long> currentRequestTimes = userRequestMap.get(limitKey);
        boolean allowed = currentRequestTimes.size() <= MAX_REQUESTS;

        // 8. 记录限流日志（可选）
        if (!allowed) {
            log.warn("用户限流触发 | 用户标识: {} | 接口: {} | 10秒内请求数: {}",
                    userId, path, currentRequestTimes.size());
        }
        return allowed;
    }

    /**
     * 从请求中获取用户标识（核心：适配登录/未登录场景）
     * 登录用户：从Token/请求头获取userId；未登录用户：用IP作为标识
     */
    private String getUserIdFromRequest(HttpServletRequest request) {
        // 场景1：登录用户 - 从请求头获取Token，解析出userId（替换为你的实际逻辑）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String userId = parseUserIdFromToken(token.substring(7));
            if (userId != null) {
                return userId;
            }
        }

        // 场景2：未登录用户 - 用IP作为标识（兼容X-Forwarded-For代理场景）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多IP场景（X-Forwarded-For可能有多个IP，取第一个）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip == null ? "unknown" : ip;
    }

    /**
     * 从Token解析用户ID
     */
    private String parseUserIdFromToken(String token) {
        try {
            return jwtUtil.getUserIdFromToken(token);// 未登录时返回null，走IP标识逻辑
        } catch (Exception e) {
            // Token无效或已过期时返回null，走IP标识逻辑
            return null;
        }
    }

    /**
     * 扩展方法：手动清理某个用户的限流记录（比如用户登出时）
     */
    public void clearUserLimit(String userId) {
        Iterator<String> iterator = userRequestMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.startsWith(userId + ":")) {
                iterator.remove();
            }
        }
    }
}