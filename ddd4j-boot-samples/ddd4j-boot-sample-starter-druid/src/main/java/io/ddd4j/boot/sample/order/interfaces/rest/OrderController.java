package io.ddd4j.boot.sample.order.interfaces.rest;

import io.ddd4j.boot.core.R;
import io.ddd4j.boot.sample.order.application.command.CancelOrderCommand;
import io.ddd4j.boot.sample.order.application.command.CreateOrderCommand;
import io.ddd4j.boot.sample.order.application.command.PayOrderCommand;
import io.ddd4j.boot.sample.order.application.command.ShipOrderCommand;
import io.ddd4j.boot.sample.order.application.dto.OrderDTO;
import io.ddd4j.boot.sample.order.application.query.OrderQuery;
import io.ddd4j.boot.sample.order.application.response.OrderPageResponse;
import io.ddd4j.boot.sample.order.application.service.OrderApplicationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 订单REST接口
 */
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    /**
     * 创建订单
     */
    @ApiOperation(value = "创建订单", notes = "创建一个新的订单")
    @PostMapping
    public R<OrderDTO> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDTO order = orderApplicationService.createOrder(command);
        return R.success(order);
    }

    /**
     * 支付订单
     */
    @ApiOperation(value = "支付订单", notes = "对指定订单进行支付操作")
    @PostMapping("/{orderId}/pay")
    public R<OrderDTO> payOrder(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody PayOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.payOrder(command);
        return R.success(order);
    }

    /**
     * 发货
     */
    @ApiOperation(value = "订单发货", notes = "对已支付的订单进行发货操作")
    @PostMapping("/{orderId}/ship")
    public R<OrderDTO> shipOrder(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody ShipOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.shipOrder(command);
        return R.success(order);
    }

    /**
     * 确认收货
     */
    @ApiOperation(value = "确认收货", notes = "确认订单已送达")
    @PostMapping("/{orderId}/confirm-delivery")
    public R<OrderDTO> confirmDelivery(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @ApiParam(value = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.confirmDelivery(orderId, orderNo);
        return R.success(order);
    }

    /**
     * 完成订单
     */
    @ApiOperation(value = "完成订单", notes = "完成订单流程")
    @PostMapping("/{orderId}/complete")
    public R<OrderDTO> completeOrder(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @ApiParam(value = "订单号", example = "ORD1234567890")
            @RequestParam(required = false) String orderNo) {
        OrderDTO order = orderApplicationService.completeOrder(orderId, orderNo);
        return R.success(order);
    }

    /**
     * 取消订单
     */
    @ApiOperation(value = "取消订单", notes = "取消指定订单")
    @PostMapping("/{orderId}/cancel")
    public R<OrderDTO> cancelOrder(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderCommand command) {
        command.setOrderId(orderId);
        OrderDTO order = orderApplicationService.cancelOrder(command);
        return R.success(order);
    }

    /**
     * 根据ID查询订单
     */
    @ApiOperation(value = "查询订单", notes = "根据订单ID查询订单详情")
    @GetMapping("/{id}")
    public R<OrderDTO> getOrderById(
            @ApiParam(value = "订单ID", example = "1", required = true)
            @PathVariable Long id) {
        OrderDTO order = orderApplicationService.getOrderById(id);
        return R.success(order);
    }

    /**
     * 根据订单号查询订单
     */
    @ApiOperation(value = "根据订单号查询", notes = "根据订单号查询订单详情")
    @GetMapping("/order-no/{orderNo}")
    public R<OrderDTO> getOrderByOrderNo(
            @ApiParam(value = "订单号", example = "ORD1234567890", required = true)
            @PathVariable String orderNo) {
        OrderDTO order = orderApplicationService.getOrderByOrderNo(orderNo);
        return R.success(order);
    }

    /**
     * 根据用户ID查询订单列表
     */
    @ApiOperation(value = "查询用户订单列表", notes = "根据用户ID查询该用户的所有订单")
    @GetMapping("/user/{userId}")
    public R<List<OrderDTO>> getOrdersByUserId(
            @ApiParam(value = "用户ID", example = "1001", required = true)
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderApplicationService.getOrdersByUserId(userId);
        return R.success(orders);
    }

    /**
     * 分页查询订单
     */
    @ApiOperation(value = "分页查询订单", notes = "根据查询条件分页查询订单列表，支持多条件组合查询")
    @PostMapping("/query")
    public R<OrderPageResponse> queryOrders(@Valid @RequestBody OrderQuery query) {
        OrderPageResponse result = orderApplicationService.queryOrders(query);
        return R.success(result);
    }
}
