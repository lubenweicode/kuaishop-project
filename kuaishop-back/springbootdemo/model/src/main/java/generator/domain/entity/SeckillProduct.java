package generator.domain.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 秒杀商品关联表
 *
 * @TableName seckill_product
 */
@Data
@TableName(value = "seckill_product")
public class SeckillProduct implements Serializable {

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 关联ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty("关联ID")
    private Long id;
    /**
     * 秒杀活动ID
     */
    @ApiModelProperty("秒杀活动ID")
    @TableField(value = "activity_id")
    private Long activityId;
    /**
     * 商品ID（关联商品主表）
     */
    @ApiModelProperty("商品ID（关联商品主表）")
    @TableField(value = "product_id")
    private Long productId;
    /**
     * 商品名称（冗余存储，避免关联查询）
     */
    @ApiModelProperty("商品名称（冗余存储，避免关联查询）")
    @TableField(value = "product_name")
    private String productName;
    /**
     * 秒杀价格
     */
    @ApiModelProperty("秒杀价格")
    @TableField(value = "seckill_price")
    private BigDecimal seckillPrice;
    /**
     * 商品原价
     */
    @ApiModelProperty("商品原价")
    @TableField(value = "original_price")
    private BigDecimal originalPrice;
    /**
     * 总库存
     */
    @ApiModelProperty("总库存")
    @TableField(value = "total_stock")
    private Long totalStock;
    /**
     * 剩余库存（核心字段，高并发更新）
     */
    @ApiModelProperty("剩余库存（核心字段，高并发更新）")
    @TableField(value = "surplus_stock")
    private Long surplusStock;
    /**
     * 已售数量
     */
    @ApiModelProperty("已售数量")
    @TableField(value = "sold")
    private Long sold;
    /**
     * 每人限购数量
     */
    @ApiModelProperty("每人限购数量")
    @TableField(value = "limit_per_user")
    private Integer limitPerUser;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "update_time")
    private Date updateTime;

    public String toInfo() {

        Map<String, Object> map = new HashMap<>();
        map.put("productId", productId);
        map.put("productName", productName);
        map.put("price", seckillPrice);
        map.put("quantity", 1);
        return JSON.toJSONString(map);
    }
}