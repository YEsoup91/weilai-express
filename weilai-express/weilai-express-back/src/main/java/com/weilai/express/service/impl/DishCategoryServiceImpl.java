package com.weilai.express.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weilai.express.entity.DishCategory;
import com.weilai.express.mapper.DishCategoryMapper;
import com.weilai.express.service.DishCategoryService;
import org.springframework.stereotype.Service;

@Service
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements DishCategoryService {
}
