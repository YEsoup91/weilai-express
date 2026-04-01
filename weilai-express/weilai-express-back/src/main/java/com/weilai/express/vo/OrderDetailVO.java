package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String userName;
    private Long merchantId;
    private String merchantName;
    private Long riderId;
    private String riderName;
    
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
    private LocalDateTime finishTime;
    private String cancelReason;
    
    // 订单项列表
    private List<OrderItemVO> items;
}
