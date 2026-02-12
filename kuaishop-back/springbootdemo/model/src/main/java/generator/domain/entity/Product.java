package generator.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品表
 *
 * @TableName product
 */
@Data
@TableName(value = "product", autoResultMap = true)
public class Product implements Serializable {

    /**
     * 商品ID
     */
    @NotNull(message = "[商品ID]不能为空")
    @ApiModelProperty("商品ID")
    private Long id;

    /**
     * 商品名称
     */
    @ApiModelProperty("商品名称")
    @NotBlank(message = "[商品名称]不能为空")
    @Length(max = 200, message = "编码长度不能超过200")
    @TableField(value = "name")
    private String name;

    /**
     * 商品描述
     */
    @ApiModelProperty("商品描述")
    @Length(max = 2000, message = "编码长度不能超过2000")
    @TableField(value = "description")
    private String description;

    /**
     * 商品详情
     */
    @ApiModelProperty("商品详情")
    @Length(max = 2000, message = "编码长度不能超过2000")
    @TableField(value = "detail")
    private String detail;

    /**
     * 现价
     */
    @ApiModelProperty("现价")
    @NotNull(message = "[现价]不能为空")
    @TableField(value = "price")
    private BigDecimal price;
    /**
     * 原价（划线价）
     */
    @ApiModelProperty("原价（划线价）")
    @TableField(value = "original_price")
    private BigDecimal originalPrice;
    /**
     * 库存
     */
    @ApiModelProperty("库存")
    @TableField(value = "stock")
    private Integer stock;
    /**
     * 销量
     */
    @ApiModelProperty("销量")
    @TableField(value = "sales")
    private Integer sales;
    /**
     * 分类ID
     */
    @NotNull(message = "[分类ID]不能为空")
    @ApiModelProperty("分类ID")
    private Long categoryId;
    /**
     * 主图URL
     */
    @ApiModelProperty("主图URL")
    @NotBlank(message = "[主图URL]不能为空")
    @Length(max = 255, message = "编码长度不能超过255")
    @TableField(value = "main_image")
    private String mainImage;
    /**
     * 详情图数组
     */
    @ApiModelProperty("详情图数组")
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> images;
    /**
     * 规格参数
     */
    @ApiModelProperty("规格参数")
    @TableField(value = "specifications")
    private List<Map<String, String>> specifications;
    /**
     * 状态：0-下架 1-上架
     */
    @ApiModelProperty("状态：0-下架 1-上架")
    @TableField(value = "status")
    private Integer status;
    /**
     * 是否热销
     */
    @ApiModelProperty("是否热销")
    @TableField(value = "is_hot")
    private Integer isHot;
    /**
     * 是否新品
     */
    @ApiModelProperty("是否新品")
    @TableField(value = "is_new")
    private Integer isNew;
    /**
     * 浏览数
     */
    @ApiModelProperty("浏览数")
    @TableField(value = "view_count")
    private Integer viewCount;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
