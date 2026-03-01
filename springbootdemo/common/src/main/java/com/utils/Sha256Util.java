package com.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * SHA256加密工具类
 * 支持纯文本加密、带盐值加密，适用于密码等敏感信息加密
 */
@Component
public class Sha256Util {

    // 加密算法名称
    private static final String ALGORITHM = "SHA-256";

    /**
     * 生成随机盐值（16位）
     *
     * @return 16进制字符串格式的盐值
     */
    public static String generateSalt() {
        // 创建随机数生成器
        SecureRandom random = new SecureRandom();
        // 创建盐值字节数组
        byte[] salt = new byte[16];
        // 填充盐值
        random.nextBytes(salt);
        // 将盐值转换为16进制字符串
        return bytesToHex(salt);
    }

    /**
     * SHA256带盐值加密（推荐用于密码加密）
     *
     * @param text 需要加密的文本
     * @param salt 盐值（null则不使用盐值）
     * @return 加密后的16进制字符串
     */
    public static String encrypt(String text, String salt) {
        try {
            // 创建MessageDigest对象
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            // 重置MessageDigest对象
            digest.reset();
            // 拼接盐值（如果有）
            String content = salt == null ? text : text + salt;
            // 执行加密
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest(bytes);
            // 转换为16进制字符串
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA256加密算法不存在", e);
        }
    }

    /**
     * 将字节数组转换为16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            // 将字节转换为16进制字符串
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}