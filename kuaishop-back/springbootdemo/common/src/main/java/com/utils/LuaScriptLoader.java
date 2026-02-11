package com.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Lua脚本加载工具类（支持读取多模块resources下的脚本）
 */
public class LuaScriptLoader {

    /**
     * 从类路径加载Lua脚本（核心：兼容多模块资源）
     * @param scriptPath 脚本路径（如 "scripts/unlock_order.lua"）
     * @return 脚本内容字符串
     * @throws IOException 加载失败抛出异常
     */
    public static String loadScript(String scriptPath) throws IOException {
        // 核心：通过类加载器读取，而非本地文件——类加载器会扫描所有模块的resources目录
        try (InputStream inputStream = LuaScriptLoader.class.getClassLoader().getResourceAsStream(scriptPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            if (inputStream == null) {
                throw new IOException("Lua脚本文件不存在：" + scriptPath + "，请检查文件是否在任意模块的src/main/resources目录下");
            }

            StringBuilder scriptContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                scriptContent.append(line).append("\n");
            }
            return scriptContent.toString();
        }
    }
}