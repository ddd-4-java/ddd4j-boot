package io.ddd4j.boot.sample.adapter.order.web;

import io.ddd4j.boot.sample.app.order.command.CancelOrderCommand;
import io.ddd4j.boot.sample.app.order.command.CreateOrderCommand;
import io.ddd4j.boot.sample.app.order.command.PayOrderCommand;
import io.ddd4j.boot.sample.app.order.command.ShipOrderCommand;
import io.ddd4j.boot.sample.app.order.dto.OrderDTO;
import io.ddd4j.boot.sample.app.order.query.OrderQuery;
import io.ddd4j.boot.sample.app.order.response.OrderPageResponse;
import io.ddd4j.boot.sample.app.order.service.OrderApplicationService;
import io.ddd4j.core.api.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单REST接口
 */
@Tag(name = "订单管理", description = "订单相关的API接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    /**
     * 创建订单
     */
    @Operation(summary = "创建订单", description = "创建一个新的订单")
    @PostMapping
    public R<OrderDTO> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDTO order = orderApplicationService.createOrder(command);
        return R.success(order);
    }

    /**
     * 支付订单
     */
    @Operation(summary = "支付订单", description = "对指定订单进行支付操作")
    @PostMapping("/{orderId}/pay")
    public R<OrderDTO> payOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody PayOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.payOrder(command);
        return R.success(order);
    }

    /**
     * 发货
     */
    @Operation(summary = "订单发货", description = "对已支付的订单进行发货操作")
    @PostMapping("/{orderId}/ship")
    public R<OrderDTO> shipOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody ShipOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.shipOrder(command);
        return R.success(order);
    }

    /**
     * 确认收货
     */
    @Operation(summary = "确认收货", description = "确认订单已送达")
    @PostMapping("/{orderId}/confirm-delivery")
    public R<OrderDTO> confirmDelivery(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.confirmDelivery(orderId, orderNo);
        return R.success(order);
    }

    /**
     * 完成订单
     */
    @Operation(summary = "完成订单", description = "完成订单流程")
    @PostMapping("/{orderId}/complete")
    public R<OrderDTO> completeOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Parameter(description = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.completeOrder(orderId, orderNo);
        return R.success(order);
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消指定订单")
    @PostMapping("/{orderId}/cancel")
    public R<OrderDTO> cancelOrder(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.cancelOrder(command);
        return R.success(order);
    }

    /**
     * 根据ID查询订单
     */
    @Operation(summary = "查询订单", description = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public R<OrderDTO> getOrderById(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long id) {
        OrderDTO order = orderApplicationService.getOrderById(id);
        return R.success(order);
    }

    /**
     * 根据订单号查询订单
     */
    @Operation(summary = "根据订单号查询", description = "根据订单号查询订单详情")
    @GetMapping("/order-no/{orderNo}")
    public R<OrderDTO> getOrderByOrderNo(
            @Parameter(description = "订单号", example = "ORD1234567890", required = true)
            @PathVariable String orderNo) {
        OrderDTO order = orderApplicationService.getOrderByOrderNo(orderNo);
        return R.success(order);
    }

    /**
     * 根据用户ID查询订单列表
     */
    @Operation(summary = "查询用户订单列表", description = "根据用户ID查询该用户的所有订单")
    @GetMapping("/user/{userId}")
    public R<List<OrderDTO>> getOrdersByUserId(
            @Parameter(description = "用户ID", example = "1001", required = true)
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderApplicationService.getOrdersByUserId(userId);
        return R.success(orders);
    }

    /**
     * 分页查询订单
     */
    @Operation(summary = "分页查询订单", description = "根据查询条件分页查询订单列表，支持多条件组合查询")
    @PostMapping("/query")
    public R<OrderPageResponse> queryOrders(@Valid @RequestBody OrderQuery query) {
        OrderPageResponse result = orderApplicationService.queryOrders(query);
        return R.success(result);
    }
}
