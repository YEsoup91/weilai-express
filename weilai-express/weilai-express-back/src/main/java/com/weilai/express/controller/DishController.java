package com.weilai.express.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weilai.express.entity.Dish;
import com.weilai.express.service.DishService;
import com.weilai.express.vo.DishVO;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "菜品管理")
@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    @Operation(summary = "获取菜品列表")
    public Result<List<DishVO>> list(@RequestParam(required = false) Long merchantId,
                                     @RequestParam(required = false) Long categoryId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<Dish>()
            .eq(Dish::getStatus, 1);
        
        if (merchantId != null) {
            wrapper.eq(Dish::getMerchantId, merchantId);
        }
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        
        List<Dish> dishes = dishService.list(wrapper);
        
        List<DishVO> result = dishes.stream().map(dish -> {
            DishVO vo = new DishVO();
            BeanUtils.copyProperties(dish, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取菜品详情")
    public Result<DishVO> detail(@PathVariable Long id) {
        Dish dish = dishService.getById(id);
        
        if (dish == null) {
            return Result.error(404, "菜品不存在");
        }
        
        DishVO vo = new DishVO();
        BeanUtils.copyProperties(dish, vo);
        
        return Result.success(vo);
    }
}
