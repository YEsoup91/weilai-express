package com.weilai.express.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Long roleId;
    private String roleName;
    private String roleCode;
    private Integer status;
    private LocalDateTime createTime;
}
