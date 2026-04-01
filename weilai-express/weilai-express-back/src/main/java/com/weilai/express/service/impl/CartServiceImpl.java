package com.weilai.express.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weilai.express.entity.Cart;
import com.weilai.express.exception.BusinessException;
import com.weilai.express.mapper.CartMapper;
import com.weilai.express.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public List<Cart> getByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(Cart cart) {
        Cart existCart = this.getOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getDishId, cart.getDishId()));
        
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + cart.getQuantity());
            this.updateById(existCart);
        } else {
            this.save(cart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Cart cart) {
        if (cart.getQuantity() <= 0) {
            this.removeById(cart.getId());
        } else {
            this.updateById(cart);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFromCart(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        this.remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));
    }
}
