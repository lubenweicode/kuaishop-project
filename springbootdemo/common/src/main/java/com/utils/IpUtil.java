package com.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * IP地址工具类
 */
@Slf4j
@Component
public class IpUtil {

    /**
     * 获取客户端真实IP地址
     * 处理了反向代理、负载均衡等场景下的IP获取
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 依次从请求头中获取真实IP（适配Nginx/反向代理）
        String ip = request.getHeader("X-Real-IP");
        if (isInvalidIp(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalidIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        // 如果以上都获取不到，直接从request获取
        if (isInvalidIp(ip)) {
            ip = request.getRemoteAddr();
            // 处理本地回环地址
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                ip = "localhost";
            }
        }

        // 处理X-Forwarded-For多IP场景（第一个IP为真实客户端IP）
        if (StringUtils.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        log.debug("获取客户端IP：{}", ip);
        return ip;
    }

    /**
     * 判断IP是否无效
     */
    private static boolean isInvalidIp(String ip) {
        return StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip);
    }
}