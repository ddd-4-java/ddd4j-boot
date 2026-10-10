package io.ddd4j.boot.sample.order.domain.event;


/**
 * 订单创建事件
 */

public class OrderCreatedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String totalAmount;

    /**
     * 构造 OrderCreatedEvent 实例。
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @param userId 用户 ID
     * @param totalAmount 订单总金额
     *
     */
    public OrderCreatedEvent(Long orderId, String orderNo, Long userId, String totalAmount) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
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
     * 获取TotalAmount。
     *
     * @return TotalAmount
     */
    public String getTotalAmount() {
        return totalAmount;
    }
}

