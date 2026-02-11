package utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class OrderDistributedLock {

    private final RedisTemplate<String, String> redisTemplate;
    // 预加载Lua解锁脚本
    private final String unlockLuaScript;

    // 注入RedisTemplate(需提前配置Redis连接)
    public OrderDistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 加载Lua脚本(捕获异常,避免启动失败)
        try {
            this.unlockLuaScript = LuaScriptLoader.loadScript("scripts/unlock_order.lua");
        }catch (IOException e){
            throw new RuntimeException("加载Lua解锁脚本失败" + e.getMessage());
        }
    }

    /**
     * 尝试获取锁
     *
     * @param orderKey 锁的key
     * @return 锁的唯一标识(用于解锁),获取失败返回null。
     */
    public String tryLock(String orderKey) {
        return tryLock(orderKey, DEFAULT_LOCK_TIMEOUT, DEFAULT_RETRY_INTERVAL, DEFAULT_MAX_RETRY_COUNT);
    }

    // 锁的前缀,方便区别不同锁
    private static final String ORDER_LOCK_PREFIX = "order:lock:";

    // 默认锁超时时间(30秒)
    private static final long DEFAULT_LOCK_TIMEOUT = 30L;

    // 默认重试间隔(100毫秒)
    private static final long DEFAULT_RETRY_INTERVAL = 100L;

    // 默认最大重试次数
    private static final int DEFAULT_MAX_RETRY_COUNT = 30;

    /**
     * 尝试获取锁
     *
     * @param orderKey 锁的key
     * @param lockTimeout 锁超时时间(秒)
     * @param retryInterval 重试间隔(毫秒)
     * @param maxRetryCount 最大重试次数
     * @return 锁的唯一标识(用于解锁),获取失败返回null。
     */
    public String tryLock(String orderKey, long lockTimeout, long retryInterval, int maxRetryCount){
        // 生成唯一标识,防止误删其他线程的锁
        String lockValue = UUID.randomUUID().toString();
        String realKey = ORDER_LOCK_PREFIX + orderKey;
        int retryCount = 0;

        while(retryCount < maxRetryCount){
            // SETNX + 过期时间 原子操作
            Boolean success = redisTemplate.opsForValue().setIfAbsent(realKey, lockValue, lockTimeout, TimeUnit.SECONDS);
            if(Boolean.TRUE.equals( success)){
                // 获取锁成功
                return lockValue;
            }
            // 重试间隔
            try {
                Thread.sleep(retryInterval);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return null;
            }
            retryCount++;
        }
        // 重试次数耗尽,获取锁失败
        return null;
    }

    /**
     * 释放锁
     *
     * @param orderKey 锁的key
     * @param lockValue 锁的唯一标识
     * @return 是否释放成功
     */
    public boolean releaseLock(String orderKey, String lockValue){
        if (lockValue == null){
            return false;
        }
        String realKey = ORDER_LOCK_PREFIX + orderKey;

        // Lua脚本:先校验锁的持有者,再删除锁
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(unlockLuaScript);
        redisScript.setResultType(Long.class);

        // 执行脚本
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(realKey), lockValue);
        return result != null && result > 0;
    }

}
