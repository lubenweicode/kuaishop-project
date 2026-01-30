package generator.domain.seckill;

import generator.domain.entity.SeckillActivity;
import generator.domain.entity.SeckillProduct;
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

    private List<SeckillProduct> products;

    public static List<SeckillActivityVO> fromEntityList(List<SeckillActivity> activityList) {
        return activityList.stream().map(activity -> {
            SeckillActivityVO activityVO = new SeckillActivityVO();
            activityVO.setActivityId(activity.getId());
            activityVO.setActivityName(activity.getActivityName());
            activityVO.setStartTime(activity.getStartTime());
            activityVO.setEndTime(activity.getEndTime());

            activityVO.setStatus(activity.getStatus());

            if(activity.getStatus() == 1){
                activityVO.setStatusText("进行中");
            } else if (activity.getStatus() == 2) {
                activityVO.setStatusText("已结束");
            } else if (activity.getStatus() == 0) {
                activityVO.setStatusText("未开始");
            }
            return activityVO;
        }).toList();
    }
}
