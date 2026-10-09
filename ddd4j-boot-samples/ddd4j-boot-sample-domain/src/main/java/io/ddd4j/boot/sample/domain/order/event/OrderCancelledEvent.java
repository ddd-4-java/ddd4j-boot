package io.ddd4j.boot.sample.domain.order.event;

import lombok.Getter;

/**
 * 订单取消事件
 */
@Getter
public class OrderCancelledEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String reason;

    public OrderCancelledEvent(Long orderId, String orderNo, Long userId, String reason) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.reason = reason;
    }
}

