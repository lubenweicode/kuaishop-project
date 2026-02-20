package com.interceptor;

import com.utils.UserRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * 用户限流拦截器
 */
@Component
public class UserRateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRateLimiter userRateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 判断是否允许请求
        boolean allowed = userRateLimiter.allowRequest(request);

        // 2. 限流触发：返回429响应（友好提示）
        if (!allowed) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429状态码
            PrintWriter writer = response.getWriter();
            writer.write("{\"code\":429,\"message\":\"单个用户10秒内最多请求5次，请稍后重试\",\"data\":null}");
            writer.flush();
            writer.close();
            return false; // 拒绝请求
        }

        // 3. 允许请求：放行
        return true;
    }
}