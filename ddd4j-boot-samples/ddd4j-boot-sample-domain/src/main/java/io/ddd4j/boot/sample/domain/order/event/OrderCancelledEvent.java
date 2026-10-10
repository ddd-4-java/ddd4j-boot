package io.ddd4j.boot.sample.domain.order.event;

/**
 * 订单取消领域事件。
 *
 * <p>表示一笔订单在待支付或已支付未发货阶段被取消，
 * 携带订单标识与取消原因，供通知、统计等下游订阅方消费。</p>
 */
public class OrderCancelledEvent extends DomainEvent {

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
     * 取消原因。
     */
    private final String reason;

    /**
     * 构造订单取消事件。
     *
     * @param orderId 订单主键 ID
     * @param orderNo 订单编号
     * @param userId  下单用户 ID
     * @param reason  取消原因
     */
    public OrderCancelledEvent(Long orderId, String orderNo, Long userId, String reason) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.reason = reason;
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
     * 获取取消原因。
     *
     * @return 取消原因
     */
    public String getReason() {
        return reason;
    }
}
