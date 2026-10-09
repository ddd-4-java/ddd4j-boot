package io.ddd4j.boot.sample.order.domain.event;

import lombok.Getter;

/**
 * 订单创建事件
 */
@Getter
public class OrderCreatedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String totalAmount;

    public OrderCreatedEvent(Long orderId, String orderNo, Long userId, String totalAmount) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }
}

