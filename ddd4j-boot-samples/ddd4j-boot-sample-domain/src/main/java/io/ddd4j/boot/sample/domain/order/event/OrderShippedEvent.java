package io.ddd4j.boot.sample.domain.order.event;

/**
 * 订单发货领域事件。
 *
 * <p>表示一笔已支付订单完成发货、状态流转为已发货，
 * 携带物流运单号与物流公司，供物流跟踪、用户通知等下游订阅方消费。</p>
 */
public class OrderShippedEvent extends DomainEvent {

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
     * 物流运单号。
     */
    private final String trackingNumber;

    /**
     * 物流公司名称或编码。
     */
    private final String logisticsCompany;

    /**
     * 构造订单发货事件。
     *
     * @param orderId         订单主键 ID
     * @param orderNo         订单编号
     * @param userId          下单用户 ID
     * @param trackingNumber  物流运单号
     * @param logisticsCompany 物流公司
     */
    public OrderShippedEvent(Long orderId, String orderNo, Long userId, String trackingNumber, String logisticsCompany) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.trackingNumber = trackingNumber;
        this.logisticsCompany = logisticsCompany;
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
     * 获取物流运单号。
     *
     * @return 运单号
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * 获取物流公司。
     *
     * @return 物流公司
     */
    public String getLogisticsCompany() {
        return logisticsCompany;
    }
}
