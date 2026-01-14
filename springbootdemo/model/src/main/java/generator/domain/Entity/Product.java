package generator.domain.Entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
* 商品表
* @TableName product
*/
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

    /**
    * 商品ID
    */
    private void setId(Long id){
    this.id = id;
    }

    /**
    * 商品名称
    */
    private void setName(String name){
    this.name = name;
    }

    /**
    * 商品描述
    */
    private void setDescription(String description){
    this.description = description;
    }

    /**
    * 现价
    */
    private void setPrice(BigDecimal price){
    this.price = price;
    }

    /**
    * 原价（划线价）
    */
    private void setOriginalPrice(BigDecimal originalPrice){
    this.originalPrice = originalPrice;
    }

    /**
    * 库存
    */
    private void setStock(Integer stock){
    this.stock = stock;
    }

    /**
    * 销量
    */
    private void setSales(Integer sales){
    this.sales = sales;
    }

    /**
    * 分类ID
    */
    private void setCategoryId(Long categoryId){
    this.categoryId = categoryId;
    }

    /**
    * 主图URL
    */
    private void setMainImage(String mainImage){
    this.mainImage = mainImage;
    }

    /**
    * 详情图数组
    */
    private void setImages(Object images){
    this.images = images;
    }

    /**
    * 规格参数
    */
    private void setSpecifications(Object specifications){
    this.specifications = specifications;
    }

    /**
    * 状态：0-下架 1-上架
    */
    private void setStatus(Integer status){
    this.status = status;
    }

    /**
    * 是否热销
    */
    private void setIsHot(Integer isHot){
    this.isHot = isHot;
    }

    /**
    * 是否新品
    */
    private void setIsNew(Integer isNew){
    this.isNew = isNew;
    }

    /**
    * 浏览数
    */
    private void setViewCount(Integer viewCount){
    this.viewCount = viewCount;
    }

    /**
    * 创建时间
    */
    private void setCreateTime(Date createTime){
    this.createTime = createTime;
    }

    /**
    * 更新时间
    */
    private void setUpdateTime(Date updateTime){
    this.updateTime = updateTime;
    }


    /**
    * 商品ID
    */
    private Long getId(){
    return this.id;
    }

    /**
    * 商品名称
    */
    private String getName(){
    return this.name;
    }

    /**
    * 商品描述
    */
    private String getDescription(){
    return this.description;
    }

    /**
    * 现价
    */
    private BigDecimal getPrice(){
    return this.price;
    }

    /**
    * 原价（划线价）
    */
    private BigDecimal getOriginalPrice(){
    return this.originalPrice;
    }

    /**
    * 库存
    */
    private Integer getStock(){
    return this.stock;
    }

    /**
    * 销量
    */
    private Integer getSales(){
    return this.sales;
    }

    /**
    * 分类ID
    */
    private Long getCategoryId(){
    return this.categoryId;
    }

    /**
    * 主图URL
    */
    private String getMainImage(){
    return this.mainImage;
    }

    /**
    * 详情图数组
    */
    private Object getImages(){
    return this.images;
    }

    /**
    * 规格参数
    */
    private Object getSpecifications(){
    return this.specifications;
    }

    /**
    * 状态：0-下架 1-上架
    */
    private Integer getStatus(){
    return this.status;
    }

    /**
    * 是否热销
    */
    private Integer getIsHot(){
    return this.isHot;
    }

    /**
    * 是否新品
    */
    private Integer getIsNew(){
    return this.isNew;
    }

    /**
    * 浏览数
    */
    private Integer getViewCount(){
    return this.viewCount;
    }

    /**
    * 创建时间
    */
    private Date getCreateTime(){
    return this.createTime;
    }

    /**
    * 更新时间
    */
    private Date getUpdateTime(){
    return this.updateTime;
    }

}
