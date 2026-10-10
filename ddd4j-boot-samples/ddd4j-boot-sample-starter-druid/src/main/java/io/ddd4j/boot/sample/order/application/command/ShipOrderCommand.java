package io.ddd4j.boot.sample.order.application.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


import java.io.Serializable;

/**
 * 发货订单命令（Command）
 *
 * <p>用于执行订单发货操作的命令对象。
 * 包含发货所需的订单标识和物流信息。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "发货订单请求")

public class ShipOrderCommand implements Serializable {
    /**
     * 构造 ShipOrderCommand 实例。
     *
     */
    public ShipOrderCommand() {
    }

    private static final long serialVersionUID = 1L;

    /** 订单ID。 */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /** 订单号。 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;

    /** 物流公司。 */
    @Schema(description = "物流公司", example = "顺丰快递", required = true)
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;

    /** 物流单号。 */
    @Schema(description = "物流单号", example = "SF1234567890", required = true)
    @NotBlank(message = "物流单号不能为空")
    private String trackingNumber;


    /**
     * 获取订单ID。
     *
     * @return 订单ID
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置订单ID。
     *
     * @param orderId 订单ID
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单号。
     *
     * @return 订单号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单号。
     *
     * @param orderNo 订单号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取物流公司。
     *
     * @return 物流公司
     */
    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    /**
     * 设置物流公司。
     *
     * @param logisticsCompany 物流公司
     */
    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    /**
     * 获取物流单号。
     *
     * @return 物流单号
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * 设置物流单号。
     *
     * @param trackingNumber 物流单号
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
