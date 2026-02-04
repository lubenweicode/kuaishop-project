package generator.domain.seckill;

import generator.domain.entity.SeckillActivity;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeckillActivityVO {

    private Long activityId; // 秒杀活动id
    private String activityName; // 秒杀活动名称
    private Date startTime; // 秒杀活动开始时间
    private Date endTime; // 秒杀活动结束时间
    private Integer status; // 秒杀活动状态
    private String statusText; // 秒杀活动状态文本

    private List<SeckillProductVO> products;

}
