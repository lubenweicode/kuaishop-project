package generator.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;

/**
* 用户表
* @TableName user
*/
@Data
public class User implements Serializable {

    /**
    * 用户ID
    */
    @ApiModelProperty("用户ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
    * 用户名
    */
    @NotBlank(message="[用户名]不能为空")
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("用户名")
    @Length(max= 50,message="编码长度不能超过50")
    private String username;
    /**
    * 加密密码（BCrypt）
    */
    @NotBlank(message="[加密密码（BCrypt）]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("加密密码（BCrypt）")
    @Length(max= 255,message="编码长度不能超过255")
    private String password;
    /**
    * 昵称
    */
    @Size(max= 50,message="编码长度不能超过50")
    @ApiModelProperty("昵称")
    @Length(max= 50,message="编码长度不能超过50")
    private String nickname;
    /**
    * 头像URL
    */
    @Size(max= 255,message="编码长度不能超过255")
    @ApiModelProperty("头像URL")
    @Length(max= 255,message="编码长度不能超过255")
    private String avatar;
    /**
    * 手机号
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("手机号")
    @Length(max= 20,message="编码长度不能超过20")
    private String phone;
    /**
    * 邮箱
    */
    @Size(max= 100,message="编码长度不能超过100")
    @ApiModelProperty("邮箱")
    @Length(max= 100,message="编码长度不能超过100")
    private String email;
    /**
    * 性别：0-未知 1-男 2-女
    */
    @ApiModelProperty("性别：0-未知 1-男 2-女")
    private Integer gender;
    /**
    * 状态：0-禁用 1-正常
    */
    @ApiModelProperty("状态：0-禁用 1-正常")
    private Integer status;
    /**
    * 最后登录时间
    */
    @ApiModelProperty("最后登录时间")
    private Date lastLoginTime;
    /**
    * 最后登录IP
    */
    @Size(max= 45,message="编码长度不能超过45")
    @ApiModelProperty("最后登录IP")
    @Length(max= 45,message="编码长度不能超过45")
    private String lastLoginIp;
    /**
    * 注册时间
    */
    @ApiModelProperty("注册时间")
    private Date createTime;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updateTime;


}
