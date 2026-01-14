package generator.domain.auth;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class RegisterAuthVO {

    private Long id;
    private String username;
    private String phone;
    private Date createTime;
}
