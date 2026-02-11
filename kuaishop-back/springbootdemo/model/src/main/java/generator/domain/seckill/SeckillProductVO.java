package generator.domain.seckill;

import generator.domain.entity.SeckillProduct;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀商品视图对象（VO）
 * 适配前端展示和秒杀业务逻辑，扩展 SeckillProduct 实体类字段
 */
@Data
public class SeckillProductVO extends SeckillProduct implements Serializable {

    private static final long serialVersionUID = 1L; // 序列化版本号，避免反序列化异常

    /**
     * 剩余库存（秒杀过程中动态更新）
     */
    private Long surplusStock;

    /**
     * 已售数量（默认 0）
     */
    private Long sold = 0L;

    /**
     * 每人限购数量
     */
    private Integer limitPerUser;

}