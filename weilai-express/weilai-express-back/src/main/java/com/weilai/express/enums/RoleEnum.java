package com.weilai.express.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    USER(1, "普通用户"),
    MERCHANT(2, "商家"),
    RIDER(3, "骑手"),
    ADMIN(4, "管理员");

    private final Integer code;
    private final String desc;

    RoleEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
