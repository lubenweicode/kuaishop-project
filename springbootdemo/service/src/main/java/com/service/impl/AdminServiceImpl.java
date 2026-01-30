package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mapper.AdminMapper;
import com.mapper.OrderMapper;
import com.mapper.SeckillActivityMapper;
import com.mapper.SeckillProductMapper;
import com.service.AdminService;
import com.utils.DateUtil;
import com.utils.IdentityUtil;
import generator.domain.demo.Result;
import generator.domain.entity.Orders;
import generator.domain.entity.Product;
import generator.domain.entity.SeckillActivity;
import generator.domain.entity.User;
import generator.domain.order.OrderPageVO;
import generator.domain.order.OrderVO;
import generator.domain.product.ProductDTO;
import generator.domain.product.ProductVO;
import generator.domain.seckill.SeckillActivityDTO;
import generator.domain.seckill.SeckillProductVO;
import generator.domain.seckill.SeckillProductDTO;
import generator.domain.statistics.Statistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper, User> implements AdminService {

    private final IdentityUtil identityUtil;
    private final AdminMapper adminMapper;
    private final OrderMapper orderMapper;
    private final DateUtil dateUtil;
    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillProductMapper seckillProductMapper;

    public AdminServiceImpl(IdentityUtil identityUtil, AdminMapper adminMapper, OrderMapper orderMapper, DateUtil dateUtil, SeckillActivityMapper seckillActivityMapper, SeckillProductMapper seckillProductMapper) {
        this.identityUtil = identityUtil;
        this.adminMapper = adminMapper;
        this.orderMapper = orderMapper;
        this.dateUtil = dateUtil;
        this.seckillActivityMapper = seckillActivityMapper;
        this.seckillProductMapper = seckillProductMapper;
    }

    @Override
    public Result<ProductVO> addProducts(Long userId,ProductDTO productDTO) {

        if (!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        if(productDTO.getName() == null){
            log.error("用户 {} 商品名称不能为空", userId);
            return Result.error(400,"商品名称不能为空");
        }

        if(productDTO.getDescription() == null){
            log.error("用户 {} 商品描述不能为空", userId);
            return Result.error(400,"商品描述不能为空");
        }

        if(productDTO.getPrice() == null){
            log.error("用户 {} 商品价格不能为空", userId);
            return Result.error(400,"商品价格不能为空");
        }

        if(productDTO.getStock() == null){
            log.error("用户 {} 商品库存不能为空", userId);
            return Result.error(400,"商品库存不能为空");
        }

        if(productDTO.getCategoryId() == null){
            log.error("用户 {} 商品分类不能为空", userId);
            return Result.error(400,"商品分类不能为空");
        }

        if(productDTO.getImages() == null){
            log.error("用户 {} 商品图片不能为空", userId);
            return Result.error(400,"商品图片不能为空");
        }

        if(productDTO.getSpecifications() == null){
            log.error("用户 {} 商品规格不能为空", userId);
            return Result.error(400,"商品规格不能为空");
        }

        Product product = new Product();

        BeanUtils.copyProperties(productDTO, product);

        boolean result = adminMapper.addProducts(product);

        if(result){
            ProductVO productVO = new ProductVO();
            BeanUtils.copyProperties(product, productVO);
            return Result.success(productVO);
        }else{
            return Result.error(500,"添加商品失败");
        }
    }

    @Override
    public Result<OrderPageVO> getOrderList(Long userId, Integer page, Integer size, String orderNo, String startTime, String endTime, Integer status) {

        if (userId == null){
            log.error("订单查询失败:用户未登录");
            return Result.error(401,"用户未登录");
        }

        if(!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        int current = page == null ? 1 : page;
        int pageSize = size == null ? 10 : size;

        IPage<Orders> orderPage = new Page<>(current, pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if(orderNo != null && !orderNo.trim().isEmpty()){
            queryWrapper.like(Orders::getOrderNo, orderNo);
        }

        if(startTime != null && !startTime.trim().isEmpty()){
            Date startDate = DateUtil.strToDate(startTime);
            if(startDate != null){
                queryWrapper.ge(Orders::getCreateTime, startDate);
            }else{
                log.error("订单查询失败:开始时间格式错误");
                return Result.error(400,"开始时间格式错误");
            }
        }

        if(endTime != null && !endTime.trim().isEmpty()){
            Date endDate = DateUtil.strToDate(endTime);
            if(endDate != null){
                queryWrapper.le(Orders::getCreateTime, endDate);
            }else{
                log.error("订单查询失败:结束时间格式错误");
                return Result.error(400,"结束时间格式错误");
            }
        }

        if(status != null){
            queryWrapper.eq(Orders::getOrderStatus, status);
        }

        queryWrapper.orderByDesc(Orders::getCreateTime);

        IPage<Orders> resultPage = orderMapper.selectPage(orderPage, queryWrapper);

        List<OrderVO> pageVOList = resultPage.getRecords().stream()
                .map(order -> {
                    OrderVO orderVO = new OrderVO();
                    BeanUtils.copyProperties(order, orderVO);
                    return orderVO;
                })
                .collect(Collectors.toList());

        OrderPageVO orderPageVO = new OrderPageVO();
        orderPageVO.setTotal(resultPage.getTotal());
        orderPageVO.setCurrent((int) resultPage.getCurrent());
        orderPageVO.setSize((int) resultPage.getSize());
        orderPageVO.setPages(resultPage.getPages());
        orderPageVO.setRecords(pageVOList);

        log.info("用户 {} 订单查询成功：当前页{}，每页{}条，总{}条，总{}页",
                userId, orderPageVO.getCurrent(), orderPageVO.getSize(),
                orderPageVO.getTotal(), orderPageVO.getPages());
        return Result.success(orderPageVO);
    }

    /**
     * 发货
     * @param userId 用户id
     * @param orderNo 订单编号
     * @param logisticsCompany 物流公司
     * @param logisticsNo 物流单号
     * @return 发货结果
     */
    @Override
    public Result<String> shipOrder(Long userId, String orderNo,String logisticsCompany,String logisticsNo) {

        if (userId == null){
            log.error("订单管理-发货失败:用户未登录");
            return Result.error(401,"用户未登录");
        }

        if(!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        if (orderNo == null){
            log.error("订单管理-发货失败:订单编号不能为空");
            return Result.error(400,"订单编号不能为空");
        }

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = orderMapper.selectOne(queryWrapper);

        if (order == null){
            log.error("订单管理-发货失败:订单不存在");
            return Result.error(404,"订单不存在");
        }

        switch (order.getOrderStatus()) {
            case 1:
                log.error("订单管理-发货失败:订单已支付");
                return Result.error(400,"订单已支付");
            case 2:
                log.error("订单管理-发货失败:订单已发货");
                return Result.error(400,"订单已发货");
            case 3:
                log.error("订单管理-发货失败:订单已完成");
                return Result.error(400,"订单已完成");
            case 4:
                log.error("订单管理-发货失败:订单已取消");
                return Result.error(400,"订单已取消");
        }

        order.setOrderStatus(2);
        String orderOrderNo = "OR"+orderNo;
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(orderOrderNo);
        boolean result = orderMapper.updateById(order) > 0;

        if(result){
            log.info("订单管理-发货成功:订单{}发货成功", orderNo);
            return Result.success("发货成功");
        }else{
            log.error("订单管理-发货失败:订单{}发货失败", orderNo);
            return Result.error(500,"发货失败");
        }


    }

    /**
     * 统计
     * @param userId 用户id
     * @param type 统计类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    @Override
    public Result<Statistics> getSalesStatistics(Long userId,String type, String startTime, String endTime) {

        // 1.用户登录校验
        if (userId == null){
            log.error("订单管理-统计失败:用户未登录");
            return Result.error(401,"用户未登录");
        }

        // 2.用户权限校验
        if(!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        // 3.参数校验
        if(type == null){
            log.error("订单管理-统计失败:统计类型不能为空");
            return Result.error(400,"统计类型不能为空");
        }

        // 4.日期参数处理与查询条件构造
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getOrderStatus, 3); // 订单已完成


        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;

            switch ( type){
                case "day":
                    startDateTime = LocalDate.now().atStartOfDay();
                    endDateTime = LocalDateTime.now();
                    break;
                case "week":
                    startDateTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
                    endDateTime = LocalDateTime.now();
                    break;
                case "year":
                    startDateTime = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
                    endDateTime = LocalDateTime.now();
                    break;
            }

            if(StringUtils.hasText(startTime)){
                startDateTime = LocalDateTime.parse(startTime, formatter);
            }
            if(StringUtils.hasText(endTime)){
                endDateTime = LocalDateTime.parse(endTime, formatter);
            }

            if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
                log.error("订单管理-统计失败:开始时间不能晚于结束时间");
                return Result.error(400, "开始时间不能晚于结束时间");
            }

            if(startDateTime != null){
                queryWrapper.ge(Orders::getCreateTime, startDateTime);
            }
            if(endDateTime != null){
                queryWrapper.le(Orders::getCreateTime, endDateTime);
            }
        }catch (DateTimeParseException e){
            log.error("订单管理-统计失败:日期格式错误，startTime：{}，endTime：{}，异常信息：{}",
                    startTime, endTime, e.getMessage());
            return Result.error(400, "日期格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
        }

        List<Orders> ordersList = orderMapper.selectList(queryWrapper);

        Statistics statistics = new Statistics();
        if(CollectionUtils.isEmpty(ordersList)){
            statistics.setTotalSales(BigDecimal.ZERO);
            statistics.setTotalOrders(BigDecimal.valueOf(0L));
        }else{
            long orderCount = ordersList.size();
            BigDecimal totalAmount = ordersList.stream()
                    .map(Orders::getPayAmount)
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);

            statistics.setTotalSales(totalAmount);
            statistics.setTotalOrders(BigDecimal.valueOf(orderCount));
        }

        log.info("订单管理-统计成功:用户{}的{}统计结果为{}", userId, type, statistics);
        return Result.success(statistics);
    }

    /**
     * 添加秒杀活动
     * @param userId 用户id
     * @param seckillActivityDTO 秒杀活动
     * @return 添加结果
     */
    @Override
    public Result<SeckillActivity> addSeckillActivity(Long userId, SeckillActivityDTO seckillActivityDTO) {

        if (userId == null){
            log.error("秒杀管理-添加秒杀活动失败:用户未登录");
            return Result.error(401,"用户未登录");
        }

        if(!identityUtil.confirmIdentity(userId)){
            log.error("用户 {} 无权限", userId);
            return Result.error(401,"用户无权限");
        }

        if(seckillActivityDTO.getActivityName() == null){
            log.error("秒杀管理-添加秒杀活动失败:活动名称不能为空");
            return Result.error(400,"活动名称不能为空");
        }

        if(seckillActivityDTO.getStartTime() == null){
            log.error("秒杀管理-添加秒杀活动失败:活动开始时间不能为空");
            return Result.error(400,"活动开始时间不能为空");
        }

        if(seckillActivityDTO.getEndTime() == null){
            log.error("秒杀管理-添加秒杀活动失败:活动结束时间不能为空");
            return Result.error(400,"活动结束时间不能为空");
        }

        try{

            Date startTime;
            Date endTime;

            try {
                startTime = DateUtil.strToDateTime(String.valueOf(seckillActivityDTO.getStartTime()));
                endTime = DateUtil.strToDateTime(String.valueOf(seckillActivityDTO.getEndTime()));
            }catch (Exception e){
                log.error("秒杀管理-添加秒杀活动失败:日期格式错误，startTime：{}，endTime：{}，异常信息：{}",
                        seckillActivityDTO.getStartTime(), seckillActivityDTO.getEndTime(), e.getMessage());
                return Result.error(400, "日期格式错误，请使用 yyyy-MM-dd HH:mm:ss 格式");
            }

            Date currentDate = new Date();
            if (startTime.after(endTime)) {
                log.error("秒杀管理-添加秒杀活动失败:活动开始时间[{}]晚于结束时间[{}]",
                        DateUtil.formatDateTime(startTime), DateUtil.formatDateTime(endTime));
                return Result.error(400, "活动开始时间不能晚于活动结束时间");
            }
            if (startTime.before(currentDate)) {
                log.error("秒杀管理-添加秒杀活动失败:活动开始时间[{}]早于当前时间[{}]",
                        DateUtil.formatDateTime(startTime), DateUtil.formatDateTime(currentDate));
                return Result.error(400, "活动开始时间不能早于当前时间");
            }

            // 4. 检查活动时间段内没有其他活动
            LambdaQueryWrapper<SeckillActivity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SeckillActivity::getActivityName, seckillActivityDTO.getActivityName());
            long count = seckillActivityMapper.selectCount(queryWrapper);
            if (count > 0) {
                log.error("秒杀管理-添加秒杀活动失败:活动名称[{}]已存在", seckillActivityDTO.getActivityName());
                return Result.error(400, "活动名称已存在");
            }

            // 5.  DTO 转换为 DO（SeckillActivity 是数据库实体类）
            SeckillActivity seckillActivity = new SeckillActivity();
            BeanUtils.copyProperties(seckillActivityDTO, seckillActivity);
            seckillActivity.setCreateTime(new Date());
            seckillActivity.setUpdateTime(new Date());
            seckillActivity.setStatus(0);
            seckillActivity.setCreateTime(currentDate);
            seckillActivity.setUpdateTime(currentDate);

            // 6. 秒杀活动插入数据库
            seckillActivityMapper.insert(seckillActivity);
            Long activityId = seckillActivity.getId();

            if (activityId == null) {
                log.error("秒杀管理-添加秒杀活动失败:插入活动主表后，未获取到活动ID");
                return Result.error(500, "插入秒杀活动主表失败");
            }

            // 6. 处理秒杀商品列表
            List<generator.domain.entity.SeckillProduct> seckillProductList = new ArrayList<>();
            if(seckillActivityDTO.getProducts() != null && !seckillActivityDTO.getProducts().isEmpty()){

                for(SeckillProductDTO seckillProductDTO : seckillActivityDTO.getProducts()){
                    SeckillProductVO seckillProductVO = getSeckillProduct(seckillProductDTO, seckillActivity);
                    seckillProductVO.setActivityId(activityId);
                    // 补充商品初始库存字段（避免非空报错，符合业务逻辑）
                    seckillProductVO.setSurplusStock(seckillProductVO.getTotalStock());
                    seckillProductVO.setSold(0L);
                    // 补充商品创建/更新时间（如果表中有该字段）
                    seckillProductVO.setCreateTime(currentDate);
                    seckillProductVO.setUpdateTime(currentDate);
                    seckillProductList.add(seckillProductVO);
                }

                try {
                    seckillProductMapper.insertBatch(seckillProductList);
                }catch (Exception e){
                    log.error("秒杀管理-添加秒杀活动失败:批量插入秒杀商品列表失败，异常信息：{}", e.getMessage());
                    return Result.error(500, "批量插入秒杀商品列表失败");
                }
            }

            log.info("秒杀管理-添加秒杀活动成功:{}", seckillActivity);
            return Result.success(seckillActivity);

        }catch (Exception e){
            log.error("秒杀管理-添加秒杀活动失败:{}", e.getMessage());
            return Result.error(500, "系统异常,添加秒杀活动失败");
        }
    }

    private static SeckillProductVO getSeckillProduct(SeckillProductDTO seckillProductDTO, SeckillActivity seckillActivity) {
        SeckillProductVO seckillProductVO = new SeckillProductVO();
        BeanUtils.copyProperties(seckillProductDTO, seckillProductVO);
        return seckillProductVO;
    }
}
