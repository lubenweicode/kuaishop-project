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
    public void setId(Long id){
    this.id = id;
    }

    /**
    * 商品名称
    */
    public void setName(String name){
    this.name = name;
    }

    /**
    * 商品描述
    */
    public void setDescription(String description){
    this.description = description;
    }

    /**
    * 现价
    */
    public void setPrice(BigDecimal price){
    this.price = price;
    }

    /**
    * 原价（划线价）
    */
    public void setOriginalPrice(BigDecimal originalPrice){
    this.originalPrice = originalPrice;
    }

    /**
    * 库存
    */
    public void setStock(Integer stock){
    this.stock = stock;
    }

    /**
    * 销量
    */
    public void setSales(Integer sales){
    this.sales = sales;
    }

    /**
    * 分类ID
    */
    public void setCategoryId(Long categoryId){
    this.categoryId = categoryId;
    }

    /**
    * 主图URL
    */
    public void setMainImage(String mainImage){
    this.mainImage = mainImage;
    }

    /**
    * 详情图数组
    */
    public void setImages(Object images){
    this.images = images;
    }

    /**
    * 规格参数
    */
    public void setSpecifications(Object specifications){
    this.specifications = specifications;
    }

    /**
    * 状态：0-下架 1-上架
    */
    public void setStatus(Integer status){
    this.status = status;
    }

    /**
    * 是否热销
    */
    public void setIsHot(Integer isHot){
    this.isHot = isHot;
    }

    /**
    * 是否新品
    */
    public void setIsNew(Integer isNew){
    this.isNew = isNew;
    }

    /**
    * 浏览数
    */
    public void setViewCount(Integer viewCount){
    this.viewCount = viewCount;
    }

    /**
    * 创建时间
    */
    public void setCreateTime(Date createTime){
    this.createTime = createTime;
    }

    /**
    * 更新时间
    */
    public void setUpdateTime(Date updateTime){
    this.updateTime = updateTime;
    }


    /**
    * 商品ID
    */
    public Long getId(){
    return this.id;
    }

    /**
    * 商品名称
    */
    public String getName(){
    return this.name;
    }

    /**
    * 商品描述
    */
    public String getDescription(){
    return this.description;
    }

    /**
    * 现价
    */
    public BigDecimal getPrice(){
    return this.price;
    }

    /**
    * 原价（划线价）
    */
    public BigDecimal getOriginalPrice(){
    return this.originalPrice;
    }

    /**
    * 库存
    */
    public Integer getStock(){
    return this.stock;
    }

    /**
    * 销量
    */
    public Integer getSales(){
    return this.sales;
    }

    /**
    * 分类ID
    */
    public Long getCategoryId(){
    return this.categoryId;
    }

    /**
    * 主图URL
    */
    public String getMainImage(){
    return this.mainImage;
    }

    /**
    * 详情图数组
    */
    public Object getImages(){
    return this.images;
    }

    /**
    * 规格参数
    */
    public Object getSpecifications(){
    return this.specifications;
    }

    /**
    * 状态：0-下架 1-上架
    */
    public Integer getStatus(){
    return this.status;
    }

    /**
    * 是否热销
    */
    public Integer getIsHot(){
    return this.isHot;
    }

    /**
    * 是否新品
    */
    public Integer getIsNew(){
    return this.isNew;
    }

    /**
    * 浏览数
    */
    public Integer getViewCount(){
    return this.viewCount;
    }

    /**
    * 创建时间
    */
    public Date getCreateTime(){
    return this.createTime;
    }

    /**
    * 更新时间
    */
    public Date getUpdateTime(){
    return this.updateTime;
    }

}
