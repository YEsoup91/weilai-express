package com.weilai.express.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    
    private Long userId;
    
    private Long merchantId;
    
    @NotEmpty(message = "订单商品不能为空")
    private List<OrderItemDTO> items;
    
    @NotBlank(message = "配送地址不能为空")
    private String deliveryAddress;
    
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;
    
    private String remark;
    
    private BigDecimal discountAmount;
    
    @Data
    public static class OrderItemDTO {
        private Long dishId;
        private Integer quantity;
        private BigDecimal price;
    }
}
