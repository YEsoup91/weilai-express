package com.weilai.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weilai.express.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
