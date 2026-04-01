package com.weilai.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weilai.express.entity.Dish;
import com.weilai.express.entity.DishCategory;

import java.util.List;

public interface DishService extends IService<Dish> {
    List<Dish> listByMerchant(Long merchantId);
    List<DishCategory> listCategoriesByMerchant(Long merchantId);
}
