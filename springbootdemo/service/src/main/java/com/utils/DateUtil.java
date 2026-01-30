package com.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;

@Component
public class DateUtil {

    // 日志记录（替代System.err.println()，符合Spring项目规范）
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    // 定义常用的日期格式
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // 新增：支持解析 Tue Feb 10 10:00:00 CST 2026 格式（必须指定Locale.US，否则中文环境解析英文月份/星期失败）
    private static final DateTimeFormatter EN_LOCAL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    /**
     * 日期字符串（yyyy-MM-dd）转 Date
     * @param dateStr 日期字符串（如：2026-01-28）
     * @return Date 类型
     * @throws IllegalArgumentException 日期字符串为空或格式不匹配时抛出异常（不再返回null）
     */
    public static Date strToDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期字符串不能为空");
        }
        try {
            // 先转 LocalDate → 再转 Date
            LocalDate localDate = LocalDate.parse(dateStr.trim(), DATE_FORMAT);
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            String errorMsg = String.format("日期格式错误，期望格式：yyyy-MM-dd，实际值：%s", dateStr);
            logger.error(errorMsg, e); // 用日志框架记录异常详情
            throw new IllegalArgumentException(errorMsg, e); // 抛出异常，让调用方感知错误，而非返回null
        }
    }

    /**
     * 日期时间字符串转 Date（兼容两种格式）
     * 1. 优先解析：yyyy-MM-dd HH:mm:ss（你的期望格式）
     * 2. 兼容解析：Tue Feb 10 10:00:00 CST 2026（你的实际传入格式）
     * @param datetimeStr 日期时间字符串
     * @return Date 类型
     * @throws IllegalArgumentException 日期字符串为空或两种格式都不匹配时抛出异常（不再返回null）
     */
    public static Date strToDateTime(String datetimeStr) {
        if (datetimeStr == null || datetimeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("日期时间字符串不能为空");
        }
        // 1. 先尝试解析标准格式（yyyy-MM-dd HH:mm:ss）
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(datetimeStr.trim(), DATETIME_FORMAT);
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e1) {
            // 2. 标准格式解析失败，尝试解析英文本地化格式
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(datetimeStr.trim(), EN_LOCAL_DATETIME_FORMAT);
                return Date.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e2) {
                String errorMsg = String.format("日期时间格式错误，期望格式：yyyy-MM-dd HH:mm:ss，实际值：%s", datetimeStr);
                logger.error(errorMsg, e2); // 用日志框架记录异常详情
                throw new IllegalArgumentException(errorMsg, e2); // 抛出异常，让调用方感知错误，而非返回null
            }
        }
    }

    /**
     * Date 转 日期时间字符串（yyyy-MM-dd HH:mm:ss）
     * @param date 日期对象
     * @return 格式化后的日期时间字符串
     * @throws IllegalArgumentException 传入Date为null时抛出异常
     */
    public static String formatDateTime(Date date) { // 修复返回值：从Object改为String，直接返回格式化结果
        if (date == null) {
            throw new IllegalArgumentException("待格式化的日期对象不能为空");
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return DATETIME_FORMAT.format(localDateTime);
    }
}