package generator.domain.entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 商品表
* @TableName product
*/
@Data
public class Product implements Serializable {

    /**
    * 商品ID
    */
    @NotNull(message="[商品ID]不能为空")
    @ApiModelProperty("商品ID")
    private Long id;
    /**
    * 商品名称
    */
    @NotBlank(message="[商品名称]不能为空")
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("商品名称")
    @Length(max= 200,message="编码长度不能超过200")
    private String name;
    /**
    * 商品描述
    */
    @Size(max= 2000,message="编码长度不能超过2000")
    @ApiModelProperty("商品描述")
    @Length(max= 2000,message="编码长度不能超过2000")
    private String description;

    /**
    * 商品详情
    */
    @Size(max= 2000,message="编码长度不能超过2000")
    @ApiModelProperty("商品详情")
    @Length(max= 2000,message="编码长度不能超过2000")
    private String detail;

    /**
    * 现价
    */
    @NotNull(message="[现价]不能为空")
    @ApiModelProperty("现价")
    private BigDecimal price;
    /**
    * 原价（划线价）
    */
    @ApiModelProperty("原价（划线价）")
    private BigDecimal originalPrice;
    /**
    * 库存
    */
    @ApiModelProperty("库存")
    private Integer stock;
    /**
    * 销量
    */
    @ApiModelProperty("销量")
    private Integer sales;
    /**
    * 分类ID
    */
    @NotNull(message="[分类ID]不能为空")
    @ApiModelProperty("分类ID")
    private Long categoryId;
    /**
    * 主图URL
    */
    @NotBlank(message="[主图URL]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("主图URL")
    @Length(max= 255,message="编码长度不能超过255")
    private String mainImage;
    /**
    * 详情图数组
    */
    @ApiModelProperty("详情图数组")
    private Object images;
    /**
    * 规格参数
    */
    @ApiModelProperty("规格参数")
    private Object specifications;
    /**
    * 状态：0-下架 1-上架
    */
    @ApiModelProperty("状态：0-下架 1-上架")
    private Integer status;
    /**
    * 是否热销
    */
    @ApiModelProperty("是否热销")
    private Integer isHot;
    /**
    * 是否新品
    */
    @ApiModelProperty("是否新品")
    private Integer isNew;
    /**
    * 浏览数
    */
    @ApiModelProperty("浏览数")
    private Integer viewCount;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
