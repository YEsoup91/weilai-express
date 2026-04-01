package com.weilai.express.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.weilai.express.dto.OrderDTO;
import com.weilai.express.entity.Merchant;
import com.weilai.express.entity.Orders;
import com.weilai.express.entity.User;
import com.weilai.express.service.MerchantService;
import com.weilai.express.service.OrderService;
import com.weilai.express.service.UserService;
import com.weilai.express.vo.OrderDetailVO;
import com.weilai.express.vo.OrderListVO;
import com.weilai.express.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final MerchantService merchantService;
    private final UserService userService;

    public OrderController(OrderService orderService, MerchantService merchantService, UserService userService) {
        this.orderService = orderService;
        this.merchantService = merchantService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<OrderDetailVO> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        Orders order = orderService.createOrder(orderDTO);
        
        // 获取完整的订单详情（包含订单项）
        OrderDetailVO vo = orderService.getOrderDetail(order.getId());
        
        return Result.success(vo);
    }

    @GetMapping
    @Operation(summary = "获取订单列表")
    public Result<List<OrderListVO>> list(@RequestParam Long userId,
                                          @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
            .eq(Orders::getUserId, userId);
        
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        
        wrapper.orderByDesc(Orders::getCreateTime);
        
        List<Orders> orders = orderService.list(wrapper);
        
        List<OrderListVO> result = orders.stream().map(order -> {
            OrderListVO vo = new OrderListVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());
        
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        OrderDetailVO vo = orderService.getOrderDetail(id);
        return Result.success(vo);
    }

    @GetMapping("/merchant/by-user/{userId}")
    @Operation(summary = "根据用户 ID 获取商家订单列表")
    public Result<List<Map<String, Object>>> listByMerchantUserId(@PathVariable Long userId,
                                                     @RequestParam(required = false) Integer status) {
        System.out.println("========================================");
        System.out.println("=== 根据用户 ID 查询商家订单，userId: " + userId);
        
        // 先根据 userId 查询商家信息（可能有多个店铺）
        LambdaQueryWrapper<Merchant> merchantWrapper = new LambdaQueryWrapper<Merchant>()
            .eq(Merchant::getUserId, userId)
            .eq(Merchant::getStatus, 1); // 只查询营业中的店铺
        
        List<Merchant> merchants = merchantService.list(merchantWrapper);
        
        if (merchants == null || merchants.isEmpty()) {
            System.out.println("=== 该用户没有关联的商家");
            return Result.error(404, "该用户没有关联的商家");
        }
        
        System.out.println("=== 找到 " + merchants.size() + " 个商家");
        
        // 获取第一个商家的 ID（如果有多个店铺，取第一个）
        Merchant merchant = merchants.get(0);
        Long merchantId = merchant.getId();
        
        System.out.println("=== 使用商家 ID: " + merchantId + ", 商家名称：" + merchant.getName());
        
        // 查询该商家的所有订单
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
            .eq(Orders::getMerchantId, merchantId);
        
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        
        wrapper.orderByDesc(Orders::getCreateTime);
        
        List<Orders> orders = orderService.list(wrapper);
        
        System.out.println("=== 查询到订单数量：" + orders.size());
        
        // 构建返回数据
        List<Map<String, Object>> result = orders.stream().map(order -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("payAmount", order.getPayAmount());
            map.put("deliveryAddress", order.getDeliveryAddress());
            map.put("receiverName", order.getReceiverName());
            map.put("receiverPhone", order.getReceiverPhone());
            map.put("status", order.getStatus());
            map.put("riderId", order.getRiderId());
            map.put("riderName", order.getRiderId() != null ? "已分配骑手" : "未分配");
            map.put("createTime", order.getCreateTime());
            
            return map;
        }).collect(Collectors.toList());
        
        System.out.println("========================================");
        
        return Result.success(result);
    }

    @GetMapping("/rider/available")
    @Operation(summary = "获取骑手可抢订单列表")
    public Result<List<Map<String, Object>>> listAvailableForRider(@RequestParam(required = false) Integer status) {
        try {
            System.out.println("========================================");
            System.out.println("=== 骑手可抢订单接口被调用");
            System.out.println("=== 请求参数 status: " + status);
            
            // 状态为 2（待配送）的订单可以被骑手抢
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getStatus, 2)  // 必须是待配送状态
                .isNull(Orders::getRiderId); // 骑手 ID 为空的订单
            
            if (status != null) {
                wrapper.eq(Orders::getStatus, status);
            }
            
            wrapper.orderByDesc(Orders::getCreateTime);
            
            List<Orders> orders = orderService.list(wrapper);
            
            System.out.println("=== 查询到的可抢订单数量：" + orders.size());
            if (!orders.isEmpty()) {
                for (Orders order : orders) {
                    System.out.println("   - 订单 ID: " + order.getId() + 
                                     ", 订单号：" + order.getOrderNo() + 
                                     ", 金额：" + order.getPayAmount());
                }
            }
            System.out.println("========================================");
            
            List<Map<String, Object>> result = orders.stream().map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("deliveryFee", order.getDeliveryFee());
                map.put("payAmount", order.getPayAmount());
                map.put("merchantAddress", "北京市朝阳区建国路 88 号美味餐厅");
                map.put("deliveryAddress", order.getDeliveryAddress());
                map.put("receiverPhone", order.getReceiverPhone());
                map.put("createTime", order.getCreateTime());
                return map;
            }).collect(Collectors.toList());
            
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("=== ❌ 骑手可抢订单接口异常:");
            e.printStackTrace();
            return Result.error(500, "系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/rider/{riderId}/delivering")
    @Operation(summary = "获取骑手配送中订单")
    public Result<List<Map<String, Object>>> listDelivering(@PathVariable Long riderId) {
        try {
            System.out.println("========================================");
            System.out.println("=== 骑手配送中订单接口被调用，riderId: " + riderId);
            
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getRiderId, riderId)
                .eq(Orders::getStatus, 3); // 配送中
            
            wrapper.orderByDesc(Orders::getCreateTime);
            
            List<Orders> orders = orderService.list(wrapper);
            
            System.out.println("=== 查询到配送中订单数量：" + orders.size());
            
            List<Map<String, Object>> result = orders.stream().map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("deliveryFee", order.getDeliveryFee());
                map.put("merchantAddress", "商家地址待补充");
                map.put("deliveryAddress", order.getDeliveryAddress());
                map.put("receiverPhone", order.getReceiverPhone());
                map.put("createTime", order.getCreateTime());
                return map;
            }).collect(Collectors.toList());
            
            System.out.println("========================================");
            
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("=== ❌ 骑手配送中订单接口异常:");
            e.printStackTrace();
            return Result.error(500, "系统异常：" + e.getMessage());
        }
    }

    @GetMapping("/rider/{riderId}/completed")
    @Operation(summary = "获取骑手已完成订单")
    public Result<List<Map<String, Object>>> listCompleted(@PathVariable Long riderId) {
        try {
            System.out.println("========================================");
            System.out.println("=== 骑手已完成订单接口被调用，riderId: " + riderId);
            
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getRiderId, riderId)
                .eq(Orders::getStatus, 4); // 已完成
            
            wrapper.orderByDesc(Orders::getFinishTime);
            
            List<Orders> orders = orderService.list(wrapper);
            
            System.out.println("=== 查询到已完成订单数量：" + orders.size());
            
            List<Map<String, Object>> result = orders.stream().map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", order.getId());
                map.put("orderNo", order.getOrderNo());
                map.put("deliveryFee", order.getDeliveryFee());
                map.put("payAmount", order.getPayAmount());
                map.put("deliveryAddress", order.getDeliveryAddress());
                map.put("finishTime", order.getFinishTime());
                map.put("updateTime", order.getUpdateTime());
                return map;
            }).collect(Collectors.toList());
            
            System.out.println("========================================");
            
            return Result.success(result);
        } catch (Exception e) {
            System.err.println("=== ❌ 骑手已完成订单接口异常:");
            e.printStackTrace();
            return Result.error(500, "系统异常：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单")
    public Result<Void> payOrder(@PathVariable Long id,
                                 @RequestBody Map<String, Object> data) {
        Integer paymentMethod = (Integer) data.get("paymentMethod");
        orderService.payOrder(id, paymentMethod);
        return Result.success();
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "商家确认接单/制作完成")
    public Result<Void> confirmOrder(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/deliver")
    @Operation(summary = "商家发货/分配骑手")
    public Result<Void> deliverOrder(@PathVariable Long id,
                                     @RequestBody Map<String, Object> data) {
        Long riderId = ((Number) data.get("riderId")).longValue();
        orderService.assignRider(id, riderId);
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "骑手确认送达/完成订单")
    public Result<Void> completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<Void> cancelOrder(@PathVariable Long id,
                                    @RequestBody(required = false) Map<String, String> data) {
        String reason = data != null ? data.get("reason") : "用户取消";
        orderService.cancelOrder(id, reason);
        return Result.success();
    }

    @PostMapping("/{id}/grab")
    @Operation(summary = "骑手抢单")
    public Result<Void> grabOrder(@PathVariable Long id,
                                  @RequestBody Map<String, Object> data) {
        Long riderId = ((Number) data.get("riderId")).longValue();
        orderService.assignRider(id, riderId);
        return Result.success();
    }
}
