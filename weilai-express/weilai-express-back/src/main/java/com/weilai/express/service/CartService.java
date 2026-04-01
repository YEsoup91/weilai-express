package com.weilai.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weilai.express.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    List<Cart> getByUserId(Long userId);
    void addToCart(Cart cart);
    void updateQuantity(Cart cart);
    void deleteFromCart(Long id);
    void clearCart(Long userId);
}
