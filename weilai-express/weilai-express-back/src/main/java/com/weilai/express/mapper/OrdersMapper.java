package com.weilai.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weilai.express.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
