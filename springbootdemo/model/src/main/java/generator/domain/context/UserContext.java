package generator.domain.context;

/**
 * 用户上下文(ThreadLocal存储,保持线程隔离)
 */
public class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    // 设置用户ID
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    // 获取用户ID
    public static Long getUserId() {
        return USER_ID.get();
    }

    // 清空上下文(防止内存泄露)
    public static void clear() {
        USER_ID.remove();
    }
}
