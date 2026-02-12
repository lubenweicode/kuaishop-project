package generator.domain.auth;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserInfoVO {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private LocalDateTime createTime;
}
