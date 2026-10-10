package io.ddd4j.boot.sample.domain.order.event;

/**
 * 订单创建领域事件。
 *
 * <p>表示一笔订单由聚合根创建成功，携带订单标识与下单时的总金额，
 * 供库存锁定、欢迎通知等下游订阅方消费。</p>
 */
public class OrderCreatedEvent extends DomainEvent {

    /**
     * 订单主键 ID（聚合根尚未落库时可能为 null）。
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
     * 下单时的订单总金额（字符串形式，含货币标识）。
     */
    private final String totalAmount;

    /**
     * 构造订单创建事件。
     *
     * @param orderId     订单主键 ID，聚合根尚未持久化时可为 null
     * @param orderNo     订单编号
     * @param userId      下单用户 ID
     * @param totalAmount 订单总金额
     */
    public OrderCreatedEvent(Long orderId, String orderNo, Long userId, String totalAmount) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    /**
     * 获取订单主键 ID。
     *
     * @return 订单主键 ID，可能为 null
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
     * 获取订单总金额。
     *
     * @return 订单总金额字符串
     */
    public String getTotalAmount() {
        return totalAmount;
    }
}
