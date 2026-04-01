package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantListVO {
    private Long id;
    private String name;
    private String logo;
    private String coverImage;
    private String description;
    private String address;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private BigDecimal rating;
    private Integer salesCount;
    private Integer status;
}
