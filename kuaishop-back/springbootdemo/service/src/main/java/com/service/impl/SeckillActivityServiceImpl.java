package com.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constant.Seckill.SeckillConstants;
import com.repository.mapper.SeckillActivityMapper;
import com.repository.mapper.SeckillOrderMapper;
import com.repository.mapper.SeckillProductMapper;
import com.service.SeckillActivityService;
import com.utils.SeckillRateLimiter;
import generator.domain.entity.Orders;
import generator.domain.entity.SeckillActivity;
import generator.domain.entity.SeckillProduct;
import generator.domain.order.OrderVO;
import generator.domain.response.Result;
import generator.domain.seckill.SeckillActivityCacheDTO;
import generator.domain.seckill.SeckillActivityVO;
import generator.domain.seckill.SeckillOrderAddDTO;
import generator.domain.seckill.SeckillProductVO;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 1569157760
 * &#064;description  针对表【seckill_activity(秒杀活动表)】的数据库操作Service实现
 * &#064;createDate  2026-01-29 13:31:28
 */
@Service
@Slf4j
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity>
        implements SeckillActivityService {

    private static final Long CACHE_EXPIRE = 30L;// 缓存过期时间 30s
    private static final long BASE_EXPIRE_MINUTES = 30L; // 基础有效期30分钟
    private static final long STOCK_EXPIRE_EXTEND = 10L; // 库存额外延长10分钟
    private static final int RANDOM_EXPIRE_SECONDS = 300; // 随机偏移0-5分钟（避免缓存雪崩）
    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillProductMapper seckillProductMapper;
    private final SeckillOrderMapper seckillOrderMapper; // 秒杀订单Mapper（MP生成）
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private final SeckillRateLimiter seckillRateLimiter;
    // 预加载Lua脚本（仅启动时加载一次）
    private final DefaultRedisScript<Long> seckillRedisLuaScript;
    public SeckillActivityServiceImpl(SeckillActivityMapper seckillActivityMapper,
                                      SeckillProductMapper seckillProductMapper,
                                      SeckillOrderMapper seckillOrderMapper,
                                      StringRedisTemplate redisTemplate,
                                      RedissonClient redissonClient,
                                      SeckillRateLimiter seckillRateLimiter, StringRedisTemplate stringRedisTemplate) {
        this.seckillActivityMapper = seckillActivityMapper;
        this.seckillProductMapper = seckillProductMapper;
        this.seckillOrderMapper = seckillOrderMapper;
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
        this.seckillRateLimiter = seckillRateLimiter;

        // 加载Lua脚本（独立文件：resources/scripts/seckill_order.lua）
        this.seckillRedisLuaScript = new DefaultRedisScript<>();
        try {
            // 确认脚本路径：src/main/resources/scripts/seckill_order.lua
            ClassPathResource resource = new ClassPathResource("scripts/seckill_order.lua");
            // 验证文件是否存在（关键：避免加载不存在的文件）
            if (!resource.exists()) {
                log.error("Lua脚本文件不存在！路径：scripts/seckill_order.lua");
                throw new RuntimeException("Lua脚本文件加载失败，文件不存在");
            }
            seckillRedisLuaScript.setScriptSource(new ResourceScriptSource(resource));
            seckillRedisLuaScript.setResultType(Long.class);
            log.info("Lua脚本加载成功：scripts/seckill_order.lua");
        } catch (Exception e) {
            log.error("Lua脚本加载异常！", e);
            throw new RuntimeException("Lua脚本初始化失败", e);
        }
    }

    /**
     * 获取秒杀活动列表
     *
     * @param status 状态码
     * @return 秒杀活动列表
     */
    @Override
    public Result<List<SeckillActivityVO>> getSeckillActivities(Integer status) {

        if (status == null) {
            log.error("获取秒杀活动列表失败:状态码为空");
            return Result.error(400, "状态码为空");
        }

        if (status < 0 || status > 2) {
            log.error("获取秒杀活动列表失败:状态码错误");
            return Result.error(400, "状态码错误");
        }

        // 2. 构建状态活动缓存前缀
        String activityKeyPrefix = String.format(SeckillConstants.SECKILL_ACTIVITY_LIST_KEY, status);
        // 缓存基础过期时间
        long baseExpireSeconds = BASE_EXPIRE_MINUTES * 60L;
        long randomExpireSeconds = new Random().nextInt(RANDOM_EXPIRE_SECONDS);
        long finalExpireSeconds = baseExpireSeconds + randomExpireSeconds;
        // 库存有效期延长
        long stockExpireSeconds = finalExpireSeconds + (STOCK_EXPIRE_EXTEND * 60L);

        List<SeckillActivityVO> cacheActivityVOList = this.getActivityListFromRedis(status, activityKeyPrefix, finalExpireSeconds);
        if (!cacheActivityVOList.isEmpty()) {
            log.info("从Redis中获取秒杀活动列表成功");
            return Result.success(cacheActivityVOList);
        }

        // 4. 数据库查询指定状态的秒杀活动
        LambdaQueryWrapper<SeckillActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillActivity::getStatus, status);
        List<SeckillActivity> seckillActivityList = this.list(queryWrapper);

        // 4. 处理空结果
        if (seckillActivityList == null || seckillActivityList.isEmpty()) {
            log.info("当前状态{}下无秒杀活动数据", status);
            return Result.success(new ArrayList<>());
        }

        // 5. 构建两个容器
        Map<String, String> activityRedisKeyValuesMap = new HashMap<>(seckillActivityList.size());
        Map<String, String> productRedisKeyValuesMap = new HashMap<>(seckillActivityList.size() * 2);
        Map<String, String> productStockRedisKeyValueMap = new HashMap<>(seckillActivityList.size() * 2);
        List<SeckillActivityVO> seckillActivityVOList = new ArrayList<>(seckillActivityList.size());

        for (SeckillActivity seckillActivity : seckillActivityList) {
            Long activityId = seckillActivity.getId();

            // 5.1 活动实体类转VO
            SeckillActivityVO seckillActivityVO = new SeckillActivityVO();
            BeanUtils.copyProperties(seckillActivity, seckillActivityVO);
            seckillActivityVO.setStatusText(SeckillConstants.getStatusText(seckillActivity.getStatus()));
            seckillActivityVO.setActivityId(activityId);

            SeckillActivityCacheDTO seckillActivityCacheDTO = new SeckillActivityCacheDTO();
            BeanUtils.copyProperties(seckillActivity, seckillActivityCacheDTO);
            seckillActivityCacheDTO.setStatusText(SeckillConstants.getStatusText(seckillActivityCacheDTO.getStatus()));

            // 5.2 根据活动ID查询关联商品
            List<SeckillProduct> productList = this.getActivityProductList(activityId);
            List<SeckillProductVO> productVOList = new ArrayList<>(productList.size());

            if (!productList.isEmpty()) {
                for (SeckillProduct product : productList) {
                    SeckillProductVO productVO = new SeckillProductVO();
                    BeanUtils.copyProperties(product, productVO);

                    long surplusStock = product.getTotalStock() - product.getSold();
                    productVO.setSurplusStock(Math.max(surplusStock, 0));
                    productVOList.add(productVO);

                    // 5.3 构建商品Redis键值对
                    String productKeyPrefix = String.format(
                            SeckillConstants.SECKILL_ACTIVITY_PRODUCT_KEY,
                            status,
                            activityId,
                            productVO.getProductId());
                    String productRedisValue = JSON.toJSONString(productVO);
                    productRedisKeyValuesMap.put(productKeyPrefix, productRedisValue);

                    // 5.4 构建商品库存Redis键值对
                    String productStockKeyPrefix = String.format(
                            SeckillConstants.SECKILL_ACTIVITY_PRODUCT_STOCK_KEY,
                            activityId,
                            productVO.getProductId()
                    );
                    String productsStockValue = String.valueOf(productVO.getSurplusStock());
                    productStockRedisKeyValueMap.put(productStockKeyPrefix, productsStockValue);
                }
            }

            // 5.4 商品VO列表添加到活动VO中
            seckillActivityVO.setProducts(productVOList);
            seckillActivityVOList.add(seckillActivityVO);

            // 5.5 构建活动Redis键值对
            String activityRedisKey = activityKeyPrefix + activityId;
            String activityRedisValue = JSON.toJSONString(seckillActivityCacheDTO);
            activityRedisKeyValuesMap.put(activityRedisKey, activityRedisValue);

        }

        // 6. 批量写入Redis
        // 6.1 写入活动信息 + 设置有效期
        redisTemplate.opsForValue().multiSet(activityRedisKeyValuesMap);
        activityRedisKeyValuesMap.keySet().forEach(key -> {
            redisTemplate.expire(key, finalExpireSeconds, TimeUnit.SECONDS);
        });

        // 6.2 写入商品库存信息 + 设置延长有效期
        if (!productStockRedisKeyValueMap.isEmpty()) {
            redisTemplate.opsForValue().multiSet(productStockRedisKeyValueMap);
            productStockRedisKeyValueMap.keySet().forEach(key -> {
                redisTemplate.expire(key, stockExpireSeconds, TimeUnit.SECONDS);
            });
        }

        // 6.3 写入商品完整信息 + 设置有效期
        if (!productRedisKeyValuesMap.isEmpty()) {
            redisTemplate.opsForValue().multiSet(productRedisKeyValuesMap);
            productRedisKeyValuesMap.keySet().forEach(key -> {
                redisTemplate.expire(key, finalExpireSeconds, TimeUnit.SECONDS);
            });
        }

        log.info("成功将{}条状态为{}的秒杀活动批量写入Redis缓存", seckillActivityList.size(), status);
        return Result.success(seckillActivityVOList);
    }

    private List<SeckillProduct> getActivityProductList(Long activityId) {
        if (activityId == null) {
            log.error("获取秒杀活动商品列表失败:活动ID为空");
            return new ArrayList<>();
        }

        LambdaQueryWrapper<SeckillProduct> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillProduct::getActivityId, activityId);
        return this.seckillProductMapper.selectList(queryWrapper);
    }

    /**
     * 从Redis中读取秒杀活动列表（缓存命中逻辑）
     *
     * @param status              状态码
     * @param activityCachePrefix 活动缓存前缀
     * @return 活动VO列表（缓存命中返回，未命中返回空列表）
     */
    private List<SeckillActivityVO> getActivityListFromRedis(Integer status, String activityCachePrefix, long expireSeconds) {
        try {
            // 1. 匹配该状态下的所有活动缓存键（通配符查询）
            Set<String> activityRedisKeys = redisTemplate.keys(activityCachePrefix + "*");
            if (activityRedisKeys == null || activityRedisKeys.isEmpty()) {
                return new ArrayList<>();
            }

            // 2. 批量读取Redis中的活动缓存数据（批量操作，提升性能）
            List<String> activityJsonList = redisTemplate.opsForValue().multiGet(activityRedisKeys);
            List<SeckillActivityVO> activityVOList = new ArrayList<>();

            for (String activityJson : activityJsonList) {
                if (activityJson == null || activityJson.isEmpty()) {
                    continue;
                }
                // 3. 反序列化缓存DTO为VO（或直接转换，保持字段一致）
                SeckillActivityCacheDTO cacheDTO = JSON.parseObject(activityJson, SeckillActivityCacheDTO.class);
                SeckillActivityVO activityVO = new SeckillActivityVO();
                BeanUtils.copyProperties(cacheDTO, activityVO);
                activityVO.setActivityId(cacheDTO.getId());
                activityVO.setStatusText(SeckillConstants.getStatusText(cacheDTO.getStatus()));

                // 4. 补充商品列表
                Long activityId = cacheDTO.getId();
                if (activityId != null) {
                    // 4.1 匹配该活动下的所有商品键
                    String productKeyPattern = String.format(
                            SeckillConstants.SECKILL_ACTIVITY_PRODUCT_PATTERN_KEY,
                            status,
                            activityId
                    ) + "*";
                    Set<String> productRedisKeys = redisTemplate.keys(productKeyPattern);
                    List<SeckillProductVO> productVOList = new ArrayList<>();

                    if (productRedisKeys != null && !productRedisKeys.isEmpty()) {
                        // 4.2 批量读取Redis中的商品缓存数据
                        List<String> productJsonList = redisTemplate.opsForValue().multiGet(productRedisKeys);
                        for (String productJson : productJsonList) {
                            if (productJson == null || productJson.isEmpty()) {
                                continue;
                            }

                            SeckillProductVO productVO = JSON.parseObject(productJson, SeckillProductVO.class);

                            // 4.4 读取商品实时库存（从库存键中获取，保证数据最新）
                            String productStockKey = String.format(
                                    SeckillConstants.SECKILL_ACTIVITY_PRODUCT_STOCK_KEY,
                                    activityId,
                                    productVO.getProductId()
                            );
                            String surplusStockStr = redisTemplate.opsForValue().get(productStockKey);
                            productVO.setSurplusStock(surplusStockStr != null ? Long.parseLong(surplusStockStr) : 0L);

                            productVOList.add(productVO);
                        }
                    }
                    // 4.5 设置商品列表
                    activityVO.setProducts(productVOList);
                }

                // 5. 刷新活动缓存有效期
                String activityRedisKey = activityCachePrefix + activityId;
                redisTemplate.expire(activityRedisKey, expireSeconds, TimeUnit.SECONDS);

                activityVOList.add(activityVO);
            }

            return activityVOList;
        } catch (Exception e) {
            log.error("从Redis读取秒杀活动列表失败", e);
            // 缓存读取异常时，返回空列表，走数据库查询逻辑
            return new ArrayList<>();
        }
    }


    /**
     * 创建秒杀订单
     *
     * @param userId             用户ID
     * @param seckillOrderAddDTO 秒杀订单参数
     * @return
     */
    @Override
    public Result<OrderVO> createSeckillOrder(Long userId, SeckillOrderAddDTO seckillOrderAddDTO) {

        // 1. 基础参数校验
        if (userId == null) {
            log.error("创建秒杀订单失败:用户ID为空");
            return Result.error(500, "请先登录");
        }


        if (seckillOrderAddDTO.getActivityId() == null) {
            log.error("创建秒杀订单失败:秒杀活动ID为空");
            return Result.error(500, "请选择秒杀活动");
        }

        if (seckillOrderAddDTO.getProductId() == null) {
            log.error("创建秒杀订单失败:秒杀商品ID为空");
            return Result.error(500, "请选择秒杀商品");
        }

        if (seckillOrderAddDTO.getQuantity() == null) {
            log.error("创建秒杀订单失败:秒杀商品数量为空");
            return Result.error(500, "请选择秒杀商品数量");
        }

        // 2. 令牌桶限流(用户级)
        if (!seckillRateLimiter.tryAcquire(userId)) {
            return Result.error(SeckillConstants.ERROR_SYSTEM_BUSY, "系统繁忙，请稍后再试");
        }

        // 3.参数提取
        Long activityId = seckillOrderAddDTO.getActivityId();
        Long productId = seckillOrderAddDTO.getProductId();
        Long quantity = seckillOrderAddDTO.getQuantity();

        // 4.构建Redis状态
        String stockKey = String.format(SeckillConstants.SECKILL_ACTIVITY_PRODUCT_STOCK_KEY, activityId, productId);
        String userLimitKey = String.format(SeckillConstants.SECKILL_USER_LIMIT_KEY, activityId, productId);
        String userOrderKey = String.format(SeckillConstants.SECKILL_USER_ORDER_KEY, activityId, productId, userId);


        try {
            // 5. 校验活动有效性（保持不变）
            if (!checkActivity(activityId)) {
                return Result.error(SeckillConstants.ERROR_ACTIVITY_STATUS, "秒杀活动未开始或已结束");
            }

            RScript rScript = redissonClient.getScript(StringCodec.INSTANCE);
            Long result = null;


            // 读取Lua脚本文件
            ClassPathResource resource = new ClassPathResource("scripts/seckill_order.lua");
            File luaFile = resource.getFile();

            if (!luaFile.exists()) {
                log.error("Lua脚本文件不存在！路径：scripts/seckill_order.lua");
                return Result.error(SeckillConstants.ERROR_SYSTEM_BUSY, "系统繁忙，请稍后再试");
            }

            byte[] luaBytes = Files.readAllBytes(luaFile.toPath());
            String luaScript = new String(luaBytes, StandardCharsets.UTF_8);


            // 构建KEYS和ARGV参数
            String[] keysArray = new String[]{stockKey, userLimitKey, userOrderKey};
            Object[] argsArray = new Object[]{
                    userId.toString(),
                    quantity.toString(),
                    SeckillConstants.USER_LIMIT_DEFAULT.toString()
            };


            // 执行Lua脚本（返回Long类型结果）
            result = rScript.eval(
                    RScript.Mode.READ_WRITE,
                    luaScript,
                    RScript.ReturnType.INTEGER,
                    Arrays.asList(keysArray),
                    argsArray
            );

            // 后续的结果处理逻辑（保持不变）
            if (result == null) {
                log.error("创建秒杀订单失败:Redis执行Lua脚本返回结果为空");
                return Result.error(SeckillConstants.ERROR_SYSTEM_BUSY, "系统繁忙，请稍后再试");
            }

            return switch (result.intValue()) {
                case 0 -> {
                    OrderVO orderVO = buildSeckillOrderResponse(seckillOrderAddDTO, userId);
                    yield Result.success(orderVO);
                }
                case 1 -> Result.error(SeckillConstants.ERROR_ACTIVITY_EXCEPTION, "秒杀活动异常，请稍后再试");
                case 2 -> Result.error(SeckillConstants.ERROR_STOCK_INSUFFICIENT, "库存不足，秒杀失败");
                case 3 -> Result.error(SeckillConstants.ERROR_USER_LIMIT_EXCEED, "超出个人限购数量，秒杀失败");
                case 4 -> Result.error(SeckillConstants.ERROR_REPEAT_ORDER, "您已参与该商品秒杀，不可重复下单");
                default -> Result.error(SeckillConstants.ERROR_SYSTEM_BUSY, "系统繁忙，请重试");
            };

        } catch (Exception e) {
            log.error("创建秒杀订单异常:用户ID={},活动ID={},商品ID={}", userId, activityId, productId, e);
            return Result.error(SeckillConstants.ERROR_SYSTEM_BUSY, "系统繁忙，请重试");
        }

    }

    private boolean checkActivity(Long activityId) {
        String activityCacheKey = SeckillConstants.SECKILL_ACTIVITY_KEY + activityId;
        SeckillActivity activity = null;

        // 1. 从缓存中获取秒杀活动
        try {
            String activityJson = redisTemplate.opsForValue().get(activityCacheKey);
            if (activityJson != null && !activityJson.isEmpty()) {
                activity = JSON.parseObject(activityJson, SeckillActivity.class);
            }
        } catch (Exception e) {
            log.error("获取秒杀活动失败:{}", e.getMessage());
        }

        // 2. 缓存中没有，从数据库中获取
        if (activity == null) {
            activity = seckillActivityMapper.selectById(activityId);
            if (activity == null) {
                log.error("秒杀活动不存在,activityId:{}", activityId);
                return false;
            }
            redisTemplate.opsForValue().set(activityCacheKey, JSON.toJSONString(activity), 1, TimeUnit.DAYS);
        }

        if (activity.getStatus() == null || activity.getStatus() != 1) { // 假设1表示活动进行中
            log.warn("秒杀活动状态不是进行中,activityId:{}, status:{}", activityId, activity.getStatus());
            return false;
        }

        // 3. 校验活动状态+时间
        LocalDateTime now = LocalDateTime.now();
        Date startDateTime = activity.getStartTime();
        Date endDateTime = activity.getEndTime();

        if (startDateTime == null || endDateTime == null) {
            log.error("秒杀活动不存在,activityId:{}", activityId);
            return false;
        }

        LocalDateTime startTime = startDateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime endTime = endDateTime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        boolean isIntime = (now.isEqual(startTime) || now.isAfter(startTime))
                && (now.isEqual(endTime) || now.isBefore(endTime));

        if (!isIntime) {
            if (now.isBefore(startTime)) {
                log.warn("秒杀活动尚未开始,activityId:{}, 当前时间:{}, 开始时间:{}", activityId, now, startTime);
            } else if (now.isAfter(endTime)) {
                log.warn("秒杀活动已经结束,activityId:{}, 当前时间:{}, 结束时间:{}", activityId, now, endTime);
            }
            return false;
        }

        return true;
    }

    private OrderVO buildSeckillOrderResponse(SeckillOrderAddDTO seckillOrderAddDTO, Long userId) {
        Long productId = seckillOrderAddDTO.getProductId();
        Long quantity = seckillOrderAddDTO.getQuantity();
        Long activityId = seckillOrderAddDTO.getActivityId();

        // 1. 获取秒杀商品信息
        LambdaQueryWrapper<SeckillProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SeckillProduct::getProductId, productId)
                .eq(SeckillProduct::getActivityId, activityId)
                .orderByDesc(SeckillProduct::getCreateTime);

        // 1. 查询列表
        List<SeckillProduct> productList = seckillProductMapper.selectList(wrapper);
        // 2. 取第一条记录（若列表为空则为 null）
        SeckillProduct product = null;
        if (!productList.isEmpty()) {
            product = productList.get(0); // 取第一条有效记录
        }

        if (product == null) {
            log.error("秒杀商品不存在,productId:{}", productId);
            throw new RuntimeException("商品信息异常");
        }

        String productInfo = product.toInfo();

        // 2. 获取秒杀订单信息
        String orderNo = "SK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setActivityId(activityId);
        order.setPayAmount(product.getSeckillPrice().multiply(new BigDecimal(quantity)));
        order.setTotalAmount(product.getSeckillPrice().multiply(new BigDecimal(quantity)));
        order.setOrderStatus(0);
        order.setInfo(productInfo);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());

        // 3. 插入秒杀订单
        seckillOrderMapper.insert(order);
        log.info("创建秒杀订单成功,order:{}", order);

        // 4. 构建返回的OrderVO
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setUserId(order.getUserId());
        orderVO.setUsername(userId.toString());
        orderVO.setInfo(productInfo);
        orderVO.setTotalAmount(order.getTotalAmount());
        orderVO.setPayAmount(order.getPayAmount());
        orderVO.setStatus(order.getOrderStatus());
        orderVO.setStatusText(order.getOrderStatus() == 0 ? "待支付" : "已支付");
        orderVO.setRemark("秒杀订单，请注意及时支付");
        orderVO.setReceiverName(userId.toString());
        orderVO.setPayUrl(SeckillConstants.PAY_URL_PREFIX + order.getOrderNo());

        return orderVO;
    }
}




