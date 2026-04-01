package com.weilai.express.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weilai.express.dto.OrderDTO;
import com.weilai.express.entity.*;
import com.weilai.express.exception.BusinessException;
import com.weilai.express.mapper.OrdersMapper;
import com.weilai.express.service.*;
import com.weilai.express.vo.OrderDetailVO;
import com.weilai.express.vo.OrderItemVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    private final CartService cartService;
    private final DishService dishService;
    private final OrderItemService orderItemService;
    private final MerchantService merchantService;

    public OrderServiceImpl(CartService cartService, DishService dishService,
                           OrderItemService orderItemService, MerchantService merchantService) {
        this.cartService = cartService;
        this.dishService = dishService;
        this.orderItemService = orderItemService;
        this.merchantService = merchantService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(OrderDTO orderDTO) {
        // 验证商家是否存在
        Merchant merchant = merchantService.getById(orderDTO.getMerchantId());
        if (merchant == null) {
            throw new BusinessException("商家不存在");
        }

        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDTO.OrderItemDTO> items = orderDTO.getItems();

        for (OrderDTO.OrderItemDTO item : items) {
            Dish dish = dishService.getById(item.getDishId());
            if (dish == null || dish.getStatus() != 1) {
                throw new BusinessException("菜品 " + item.getDishId() + " 不存在或已下架");
            }

            BigDecimal itemTotal = dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        // 创建订单
        Orders orders = new Orders();
        orders.setOrderNo(generateOrderNo());
        orders.setUserId(orderDTO.getUserId());
        orders.setMerchantId(orderDTO.getMerchantId());
        orders.setTotalAmount(totalAmount);
        orders.setDeliveryFee(merchant.getDeliveryFee());
        orders.setDiscountAmount(orderDTO.getDiscountAmount() != null ? orderDTO.getDiscountAmount() : BigDecimal.ZERO);
        orders.setPayAmount(totalAmount.add(merchant.getDeliveryFee()).subtract(orders.getDiscountAmount()));
        orders.setStatus(0); // 待支付
        orders.setDeliveryAddress(orderDTO.getDeliveryAddress());
        orders.setReceiverName(orderDTO.getReceiverName());
        orders.setReceiverPhone(orderDTO.getReceiverPhone());
        orders.setRemark(orderDTO.getRemark());

        this.save(orders);

        // 保存订单项
        saveOrderItems(orders, items);

        // 清空购物车
        cartService.clearCart(orderDTO.getUserId());

        return orders;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        OrderDetailVO ordersVO = new OrderDetailVO();
        BeanUtils.copyProperties(orders, ordersVO);

        // 查询订单项
        List<OrderItem> orderItems = orderItemService.list(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId)
        );
        
        List<OrderItemVO> itemVOs = orderItems.stream().map(item -> {
            OrderItemVO vo = new OrderItemVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        
        ordersVO.setItems(itemVOs);
        return ordersVO;
    }

    @Override
    public List<Orders> listByUser(Long userId) {
        return this.list(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Integer paymentMethod) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        if (orders.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        orders.setStatus(1); // 待制作 - 已支付，等待商家接单
        orders.setPaymentMethod(paymentMethod);
        orders.setPaymentTime(LocalDateTime.now());
        this.updateById(orders);
        
        System.out.println("订单 " + orderId + " 已支付，状态：待制作");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmOrder(Long orderId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        if (orders.getStatus() != 1) { // 只有待制作状态才能确认
            throw new BusinessException("订单状态不正确，无法接单");
        }

        orders.setStatus(2); // 待配送 - 商家已接单并制作完成
        this.updateById(orders);
        
        System.out.println("订单 " + orderId + " 已接单，状态：待配送");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRider(Long orderId, Long riderId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        if (orders.getStatus() != 2 && orders.getStatus() != 3) { 
            throw new BusinessException("订单状态不正确，无法分配骑手");
        }

        orders.setRiderId(riderId);
        orders.setStatus(3); // 配送中 - 骑手已接单
        this.updateById(orders);
        
        System.out.println("订单 " + orderId + " 已分配给骑手 " + riderId + "，状态：配送中");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeOrder(Long orderId) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        if (orders.getStatus() != 3) { // 只有配送中才能完成
            throw new BusinessException("订单状态不正确，无法完成");
        }

        orders.setStatus(4); // 已完成 - 骑手已送达
        orders.setFinishTime(LocalDateTime.now());
        this.updateById(orders);
        
        System.out.println("订单 " + orderId + " 已完成，状态：已完成");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, String reason) {
        Orders orders = this.getById(orderId);
        if (orders == null) {
            throw new BusinessException("订单不存在");
        }

        if (orders.getStatus() != 0 && orders.getStatus() != 1) {
            throw new BusinessException("订单状态不允许取消");
        }

        orders.setStatus(5); // 已取消
        orders.setCancelReason(reason);
        this.updateById(orders);
    }

    private String generateOrderNo() {
        return "ORD" + DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss") + 
               UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void saveOrderItems(Orders orders, List<OrderDTO.OrderItemDTO> items) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderDTO.OrderItemDTO item : items) {
            Dish dish = dishService.getById(item.getDishId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orders.getId());
            orderItem.setDishId(item.getDishId());
            orderItem.setDishName(dish.getName());
            orderItem.setDishImage(dish.getImage());
            orderItem.setPrice(dish.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(dish.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            orderItems.add(orderItem);

            // 更新菜品销量
            dish.setSalesCount(dish.getSalesCount() + item.getQuantity());
            dishService.updateById(dish);
        }

        orderItemService.saveBatch(orderItems);
    }
}
