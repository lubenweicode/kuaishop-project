package generator.domain.Entity;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
* 商品分类表
* @TableName product_category
*/
@Data
public class ProductCategory implements Serializable {

    /**
    * 分类ID
    */
    @NotNull(message="[分类ID]不能为空")
    @ApiModelProperty("分类ID")
    private Long id;
    /**
    * 分类名称
    */
    @NotBlank(message="[分类名称]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("分类名称")
    @Length(max= 50,message="编码长度不能超过50")
    private String name;
    /**
    * 父分类ID，0表示一级分类
    */
    @ApiModelProperty("父分类ID，0表示一级分类")
    private Long parentId;
    /**
    * 分类层级：1-一级分类 2-二级分类
    */
    @ApiModelProperty("分类层级：1-一级分类 2-二级分类")
    private Integer level;
    /**
    * 分类图标（可存图标URL或emoji）
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("分类图标（可存图标URL或emoji）")
    @Length(max= 100,message="编码长度不能超过100")
    private String icon;
    /**
    * 排序值，越小越靠前
    */
    @ApiModelProperty("排序值，越小越靠前")
    private Integer sortOrder;
    /**
    * 状态：0-禁用 1-启用
    */
    @ApiModelProperty("状态：0-禁用 1-启用")
    private Integer status;
    /**
    * 分类描述
    */
    @Size(max= 200,message="编码长度不能超过200")
    @ApiModelProperty("分类描述")
    @Length(max= 200,message="编码长度不能超过200")
    private String description;
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
