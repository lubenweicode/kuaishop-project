package generator.domain.seckill;

import lombok.Data;

@Data
public class SeckillOrderAddDTO {

    private Long activityId; // 秒杀活动ID
    private Long productId; // 商品ID
    private Long quantity=1L; // 购买数量
}
