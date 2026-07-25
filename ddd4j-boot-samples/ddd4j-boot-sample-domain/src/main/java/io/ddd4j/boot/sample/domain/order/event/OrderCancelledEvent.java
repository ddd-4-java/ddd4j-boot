package io.ddd4j.boot.sample.domain.order.event;

/**
 * 订单取消事件
 */
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

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }
}