package com.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lua脚本加载工具类
 */
public class LuaScriptLoader {


    /**
     * 读取resources目录下的Lua脚本文件
     * @param scriptPath 脚本路径（如：lua/unlock_order.lua）
     * @return 脚本内容字符串
     * @throws IOException 读取文件异常
     */
    public static String loadScript(String scriptPath) throws IOException {
        // 加载classpath下的脚本文件
        ClassPathResource resource = new ClassPathResource(scriptPath);
        // 读取文件内容并转为UTF-8字符串
        byte[] scriptBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(scriptBytes, StandardCharsets.UTF_8);
    }
}
