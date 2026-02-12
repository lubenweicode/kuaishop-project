package generator.domain.seckill;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class SeckillActivityVO {

    private Long activityId; // 秒杀活动id
    private String activityName; // 秒杀活动名称
    private LocalDateTime startTime; // 秒杀活动开始时间
    private LocalDateTime endTime; // 秒杀活动结束时间
    private Integer status; // 秒杀活动状态
    private String statusText; // 秒杀活动状态文本

    private List<SeckillProductVO> products;

}
