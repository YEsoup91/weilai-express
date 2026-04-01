package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderListVO {
    private Long id;
    private String orderNo;
    private Long merchantId;
    private String merchantName;
    
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal payAmount;
    
    private Integer status;
    private LocalDateTime createTime;
    
    // 菜品数量（用于显示）
    private Integer itemCount;
}
