package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdersVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountAmount;
    private BigDecimal payAmount;
    private Integer status;
    private Integer paymentMethod;
    private LocalDateTime paymentTime;
    private String deliveryAddress;
    private String receiverName;
    private String receiverPhone;
    private String remark;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;
}
