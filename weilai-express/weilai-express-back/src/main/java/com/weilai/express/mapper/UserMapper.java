package com.weilai.express.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weilai.express.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
