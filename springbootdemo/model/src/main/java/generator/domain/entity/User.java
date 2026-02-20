package generator.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user
 */
@Data
@TableName(value = "user")
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
    @NotBlank(message = "[用户名]不能为空")
    @ApiModelProperty("用户名")
    @Length(max = 50, message = "编码长度不能超过50")
    private String username;

    /**
     * 加密密码（BCrypt）
     */
    @NotBlank(message = "[加密密码（BCrypt）]不能为空")
    @ApiModelProperty("加密密码（BCrypt）")
    @Length(max = 255, message = "编码长度不能超过255")
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @Size(max = 50, message = "编码长度不能超过50")
    @ApiModelProperty("昵称")
    @Length(max = 50, message = "编码长度不能超过50")
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     */
    @ApiModelProperty("头像URL")
    @Length(max = 255, message = "编码长度不能超过255")
    @TableField("avatar")
    private String avatar;
    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @Length(max = 20, message = "编码长度不能超过20")
    @TableField("phone")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @Length(max = 100, message = "编码长度不能超过100")
    @TableField("email")
    private String email;

    /**
     * 性别：0-未知 1-男 2-女
     */
    @ApiModelProperty("性别：0-未知 1-男 2-女")
    @Length(max = 1, message = "编码长度不能超过1")
    @TableField("gender")
    private Integer gender;

    /**
     * 状态：0-禁用 1-正常
     */
    @ApiModelProperty("状态：0-禁用 1-正常")
    @Length(max = 1, message = "编码长度不能超过1")
    @TableField("status")
    private Integer status;

    /**
     * 身份
     */
    @ApiModelProperty("身份")
    @Length(max = 1, message = "编码长度不能超过1")
    @NotBlank(message = "[身份]不能为空")
    @TableField("identity")
    private Integer identity;

    /**
     * 密码盐
     */
    @ApiModelProperty("密码盐")
    @Length(max = 500, message = "编码长度不能超过500")
    @NotBlank(message = "[密码盐]不能为空")
    @TableField("salt")
    private String salt;

    /**
     * 最后登录时间
     */
    @ApiModelProperty("最后登录时间")
    @TableField("last_login_time")
    @Length(max = 20, message = "编码长度不能超过20")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @ApiModelProperty("最后登录IP")
    @Length(max = 45, message = "编码长度不能超过45")
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 注册时间
     */
    @ApiModelProperty("注册时间")
    @NotBlank(message = "[注册时间]不能为空")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

}
