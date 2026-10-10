package io.ddd4j.boot.sample.order.domain.event;


/**
 * 订单取消事件
 */

public class OrderCancelledEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String reason;

    /**
     * 构造 OrderCancelledEvent 实例。
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @param userId 用户 ID
     * @param reason 原因
     *
     */
    public OrderCancelledEvent(Long orderId, String orderNo, Long userId, String reason) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.reason = reason;
    }

    /**
     * 获取OrderId。
     *
     * @return OrderId
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 获取OrderNo。
     *
     * @return OrderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 获取UserId。
     *
     * @return UserId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 获取Reason。
     *
     * @return Reason
     */
    public String getReason() {
        return reason;
    }
}

