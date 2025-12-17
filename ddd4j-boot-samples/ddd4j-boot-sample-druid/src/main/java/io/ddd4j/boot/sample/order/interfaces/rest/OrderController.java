package io.ddd4j.boot.sample.order.interfaces.rest;

import io.ddd4j.boot.core.ApiRestResponse;
import io.ddd4j.boot.sample.order.application.command.*;
import io.ddd4j.boot.sample.order.application.dto.OrderDTO;
import io.ddd4j.boot.sample.order.application.query.OrderQuery;
import io.ddd4j.boot.sample.order.application.response.OrderPageResponse;
import io.ddd4j.boot.sample.order.application.service.OrderApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 订单REST接口
 */
@Tag(name = "订单管理", description = "订单相关的API接口")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderApplicationService orderApplicationService;
    
    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "创建一个新的订单")
    @PostMapping
    public ApiRestResponse<OrderDTO> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDTO order = orderApplicationService.createOrder(command);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 支付订单
     */
    @Operation(summary = "支付订单", description = "对指定订单进行支付操作")
    @PostMapping("/{orderId}/pay")
    public ApiRestResponse<OrderDTO> payOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody PayOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.payOrder(command);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 发货
     */
    @Operation(summary = "订单发货", description = "对已支付的订单进行发货操作")
    @PostMapping("/{orderId}/ship")
    public ApiRestResponse<OrderDTO> shipOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody ShipOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.shipOrder(command);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 确认收货
     */
    @Operation(summary = "确认收货", description = "确认订单已送达")
    @PostMapping("/{orderId}/confirm-delivery")
    public ApiRestResponse<OrderDTO> confirmDelivery(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.confirmDelivery(orderId, orderNo);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "完成订单流程")
    @PostMapping("/{orderId}/complete")
    public ApiRestResponse<OrderDTO> completeOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.completeOrder(orderId, orderNo);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/{orderId}/cancel")
    public ApiRestResponse<OrderDTO> cancelOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.cancelOrder(command);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 根据ID查询订单
     */
    @Operation(summary = "查询订单", description = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public ApiRestResponse<OrderDTO> getOrderById(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long id) {
        OrderDTO order = orderApplicationService.getOrderById(id);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 根据订单号查询订单
     */
    @Operation(summary = "根据订单号查询", description = "根据订单号查询订单详情")
    @GetMapping("/order-no/{orderNo}")
    public ApiRestResponse<OrderDTO> getOrderByOrderNo(
            @Parameter(description = "订单号", example = "ORD1234567890", required = true)
            @PathVariable String orderNo) {
        OrderDTO order = orderApplicationService.getOrderByOrderNo(orderNo);
        return ApiRestResponse.success(order);
    }
    
    /**
     * 根据用户ID查询订单列表
     */
    @Operation(summary = "查询用户订单列表", description = "根据用户ID查询该用户的所有订单")
    @GetMapping("/user/{userId}")
    public ApiRestResponse<List<OrderDTO>> getOrdersByUserId(
            @Parameter(description = "用户ID", example = "1001", required = true)
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderApplicationService.getOrdersByUserId(userId);
        return ApiRestResponse.success(orders);
    }
    
    /**
     * 分页查询订单
     */
    @Operation(summary = "分页查询订单", description = "根据查询条件分页查询订单列表，支持多条件组合查询")
    @PostMapping("/query")
    public ApiRestResponse<OrderPageResponse> queryOrders(@Valid @RequestBody OrderQuery query) {
        OrderPageResponse result = orderApplicationService.queryOrders(query);
        return ApiRestResponse.success(result);
    }
}
