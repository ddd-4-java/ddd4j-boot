package io.ddd4j.boot.sample.app.order.command;

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

    public CancelOrderCommand() {
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getReason() {
        return this.reason;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CancelOrderCommand)) return false;
        final CancelOrderCommand other = (CancelOrderCommand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$orderId = this.getOrderId();
        final java.lang.Object other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
        final java.lang.Object this$orderNo = this.getOrderNo();
        final java.lang.Object other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) return false;
        final java.lang.Object this$reason = this.getReason();
        final java.lang.Object other$reason = other.getReason();
        if (this$reason == null ? other$reason != null : !this$reason.equals(other$reason)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CancelOrderCommand;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $orderId = this.getOrderId();
        result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
        final java.lang.Object $orderNo = this.getOrderNo();
        result = result * PRIME + ($orderNo == null ? 43 : $orderNo.hashCode());
        final java.lang.Object $reason = this.getReason();
        result = result * PRIME + ($reason == null ? 43 : $reason.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "CancelOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", reason=" + this.getReason() + ")";
    }
}
