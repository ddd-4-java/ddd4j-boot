package io.ddd4j.boot.sample.order.domain.event;

import lombok.Getter;

/**
 * 订单发货事件
 */
@Getter
public class OrderShippedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String trackingNumber;
    private final String logisticsCompany;

    public OrderShippedEvent(Long orderId, String orderNo, Long userId, String trackingNumber, String logisticsCompany) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.trackingNumber = trackingNumber;
        this.logisticsCompany = logisticsCompany;
    }
}

