package io.ddd4j.boot.sample.infrastructure.order.messaging.handler;

import io.ddd4j.boot.sample.domain.order.event.OrderCancelledEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderPaidEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderShippedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 订单领域事件处理器
 * 处理订单相关的领域事件，可以触发后续的业务流程
 */
@Slf4j
@Component
public class OrderEventHandler {

    /**
     * 处理订单创建事件
     */
    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("处理订单创建事件 - 订单号: {}, 用户ID: {}", event.getOrderNo(), event.getUserId());
        // TODO: 可以在这里触发后续业务流程，如：
        // 1. 发送订单创建通知
        // 2. 扣减库存
        // 3. 记录订单日志
    }

    /**
     * 处理订单支付事件
     */
    @Async
    @EventListener
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("处理订单支付事件 - 订单号: {}, 支付方式: {}", event.getOrderNo(), event.getPaymentMethod());
        // TODO: 可以在这里触发后续业务流程，如：
        // 1. 发送支付成功通知
        // 2. 更新用户积分
        // 3. 触发发货流程
    }

    /**
     * 处理订单发货事件
     */
    @Async
    @EventListener
    public void handleOrderShipped(OrderShippedEvent event) {
        log.info("处理订单发货事件 - 订单号: {}, 物流单号: {}", event.getOrderNo(), event.getTrackingNumber());
        // TODO: 可以在这里触发后续业务流程，如：
        // 1. 发送发货通知
        // 2. 更新物流信息
        // 3. 记录发货日志
    }

    /**
     * 处理订单取消事件
     */
    @Async
    @EventListener
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("处理订单取消事件 - 订单号: {}, 取消原因: {}", event.getOrderNo(), event.getReason());
        // TODO: 可以在这里触发后续业务流程，如：
        // 1. 发送取消通知
        // 2. 退款处理
        // 3. 恢复库存
    }
}

