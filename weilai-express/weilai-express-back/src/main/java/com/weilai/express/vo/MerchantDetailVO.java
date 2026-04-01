package com.weilai.express.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MerchantDetailVO {
    private Long id;
    private String name;
    private String logo;
    private String coverImage;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private String businessHours;
    private BigDecimal deliveryFee;
    private BigDecimal minOrderAmount;
    private BigDecimal rating;
    private Integer salesCount;
    private Integer status;
}
