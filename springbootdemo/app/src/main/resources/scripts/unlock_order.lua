-- 校验锁的持有者，只有匹配才删除锁（原子操作）
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end