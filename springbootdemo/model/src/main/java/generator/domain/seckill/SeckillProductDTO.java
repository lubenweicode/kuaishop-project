package generator.domain.seckill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillProductDTO {
    private Long productId; // 商品ID
    private Long activityId; // 活动ID
    private String productName; // 商品名称
    private BigDecimal seckillPrice; // 秒杀价
    private BigDecimal originalPrice; // 原价
    private Long totalStock; // 总库存
    private Long surplusStock; // 剩余库存
    private Long sold; // 已售
    private Integer limitPerUser; // 每人限购数
}