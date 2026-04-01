package com.weilai.express.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {
    PENDING_PAYMENT(0, "待支付"),
    PENDING_PRODUCTION(1, "待制作"),
    PENDING_DELIVERY(2, "待配送"),
    DELIVERING(3, "配送中"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(Integer code) {
        for (OrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDesc();
            }
        }
        return "未知状态";
    }
}
