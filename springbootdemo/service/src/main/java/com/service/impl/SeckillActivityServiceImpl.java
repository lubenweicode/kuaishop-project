package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.SeckillActivityMapper;
import com.mapper.SeckillProductMapper;
import com.service.SeckillActivityService;
import generator.domain.demo.Result;
import generator.domain.entity.SeckillActivity;
import generator.domain.entity.SeckillProduct;
import generator.domain.seckill.SeckillActivityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 1569157760
* &#064;description  针对表【seckill_activity(秒杀活动表)】的数据库操作Service实现
* &#064;createDate  2026-01-29 13:31:28
 */
@Service
@Slf4j
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity>
    implements SeckillActivityService {

    private final SeckillActivityMapper seckillActivityMapper;
    private final StringRedisTemplate redisTemplate;
    private final SeckillProductMapper seckillProductMapper;

    public SeckillActivityServiceImpl(SeckillActivityMapper seckillActivityMapper, StringRedisTemplate redisTemplate, SeckillProductMapper seckillProductMapper) {
        this.seckillActivityMapper = seckillActivityMapper;
        this.redisTemplate = redisTemplate;
        this.seckillProductMapper = seckillProductMapper;
    }

    private static final String ACTIVITY_KEY = "activity:%d";
    // 缓存过期时间 30s
    private static final Long CACHE_EXPIRE = 30L;

    @Override
    public Result<List<SeckillActivityVO>> getSeckillActivities(Integer status) {

        if(status == null){
            log.error("获取秒杀活动列表失败:状态码为空");
            return Result.error(400,"状态码为空");
        }

        if(status < 0 || status > 2){
            log.error("获取秒杀活动列表失败:状态码错误");
            return Result.error(400,"状态码错误");
        }

        // 缓存key
        String cacheKey = String.format(ACTIVITY_KEY, status);
        List<SeckillActivityVO> activityVOList = null;
        try {
            String activityVOListJSON = redisTemplate.opsForValue().get(cacheKey);
            if(activityVOListJSON != null && !activityVOListJSON.isEmpty()){
                activityVOList = JSON.parseArray(activityVOListJSON, SeckillActivityVO.class);
                log.info("从缓存中获取数据,共有:{}条数据", activityVOList.size());
                return Result.success(activityVOList);
            }
        } catch (Exception e) {
            // 缓存获取失败
            log.error("从Redis缓存中获取秒杀活动列表失败，缓存key：{}", cacheKey, e);
        }

        // 数据库查询

        try {
            List<SeckillActivity> activityList = seckillActivityMapper.getSeckillActivities(status);
            if (CollectionUtils.isEmpty(activityList)) {
                // 无活动数据，写入空列表缓存，防止缓存穿透
                activityVOList = Collections.emptyList();
                String emptyJson = JSON.toJSONString(activityVOList);
                redisTemplate.opsForValue().set(cacheKey, emptyJson, CACHE_EXPIRE, TimeUnit.SECONDS);
                log.info("数据库中无对应状态的秒杀活动，写入空列表缓存，缓存key：{}", cacheKey);
                return Result.success(activityVOList);
            }

            // 转换为VO
            activityVOList = SeckillActivityVO.fromEntityList(activityList);
            if (CollectionUtils.isEmpty(activityVOList)) {
                activityVOList = Collections.emptyList();
                String emptyJson = JSON.toJSONString(activityVOList);
                redisTemplate.opsForValue().set(cacheKey, emptyJson, CACHE_EXPIRE, TimeUnit.SECONDS);
                log.info("活动实体转VO后无有效数据，写入空列表缓存，缓存key：{}", cacheKey);
                return Result.success(activityVOList);
            }

            // 获取关联商品ID
            List<Long> activityIdList = activityVOList.stream()
                    .map(SeckillActivityVO::getActivityId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(activityIdList)) {
                // 无有效活动ID，直接写入缓存返回
                String activityVOListJSON = JSON.toJSONString(activityVOList);
                redisTemplate.opsForValue().set(cacheKey, activityVOListJSON, CACHE_EXPIRE, TimeUnit.SECONDS);
                log.info("从数据库中获取秒杀活动列表（无有效商品关联）并写入缓存成功，缓存key：{}，数据条数：{}", cacheKey, activityVOList.size());
                return Result.success(activityVOList);
            }


            // 批量查询商品数据
            if(!CollectionUtils.isEmpty(activityIdList)){
                LambdaQueryWrapper<SeckillProduct> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SeckillProduct::getActivityId, activityIdList);
                List<SeckillProduct> allProductList = seckillProductMapper.selectList(queryWrapper);

                if (!CollectionUtils.isEmpty(allProductList)) {
                    // 构建商品映射Map
                    Map<Long, List<SeckillProduct>> activityProductMap = allProductList.stream()
                            .collect(Collectors.groupingBy(SeckillProduct::getActivityId));

                    // 绑定商品到对应活动VO
                    for (SeckillActivityVO activityVO : activityVOList) {
                        Long activityId = activityVO.getActivityId();
                        List<SeckillProduct> productList = activityProductMap.getOrDefault(activityId, Collections.emptyList());
                        activityVO.setProducts(productList);
                    }

                    // 日志补充商品条数
                    log.info("成功关联商品，商品总条数：{}", allProductList.size());
                }
                String activityVOListJSON = JSON.toJSONString(activityVOList);
                redisTemplate.opsForValue().set(cacheKey, activityVOListJSON, CACHE_EXPIRE, TimeUnit.SECONDS);
                log.info("从数据库中获取秒杀活动列表并写入缓存成功，缓存key：{}，活动条数：{}", cacheKey, activityVOList.size());
            }
        } catch (Exception e) {
            log.error("获取秒杀活动列表失败:{}", e.getMessage());
            return Result.error(500, "系统异常,获取秒杀活动列表失败");
        }

        // 返回结果
        return Result.success(activityVOList);
    }
}




