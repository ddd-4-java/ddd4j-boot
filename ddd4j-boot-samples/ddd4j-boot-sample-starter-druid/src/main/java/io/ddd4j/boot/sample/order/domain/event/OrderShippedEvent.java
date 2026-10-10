package io.ddd4j.boot.sample.order.domain.event;


/**
 * 订单发货事件
 */

public class OrderShippedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderNo;
    private final Long userId;
    private final String trackingNumber;
    private final String logisticsCompany;

    /**
     * 构造 OrderShippedEvent 实例。
     *
     * @param orderId 订单 ID
     * @param orderNo 订单号
     * @param userId 用户 ID
     * @param trackingNumber 物流单号
     * @param logisticsCompany 物流公司
     *
     */
    public OrderShippedEvent(Long orderId, String orderNo, Long userId, String trackingNumber, String logisticsCompany) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.trackingNumber = trackingNumber;
        this.logisticsCompany = logisticsCompany;
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
     * 获取TrackingNumber。
     *
     * @return TrackingNumber
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * 获取LogisticsCompany。
     *
     * @return LogisticsCompany
     */
    public String getLogisticsCompany() {
        return logisticsCompany;
    }
}

