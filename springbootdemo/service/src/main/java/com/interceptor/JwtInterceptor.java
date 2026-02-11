package interceptor;

import com.alibaba.fastjson.JSONObject;
import utils.JwtUtil;
import generator.domain.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * JWT拦截器：拦截请求并校验令牌
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private static final String REDIS_JWT_KEY_PREFIX = "jwt:%d:access";

    // 读取JWT拦截器开关配置
    @Value("${jwt.interceptor.enable:true}")
    private boolean jwtInterceptorEnable;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public JwtInterceptor(JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }


    /**
     * 请求处理前执行（核心校验逻辑）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        if (!jwtInterceptorEnable) {
            log.info("JWT拦截器已禁用，所有请求都允许通过");
            return true;
        }

        // 1. 跳过不需要校验的接口（如登录、注册、静态资源）
        requestURI = request.getRequestURI();
        if (requestURI.contains("/login") || requestURI.contains("/register") || requestURI.contains("/public")) {
            log.info("请求URI：{} 不需要JWT拦截，放行", requestURI);
            return true; // 放行
        }

        // 2. 从请求头中获取JWT令牌（通常令牌放在Authorization头中，格式：Bearer <token>）
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 没有携带令牌，返回401未授权
            log.warn("请求URI：{} 未携带有效Token，Authorization头：{}", requestURI, authorization);
            return unauthorizedResponse(response, "请携带有效的JWT令牌");
        }

        // 3. 提取令牌（去掉前缀"Bearer "）
        String token = authorization.substring(7).trim();
        String tokenMask = maskToken( token);
        try {
            if (token == null || token.isEmpty()) {
                log.warn("请求URI：{} 携带的Token无效，请检查Token格式", requestURI);
                return unauthorizedResponse(response, "JWT令牌不能为空");
            }

            // 4. 校验令牌合法性
            if (!jwtUtil.validateToken(token)) {
                log.warn("请求URI：{} 携带的Token无效，请检查Token格式", requestURI);
                return unauthorizedResponse(response, "JWT令牌无效或已过期");
            }

            // 5. 从令牌中获取用户ID
            String userIdStr = jwtUtil.getUserIdFromToken(token);

            if(userIdStr == null || userIdStr.isEmpty()){
                log.warn("请求URI：{} 的Token中未包含有效用户ID，Token：{}", requestURI, tokenMask);
                return unauthorizedResponse(response, "JWT令牌中未包含有效用户ID");
            }

            // 6. 转换用户ID为Long类型
            Long userId;
            try {
                userId = Long.parseLong(userIdStr);
            } catch (Exception e) {
                log.warn("请求URI：{} 的Token中用户ID转换失败，Token：{}", requestURI, tokenMask);
                return unauthorizedResponse(response, "JWT令牌中用户ID转换失败");
            }

            // 拼接Redis Key: jwt:{userId}:access
            String redisKey = String.format(REDIS_JWT_KEY_PREFIX, userId);
            // 从Redis读取该用户的有效Token
            String redisToken = redisTemplate.opsForValue().get(redisKey);

            if(redisToken == null){
                log.warn("请求URI：{} 的Token已失效，请重新登录", requestURI);
                return unauthorizedResponse(response, "JWT令牌已失效，请重新登录");
            }
            if(!redisToken.equals(token)){
                log.warn("用户{}的前端Token与RedisToken不匹配", userId);
                return unauthorizedResponse(response, "用户Token与RedisToken不匹配");
            }

            UserContext.setUserId(userId);
            log.info("请求URI：{} 携带的Token有效，用户ID：{}", requestURI, userId);
            // 6. 放行请求
            return true;
        } catch (Exception e) {
            log.error("请求URI：{} 携带的Token验证异常：{}", requestURI, e.getMessage(), e);
            return unauthorizedResponse(response, "JWT令牌验证异常");
        }
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
        // 用JSON工具类构建响应，自动转义特殊字符
        try(PrintWriter writer = response.getWriter()){
            JSONObject result = new JSONObject();
            result.put("code", 401);
            result.put("message", message);
            result.put("timestamp", System.currentTimeMillis()); // 补充时间戳，适配接口文档规范
            writer.write(result.toString());
            writer.flush();
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求处理完成后，清空用户ID
        try {
            UserContext.clear();
            log.debug("清理用户上下文,请求URL:{}",request.getRequestURI());
        } catch (Exception e) {
            log.error("清理用户上下文异常：{}", e.getMessage(), e);
        }
    }

    private String maskToken(String token){
        if(token == null || token.length() <= 10){
            return "*********";
        }
        return token.substring(0, 5) + "*****" + token.substring(token.length() - 5);
    }
}