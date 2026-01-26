package com.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 * 支持生成token、解析token、验证token、获取token中的信息等
 */
@Component
@Slf4j
public class JwtUtil {

    // 从配置文件注入JWT过期时间（毫秒）
    @Value("${jwt.expire}")
    private Long jwtExpire;

    // 从配置文件注入JWT签名密钥
    @Value("${jwt.sign-key}")
    private String jwtSignKey;

    /**
     * 生成JWT Token
     * @param userId  用户ID（自定义有效载荷）
     * @param claims  自定义扩展载荷（如角色、权限等）
     * @return        生成的token字符串
     */
    public String generateToken(String userId, Map<String, Object> claims) {
        // 创建签名密钥（确保密钥长度足够，不足时自动补全）
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSignKey.getBytes(StandardCharsets.UTF_8));

        // 计算token过期时间
        Date expireDate = new Date(System.currentTimeMillis() + jwtExpire);

        // 构建并生成token
        return Jwts.builder()
                // 自定义载荷（必选：用户ID）
                .setSubject(userId)
                // 自定义扩展载荷
                .addClaims(claims)
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
                .setExpiration(expireDate)
                // 签名算法和密钥
                .signWith(secretKey, SignatureAlgorithm.HS256)
                // 构建token
                .compact();
    }

    /**
     * 生成token（无扩展载荷）
     * @param userId 用户ID
     * @return       token字符串
     */
    public String generateToken(String userId) {
        return generateToken(userId, Map.of());
    }

    /**
     * 解析Token，获取JWT的Payload部分
     * @param token JWT token字符串
     * @return      Claims对象（包含所有载荷信息）
     * @throws ExpiredJwtException      token过期
     * @throws MalformedJwtException    token格式错误
     * @throws SignatureException       签名验证失败
     * @throws IllegalArgumentException token为空或无效
     */
    public Claims parseToken(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSignKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证Token是否有效
     * @param token JWT token字符串
     * @return      true：有效；false：无效
     */
    public boolean validateToken(String token) {
        try {
            log.info("正在验证Token：{}", token);
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token已过期：{}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token格式错误：{}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Token签名验证失败：{}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Token为空或无效：{}", e.getMessage());
        } catch (Exception e) {
            log.error("Token验证异常：{}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT token字符串
     * @return      用户ID
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            String userId = claims.getSubject();
            // 校验userId是否为空
            return userId == null || userId.isEmpty() ? null : userId;
        }catch (Exception e){
            log.error("从Token中获取用户ID异常：{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从请求头中获取Token中的用户ID
     * @param request 请求对象
     * @return        用户ID
     */
    public Long getUserIdFromToken(HttpServletRequest  request){
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")){
            return null;
        }
        token = token.substring(7);
        // 验证Token
        if(!validateToken( token)){
            return null;
        }
        return Long.valueOf(getUserIdFromToken(token));
    }

    /**
     * 从Token中获取自定义扩展载荷
     * @param token   JWT token字符串
     * @param key     载荷key
     * @param <T>     载荷类型
     * @return        载荷值
     */
    public <T> T getClaimFromToken(String token, String key) {
        Claims claims = parseToken(token);
        return (T) claims.get(key);
    }

    /**
     * 获取Token剩余过期时间（毫秒）
     *
     * @param token JWT token字符串
     * @return 剩余时间（负数表示已过期）
     */
    public long getRemainingTime(String token) {
        Claims claims = parseToken(token);
        Date expireDate = claims.getExpiration();
        return expireDate.getTime() - System.currentTimeMillis();
    }

}