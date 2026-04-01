package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DishVO {
    private Long id;
    private Long categoryId;
    private String categoryName;  // 分类名称（关联查询）
    private Long merchantId;
    private String merchantName;  // 商家名称（关联查询）
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer salesCount;
    private BigDecimal rating;
    private Integer status;
}
