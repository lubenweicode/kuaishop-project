package com.interceptor;

import com.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * JWT拦截器：拦截请求并校验令牌
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 读取JWT拦截器开关配置
    @Value("${jwt.interceptor.enable:true}")
    private boolean jwtInterceptorEnable;

    private final JwtUtil jwtUtil;

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 请求处理前执行（核心校验逻辑）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!jwtInterceptorEnable) {
            return true;
        }

        // 1. 跳过不需要校验的接口（如登录、注册、静态资源）
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/login") || requestURI.contains("/register") || requestURI.contains("/public")) {
            return true; // 放行
        }

        // 2. 从请求头中获取JWT令牌（通常令牌放在Authorization头中，格式：Bearer <token>）
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 没有携带令牌，返回401未授权
            return unauthorizedResponse(response, "请携带有效的JWT令牌");
        }

        // 3. 提取令牌（去掉前缀"Bearer "）
        String token = authorization.substring(7);

        // 4. 校验令牌合法性
        if (!jwtUtil.validateToken(token)) {
            // 令牌无效，返回401未授权
            return unauthorizedResponse(response, "JWT令牌无效或已过期");
        }

        // 5. 从令牌中获取用户ID
        String userId = jwtUtil.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        // 6. 放行请求
        return true;
    }

    /**
     * 构建未授权的响应
     * @param response 响应对象
     * @param message 错误提示
     * @return false（拦截请求）
     */
    private boolean unauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 状态码
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":401,\"message\":\"" + message + "\"}");
        writer.flush();
        writer.close();
        return false;
    }
}