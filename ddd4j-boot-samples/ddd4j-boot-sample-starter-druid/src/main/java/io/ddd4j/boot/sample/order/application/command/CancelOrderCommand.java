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

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;

    @Schema(description = "取消原因", example = "不想要了")
    private String reason;


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
