package io.ddd4j.boot.sample.order.domain.event;

import lombok.Getter;

/**
 * 订单支付事件
 */
@Getter
public class OrderPaidEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String paymentMethod;

    public OrderPaidEvent(Long orderId, String orderNo, Long userId, String paymentMethod) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
    }
}

