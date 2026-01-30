package com.controller;

import com.service.SeckillActivityService;
import generator.domain.demo.Result;
import generator.domain.entity.SeckillActivity;
import generator.domain.seckill.SeckillActivityVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    private SeckillActivityService seckillActivityService;
    public SeckillController(SeckillActivityService seckillActivityService) {
        this.seckillActivityService = seckillActivityService;
    }

    /**
     * 获取秒杀活动列表
     * @param statusStr 状态 0:创建中 1:进行中 2:已结束
     */
    @GetMapping("/activities")
    public Result<List<SeckillActivityVO>> getSeckillActivities(@RequestParam("status")String statusStr) {
        Integer status = null;
        if (statusStr !=  null && !statusStr.trim().isEmpty()){
            try{
                status = Integer.parseInt(statusStr);
            }catch (NumberFormatException  e){
                return Result.error(400,"status参数必须是有效的数字");
            }
        }
        if(status == null){
            status = 1;
        }
        return seckillActivityService.getSeckillActivities(status);
    }
}
