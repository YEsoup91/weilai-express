package com.weilai.express.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weilai.express.entity.Cart;
import com.weilai.express.entity.Dish;
import com.weilai.express.service.CartService;
import com.weilai.express.service.DishService;
import com.weilai.express.vo.CartItemVO;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "购物车管理")
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final DishService dishService;

    public CartController(CartService cartService, DishService dishService) {
        this.cartService = cartService;
        this.dishService = dishService;
    }

    @GetMapping
    @Operation(summary = "获取购物车列表")
    public Result<List<CartItemVO>> getCart(@RequestParam Long userId) {
        List<Cart> carts = cartService.list(
            new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
        );
        
        List<CartItemVO> result = carts.stream().map(cart -> {
            CartItemVO vo = new CartItemVO();
            BeanUtils.copyProperties(cart, vo);
            
            // 关联查询菜品信息
            Dish dish = dishService.getById(cart.getDishId());
            if (dish != null) {
                vo.setDishName(dish.getName());
                vo.setDishImage(dish.getImage());
                vo.setPrice(dish.getPrice());
                vo.setMerchantId(dish.getMerchantId());
            }
            
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "添加到购物车")
    public Result<Void> addToCart(@RequestBody Cart cart) {
        // 检查是否已存在
        Cart existCart = cartService.getOne(
            new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getDishId, cart.getDishId())
        );
        
        if (existCart != null) {
            // 数量累加
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            cartService.updateById(existCart);
        } else {
            cartService.save(cart);
        }
        
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新购物车项")
    public Result<Void> updateCart(@PathVariable Long id, @RequestBody Cart cart) {
        cart.setId(id);
        cartService.updateById(cart);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除购物车项")
    public Result<Void> deleteCart(@PathVariable Long id) {
        cartService.removeById(id);
        return Result.success();
    }

    @DeleteMapping("/clear/{userId}")
    @Operation(summary = "清空购物车")
    public Result<Void> clearCart(@PathVariable Long userId) {
        cartService.remove(
            new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
        );
        return Result.success();
    }
}
