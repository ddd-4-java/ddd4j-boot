package io.ddd4j.boot.sample.adapter.order.web;

import io.ddd4j.boot.sample.app.order.command.CancelOrderCommand;
import io.ddd4j.boot.sample.app.order.command.CreateOrderCommand;
import io.ddd4j.boot.sample.app.order.command.PayOrderCommand;
import io.ddd4j.boot.sample.app.order.command.ShipOrderCommand;
import io.ddd4j.boot.sample.app.order.dto.OrderDTO;
import io.ddd4j.boot.sample.app.order.query.OrderQuery;
import io.ddd4j.boot.sample.app.order.response.OrderPageResponse;
import io.ddd4j.boot.sample.app.order.service.OrderApplicationService;
import io.ddd4j.core.ApiRestResponse;
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

    /**
     * 构造 OrderController 实例。
     *
     * @param orderApplicationService orderApplicationService
     */
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    /**
     * 创建订单
     *
     * @param command 命令对象
     * @return 新增结果
     */
    public ApiRestResponse<OrderDTO> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        OrderDTO order = orderApplicationService.createOrder(command);
        return ApiRestResponse.success(order);
    }

    /**
     * 支付订单
     *
     * @param orderId 订单 ID
     * @param command 命令对象
     * @return 处理结果
     */
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
     *
     * @param orderId 订单 ID
     * @param command 命令对象
     * @return 处理结果
     */
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
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @return 处理结果
     */
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
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @return 处理结果
     */
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
     *
     * @param orderId 订单 ID
     * @param command 命令对象
     * @return 删除结果
     */
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
     *
     * @param id 标识 ID
     * @return 查询结果
     */
    public ApiRestResponse<OrderDTO> getOrderById(
            @Parameter(description = "订单ID", example = "1", required = true)
            @PathVariable Long id) {
        OrderDTO order = orderApplicationService.getOrderById(id);
        return ApiRestResponse.success(order);
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 查询结果
     */
    public ApiRestResponse<OrderDTO> getOrderByOrderNo(
            @Parameter(description = "订单号", example = "ORD1234567890", required = true)
            @PathVariable String orderNo) {
        OrderDTO order = orderApplicationService.getOrderByOrderNo(orderNo);
        return ApiRestResponse.success(order);
    }

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户 ID
     * @return 查询结果
     */
    public ApiRestResponse<List<OrderDTO>> getOrdersByUserId(
            @Parameter(description = "用户ID", example = "1001", required = true)
            @PathVariable Long userId) {
        List<OrderDTO> orders = orderApplicationService.getOrdersByUserId(userId);
        return ApiRestResponse.success(orders);
    }

    /**
     * 分页查询订单
     *
     * @param query 查询条件
     * @return 查询结果
     */
    public ApiRestResponse<OrderPageResponse> queryOrders(@Valid @RequestBody OrderQuery query) {
        OrderPageResponse result = orderApplicationService.queryOrders(query);
        return ApiRestResponse.success(result);
    }
}
