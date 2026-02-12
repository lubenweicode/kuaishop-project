import com.utils.DateUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * DateUtil 测试类
 * 覆盖所有核心方法的正常/异常场景测试
 */
public class DateUtilTest {

    // ====================== strToDate 方法测试 ======================
    @Test
    void testStrToDate_Success() {
        // 测试用例：合法的日期字符串
        String dateStr = "2026-02-12";
        Date date = DateUtil.strToDate(dateStr);

        // 验证转换结果：转换后的Date的年月日应和输入一致
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Assertions.assertEquals(2026, localDateTime.getYear());
        Assertions.assertEquals(2, localDateTime.getMonthValue());
        Assertions.assertEquals(12, localDateTime.getDayOfMonth());
        // 时间部分应为当天0点
        Assertions.assertEquals(0, localDateTime.getHour());
        Assertions.assertEquals(0, localDateTime.getMinute());
        Assertions.assertEquals(0, localDateTime.getSecond());
    }

    @Test
    void testStrToDate_NullInput() {
        // 测试用例：传入null
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDate(null)
        );
        Assertions.assertEquals("日期字符串不能为空", exception.getMessage());
    }

    @Test
    void testStrToDate_EmptyInput() {
        // 测试用例：传入空字符串/空格
        IllegalArgumentException exception1 = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDate("")
        );
        Assertions.assertEquals("日期字符串不能为空", exception1.getMessage());

        IllegalArgumentException exception2 = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDate("   ")
        );
        Assertions.assertEquals("日期字符串不能为空", exception2.getMessage());
    }

    @Test
    void testStrToDate_InvalidFormat() {
        // 测试用例：格式错误的日期字符串
        String invalidDateStr = "2026/02/12";
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDate(invalidDateStr)
        );
        Assertions.assertEquals("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss", exception.getMessage());
    }

    // ====================== strToDateTime 方法测试 ======================
    @Test
    void testStrToDateTime_StandardFormat_Success() {
        // 测试用例：标准日期时间格式
        String datetimeStr = "2026-02-12 15:30:45";
        Date date = DateUtil.strToDateTime(datetimeStr);

        // 验证转换结果
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Assertions.assertEquals(2026, localDateTime.getYear());
        Assertions.assertEquals(2, localDateTime.getMonthValue());
        Assertions.assertEquals(12, localDateTime.getDayOfMonth());
        Assertions.assertEquals(15, localDateTime.getHour());
        Assertions.assertEquals(30, localDateTime.getMinute());
        Assertions.assertEquals(45, localDateTime.getSecond());
    }


    @Test
    void testStrToDateTime_NullInput() {
        // 测试用例：传入null
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDateTime(null)
        );
        Assertions.assertEquals("日期时间字符串不能为空", exception.getMessage());
    }

    @Test
    void testStrToDateTime_EmptyInput() {
        // 测试用例：传入空字符串/空格
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDateTime("   ")
        );
        Assertions.assertEquals("日期时间字符串不能为空", exception.getMessage());
    }

    @Test
    void testStrToDateTime_InvalidFormat() {
        // 测试用例：两种格式都不匹配
        String invalidDatetimeStr = "2026-02-12 15:30"; // 缺少秒数
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.strToDateTime(invalidDatetimeStr)
        );
        Assertions.assertEquals("日期格式错误，期望格式：yyyy-MM-dd HH:mm:ss", exception.getMessage());
    }

    // ====================== formatDateTime 方法测试 ======================
    @Test
    void testFormatDateTime_Success() {
        // 构造一个指定时间的Date对象
        LocalDateTime testDateTime = LocalDateTime.of(2026, 2, 12, 15, 30, 45);
        Date date = Date.from(testDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // 转换为字符串并验证
        String datetimeStr = DateUtil.formatDateTime(date);
        Assertions.assertEquals("2026-02-12 15:30:45", datetimeStr);
    }

    @Test
    void testFormatDateTime_NullInput() {
        // 测试用例：传入null
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> DateUtil.formatDateTime(null)
        );
        Assertions.assertEquals("待格式化的日期对象不能为空", exception.getMessage());
    }
}