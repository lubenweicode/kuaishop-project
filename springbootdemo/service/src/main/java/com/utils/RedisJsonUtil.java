package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis JSON 序列化/反序列化通用工具类
 * 功能：Bean ↔ JSON 字符串 ↔ Redis 存储
 */
@Slf4j
@Component
public class RedisJsonUtil {

    // Spring 内置的 JSON 工具（无需额外依赖）
    @Resource
    private ObjectMapper objectMapper;

    // Redis 字符串模板（专门处理 String 类型数据）
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 从 Redis 获取 JSON 字符串并转换为指定 Bean 对象
     * @param redisKey Redis 键名
     * @param clazz    目标 Bean 类
     * @return Optional 包装的 Bean 对象（空则表示 Redis 无数据或解析失败）
     */
    public <T> Optional<T> getBean(String redisKey, Class<T> clazz) {
        // 1. 空值校验
        if (redisKey == null || clazz == null) {
            log.warn("Redis 键名或 Bean 类不能为空");
            return Optional.empty();
        }

        try {
            // 2. 从 Redis 获取 JSON 字符串
            String jsonStr = stringRedisTemplate.opsForValue().get(redisKey);
            if (jsonStr == null || jsonStr.isEmpty()) {
                log.debug("Redis 键[{}]无数据", redisKey);
                return Optional.empty();
            }

            // 3. JSON 反序列化为 Bean
            T result = objectMapper.readValue(jsonStr, clazz);
            return Optional.ofNullable(result);

        } catch (JsonProcessingException e) {
            log.error("Redis JSON 解析失败，key: {}, 异常: {}", redisKey, e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Redis 操作异常，key: {}, 异常: {}", redisKey, e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * 从 Redis 获取 JSON 字符串并转换为指定类型的 List
     * @param redisKey     Redis 键名
     * @param typeReference 类型引用（用于指定泛型类型，如 new TypeReference<List<CartItemVO>>(){}）
     * @return Optional 包装的 List 对象（空则表示 Redis 无数据或解析失败）
     */
    public <T> Optional<List<T>> getBeanList(String redisKey, TypeReference<List<T>> typeReference) {
        // 1. 空值校验
        if (redisKey == null || typeReference == null) {
            log.warn("Redis 键名或类型引用不能为空");
            return Optional.empty();
        }

        try {
            // 2. 从 Redis 获取 JSON 字符串
            String jsonStr = stringRedisTemplate.opsForValue().get(redisKey);
            if (jsonStr == null || jsonStr.isEmpty()) {
                log.debug("Redis 键[{}]无数据", redisKey);
                return Optional.empty();
            }

            // 3. JSON 反序列化为 List
            List<T> result = objectMapper.readValue(jsonStr, typeReference);
            return Optional.ofNullable(result);

        } catch (JsonProcessingException e) {
            log.error("Redis JSON 解析失败，key: {}, 异常: {}", redisKey, e.getMessage(), e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Redis 操作异常，key: {}, 异常: {}", redisKey, e.getMessage(), e);
            return Optional.empty();
        }
    }

    // ==================== 核心方法：Bean → JSON 存入 Redis ====================
    /**
     * 将 Bean 对象转换为 JSON 字符串存入 Redis（永久存储）
     * @param redisKey Redis 键名
     * @param bean     要存储的 Bean 对象
     * @return true=存储成功，false=存储失败
     */
    public boolean setBean(String redisKey, Object bean) {
        return setBean(redisKey, bean, 0, TimeUnit.SECONDS);
    }

    /**
     * 将 Bean 对象转换为 JSON 字符串存入 Redis（带过期时间）
     * @param redisKey  Redis 键名
     * @param bean      要存储的 Bean 对象
     * @param timeout   过期时间（<=0 表示永久）
     * @param timeUnit  时间单位
     * @return true=存储成功，false=存储失败
     */
    public boolean setBean(String redisKey, Object bean, long timeout, TimeUnit timeUnit) {
        // 1. 空值校验
        if (redisKey == null || bean == null) {
            log.warn("Redis 键名或 Bean 对象不能为空");
            return false;
        }

        try {
            // 2. Bean 序列化为 JSON 字符串
            String jsonStr = objectMapper.writeValueAsString(bean);

            // 3. 存入 Redis（带过期时间）
            if (timeout > 0) {
                stringRedisTemplate.opsForValue().set(redisKey, jsonStr, timeout, timeUnit);
            } else {
                stringRedisTemplate.opsForValue().set(redisKey, jsonStr);
            }

            log.debug("Redis 存储成功，key: {}", redisKey);
            return true;

        } catch (JsonProcessingException e) {
            log.error("Bean 序列化为 JSON 失败，bean: {}, 异常: {}", bean.getClass().getName(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Redis 操作异常，key: {}, 异常: {}", redisKey, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除 Redis 中的指定键
     * @param redisKey Redis 键名
     * @return true=删除成功，false=删除失败或键不存在
     */
    public boolean deleteKey(String redisKey) {
        if (redisKey == null) {
            log.warn("Redis 键名不能为空");
            return false;
        }
        Boolean deleted = stringRedisTemplate.delete(redisKey);
        return deleted != null && deleted;
    }

    /**
     * 为已存在的 Redis 键设置过期时间
     * @param redisKey Redis 键名
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true=设置成功，false=设置失败或键不存在
     */
    public boolean expireKey(String redisKey, long timeout, TimeUnit timeUnit) {
        if (redisKey == null || timeout <= 0) {
            log.warn("Redis 键名或过期时间不合法");
            return false;
        }
        Boolean expired = stringRedisTemplate.expire(redisKey, timeout, timeUnit);
        return expired != null && expired;
    }
}