package generator.domain.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterAuthDTO {

    // 用户名：必填，3-20字符（字母、数字、下划线）
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]{3,20}$", message = "用户名需为3-20字符，支持字母、数字、下划线、中文")
    private String username;

    // 密码：必填，6-20字符（强校验：必须包含字母+数字）
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()_+-=]{6,20}$", message = "密码需为6-20字符，支持字母、数字、!@#$%^&*()_+-=")
    private String password;

    // 手机号：可选，但填了就必须符合格式
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    // 邮箱：可选，但填了就必须符合格式（也可以用@Email注解，更简洁）
    @Email(message = "邮箱格式不正确")
    private String email;
}
