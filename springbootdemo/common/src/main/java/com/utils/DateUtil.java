package com.utils;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DateUtil {

    // 定义常用的日期格式
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter EN_LOCAL_DATETIME_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);

    /**
     * 日期字符串（yyyy-MM-dd）转 Date
     *
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
           log.info("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss，实际值：{}", dateStr);
           throw new IllegalArgumentException("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss", e);
        }
    }

    /**
     * 日期时间字符串转 Date（兼容两种格式）
     * 1. 优先解析：yyyy-MM-dd HH:mm:ss（你的期望格式）
     * 2. 兼容解析：Tue Feb 10 10:00:00 CST 2026（你的实际传入格式）
     *
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
            // 先转 LocalDateTime → 再转 Date
            LocalDateTime localDateTime = LocalDateTime.parse(datetimeStr.trim(), DATETIME_FORMAT);
            // 获取当前时区
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e1) {
            // 2. 标准格式解析失败，尝试解析英文本地化格式
            try {
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(datetimeStr.trim(), EN_LOCAL_DATETIME_FORMAT);
                return Date.from(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e2) {
                log.info("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss，实际值：{}", datetimeStr);
                throw new IllegalArgumentException("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss", e2);
            }
        }
    }

    /**
     * Date 转 日期时间字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @param date 日期对象
     * @return 格式化后的日期时间字符串
     * @throws IllegalArgumentException 传入Date为null时抛出异常
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("待格式化的日期对象不能为空");
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return DATETIME_FORMAT.format(localDateTime);
    }
}