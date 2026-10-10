package io.ddd4j.boot.sample.order.application.command;

import io.swagger.v3.oas.annotations.media.Schema;


import java.io.Serializable;

/**
 * 取消订单命令（Command）
 *
 * <p>用于执行订单取消操作的命令对象。
 * 包含取消订单所需的订单标识和取消原因。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "取消订单请求")

public class CancelOrderCommand implements Serializable {
    /**
     * 构造 CancelOrderCommand 实例。
     *
     */
    public CancelOrderCommand() {
    }

    private static final long serialVersionUID = 1L;

    /** 订单ID。 */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /** 订单号。 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;

    /** 取消原因。 */
    @Schema(description = "取消原因", example = "不想要了")
    private String reason;


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
     * 获取取消原因。
     *
     * @return 取消原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置取消原因。
     *
     * @param reason 取消原因
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}
