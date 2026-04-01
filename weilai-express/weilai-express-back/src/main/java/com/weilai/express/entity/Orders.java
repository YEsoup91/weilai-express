package com.weilai.express.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders implements Serializable {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long merchantId;
    
    private Long riderId;
    
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
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime finishTime;
    
    private String cancelReason;
    
    @TableLogic
    private Integer deleted;
}
