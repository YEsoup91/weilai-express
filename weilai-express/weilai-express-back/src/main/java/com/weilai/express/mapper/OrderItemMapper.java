package com.weilai.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weilai.express.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
