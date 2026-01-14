package xlin.com.server;

import com.utils.Sha256Util;
import com.demoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类和SHA256加密工具类的单元测试
 * SpringBootTest会加载完整的Spring上下文，确保@Value注解能正常注入配置
 */
@SpringBootTest(classes = demoApplication.class)
public class SecurityUtilsTest {

    // 注入JWT工具类（Spring管理的Bean）
    @Autowired
    private JwtUtil jwtUtil;

    // ==================== JWT工具类测试 ====================
    @Test
    public void testJwtUtil() {
        // 测试数据
        String userId = "1001";
        String role = "admin";
        String permission = "all";

        // 1. 测试生成token（带扩展载荷）
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("permission", permission);
        String token = jwtUtil.generateToken(userId, claims);
        // 断言token不为空且是字符串
        assertNotNull(token, "生成的Token不能为空");
        assertTrue(!token.isEmpty(), "Token长度必须大于0");
        System.out.println("生成的JWT Token：" + token);

        // 2. 测试验证token有效性
        boolean isValid = jwtUtil.validateToken(token);
        assertTrue(isValid, "Token验证应该返回有效");

        // 3. 测试从token中获取用户ID
        String parsedUserId = jwtUtil.getUserIdFromToken(token);
        assertEquals(userId, parsedUserId, "解析出的用户ID应该和原始值一致");
        System.out.println("解析出的用户ID：" + parsedUserId);

        // 4. 测试从token中获取扩展载荷
        String parsedRole = jwtUtil.getClaimFromToken(token, "role");
        String parsedPermission = jwtUtil.getClaimFromToken(token, "permission");
        assertEquals(role, parsedRole, "解析出的角色应该和原始值一致");
        assertEquals(permission, parsedPermission, "解析出的权限应该和原始值一致");
        System.out.println("解析出的角色：" + parsedRole + "，权限：" + parsedPermission);

        // 5. 测试获取token剩余过期时间
        Long remainingTime = jwtUtil.getRemainingTime(token);
        assertTrue(remainingTime > 0, "Token剩余过期时间应该大于0");
        System.out.println("Token剩余过期时间（毫秒）：" + remainingTime);

        // 6. 测试简化版生成token（无扩展载荷）
        String simpleToken = jwtUtil.generateToken("1002");
        assertNotNull(simpleToken, "简化版生成的Token不能为空");
        System.out.println("简化版Token：" + simpleToken);

        // 7. 测试无效token验证（故意传错token）
        boolean invalidTokenValid = jwtUtil.validateToken("invalid-token-123456");
        assertFalse(invalidTokenValid, "无效Token验证应该返回false");
    }

    // ==================== SHA256工具类测试 ====================
    @Test
    public void testSha256Util() {
        // 测试数据
        String originalPassword = "123456";
        String wrongPassword = "654321";

        // 1. 测试纯文本SHA256加密
        String encryptedPwd = Sha256Util.encrypt(originalPassword);
        assertNotNull(encryptedPwd, "纯文本加密结果不能为空");
        assertTrue(encryptedPwd.length() == 64, "SHA256加密结果应该是64位16进制字符串");
        System.out.println("纯文本SHA256加密结果：" + encryptedPwd);

        // 2. 测试生成随机盐值
        String salt = Sha256Util.generateSalt();
        assertNotNull(salt, "生成的盐值不能为空");
        assertTrue(salt.length() == 32, "盐值应该是32位16进制字符串（对应16字节）");
        System.out.println("生成的随机盐值：" + salt);

        // 3. 测试带盐值的SHA256加密
        String encryptedPwdWithSalt = Sha256Util.encrypt(originalPassword, salt);
        assertNotNull(encryptedPwdWithSalt, "带盐值加密结果不能为空");
        assertTrue(encryptedPwdWithSalt.length() == 64, "带盐值加密结果应该是64位");
        System.out.println("带盐值SHA256加密结果：" + encryptedPwdWithSalt);

        // 4. 测试密码验证（正确密码+正确盐值）
        boolean isMatch = Sha256Util.verify(originalPassword, encryptedPwdWithSalt, salt);
        assertTrue(isMatch, "正确密码和盐值验证应该返回true");

        // 5. 测试密码验证（错误密码+正确盐值）
        boolean isWrongMatch = Sha256Util.verify(wrongPassword, encryptedPwdWithSalt, salt);
        assertFalse(isWrongMatch, "错误密码验证应该返回false");

        // 6. 测试密码验证（正确密码+错误盐值）
        String wrongSalt = Sha256Util.generateSalt();
        boolean isWrongSaltMatch = Sha256Util.verify(originalPassword, encryptedPwdWithSalt, wrongSalt);
        assertFalse(isWrongSaltMatch, "错误盐值验证应该返回false");
    }
}