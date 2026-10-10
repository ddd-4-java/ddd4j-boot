package io.ddd4j.boot.sample.infrastructure.order.messaging.handler;

import io.ddd4j.boot.sample.domain.order.event.OrderCancelledEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderCreatedEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderPaidEvent;
import io.ddd4j.boot.sample.domain.order.event.OrderShippedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 订单领域事件处理器
 * 处理订单相关的领域事件，可以触发后续的业务流程
 *
 * <p>领域语义：事务提交后的副作用出口。每个方法以 {@code @EventListener}
 * 订阅一类订单事件，并以 {@code @Async} 在独立线程异步执行，
 * 保证通知、库存、积分等后续动作不阻塞主交易链路、不参与其事务。</p>
 */
@Component
public class OrderEventHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderEventHandler.class);

    /**
     * 构造订单领域事件处理器，本类无状态、可复用。
     */
    public OrderEventHandler() {
    }

    /**
     * 处理订单创建事件。
     *
     * @param event 订单创建事件，含订单号与用户 ID
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
     * 处理订单支付事件。
     *
     * @param event 订单支付事件，含订单号与支付方式
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
     * 处理订单发货事件。
     *
     * @param event 订单发货事件，含订单号与物流单号
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
     * 处理订单取消事件。
     *
     * @param event 订单取消事件，含订单号与取消原因
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
