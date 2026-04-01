package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemVO {
    private Long id;
    private Long userId;
    private Long dishId;
    private Integer quantity;
    
    // 菜品信息（关联查询）
    private String dishName;
    private String dishImage;
    private BigDecimal price;
    private Long merchantId;
}
