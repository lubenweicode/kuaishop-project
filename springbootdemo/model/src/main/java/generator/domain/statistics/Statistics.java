package generator.domain.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class Statistics {

    private BigDecimal totalSales; // 营业额
    private BigDecimal totalOrders; // 订单数
    private Long TotalUsers; // 用户数

    private DailyData dailyData; // 当日数据
    private TopProducts topProducts; // 畅销商品

    @Data
    public static class DailyData {
        private String date; // 日期
        private BigDecimal sales; // 营业额
        private Long orders; // 订单数
        private Long users; // 用户数
    }

    @Data
    public static class TopProducts {
        private String productId; // 商品ID
        private String productName; // 商品名称
        private BigDecimal sales; // 营业额
        private BigInteger quantity; // 销量
    }

}
