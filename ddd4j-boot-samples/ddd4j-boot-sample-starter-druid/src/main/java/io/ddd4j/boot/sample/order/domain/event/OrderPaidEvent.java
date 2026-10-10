package io.ddd4j.boot.sample.order.domain.event;



/**
 * 订单支付事件
 */

public class OrderPaidEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String paymentMethod;

    /**
     * 构造 OrderPaidEvent 实例。
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @param userId 用户 ID
     * @param paymentMethod 支付方式
     *
     */
    public OrderPaidEvent(Long orderId, String orderNo, Long userId, String paymentMethod) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
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
     * 获取PaymentMethod。
     *
     * @return PaymentMethod
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }
}

