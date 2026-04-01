package com.weilai.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weilai.express.dto.OrderDTO;
import com.weilai.express.entity.Orders;
import com.weilai.express.vo.OrderDetailVO;
import com.weilai.express.vo.OrdersVO;

import java.util.List;

public interface OrderService extends IService<Orders> {
    Orders createOrder(OrderDTO orderDTO);
    OrderDetailVO getOrderDetail(Long orderId);
    List<Orders> listByUser(Long userId);
    void payOrder(Long orderId, Integer paymentMethod);
    void cancelOrder(Long orderId, String reason);
    void completeOrder(Long orderId);
    void confirmOrder(Long orderId);
    void assignRider(Long orderId, Long riderId);
}
