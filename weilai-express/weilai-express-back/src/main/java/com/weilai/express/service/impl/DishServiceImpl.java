package com.weilai.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weilai.express.entity.Dish;
import com.weilai.express.entity.DishCategory;
import com.weilai.express.mapper.DishMapper;
import com.weilai.express.service.DishCategoryService;
import com.weilai.express.service.DishService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final DishCategoryService dishCategoryService;

    public DishServiceImpl(DishCategoryService dishCategoryService) {
        this.dishCategoryService = dishCategoryService;
    }

    @Override
    public List<Dish> listByMerchant(Long merchantId) {
        return this.list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getMerchantId, merchantId)
                .eq(Dish::getStatus, 1));
    }

    @Override
    public List<DishCategory> listCategoriesByMerchant(Long merchantId) {
        return dishCategoryService.list(new LambdaQueryWrapper<DishCategory>()
                .eq(DishCategory::getMerchantId, merchantId)
                .orderByAsc(DishCategory::getSort));
    }
}
