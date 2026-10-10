package io.ddd4j.boot.sample.domain.order.event;

/**
 * 订单支付领域事件。
 *
 * <p>表示一笔订单已完成支付、状态由待支付流转为已支付，
 * 携带支付方式，供发货调度、财务对账等下游订阅方消费。</p>
 */
public class OrderPaidEvent extends DomainEvent {

    /**
     * 订单主键 ID。
     */
    private final Long orderId;

    /**
     * 订单编号（业务单号）。
     */
    private final String orderNo;

    /**
     * 下单用户 ID。
     */
    private final Long userId;

    /**
     * 支付方式（如 ALIPAY、WECHAT 等业务编码）。
     */
    private final String paymentMethod;

    /**
     * 构造订单支付事件。
     *
     * @param orderId       订单主键 ID
     * @param orderNo       订单编号
     * @param userId        下单用户 ID
     * @param paymentMethod 支付方式
     */
    public OrderPaidEvent(Long orderId, String orderNo, Long userId, String paymentMethod) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
    }

    /**
     * 获取订单主键 ID。
     *
     * @return 订单主键 ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 获取订单编号。
     *
     * @return 订单编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 获取下单用户 ID。
     *
     * @return 用户 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 获取支付方式。
     *
     * @return 支付方式编码
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }
}
