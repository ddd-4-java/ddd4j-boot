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
    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;
    /** 订单ID */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    /** 订单号 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;
    /** 取消原因 */
    @Schema(description = "取消原因", example = "不想要了")
    private String reason;

    /** 构造 CancelOrderCommand 对象。 */
    public CancelOrderCommand() {
    }

    /** 获取订单ID。
     * @return 订单ID */
    public Long getOrderId() {
        return this.orderId;
    }

    /** 获取订单号。
     * @return 订单号 */
    public String getOrderNo() {
        return this.orderNo;
    }

    /** 获取取消原因。
     * @return 取消原因 */
    public String getReason() {
        return this.reason;
    }

    /** 设置订单ID。
     * @param orderId 订单ID */
    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    /** 设置订单号。
     * @param orderNo 订单号 */
    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    /** 设置取消原因。
     * @param reason 取消原因 */
    public void setReason(final String reason) {
        this.reason = reason;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
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

    /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
     * @param other 待判断对象
     * @return 可比较返回 {@code true}，否则返回 {@code false} */
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CancelOrderCommand;
    }

    /** 返回基于各字段计算的哈希码。
     * @return 哈希码 */
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

    /** 返回对象各字段拼接而成的字符串表示。
     * @return 字符串表示 */
    @java.lang.Override
    public java.lang.String toString() {
        return "CancelOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", reason=" + this.getReason() + ")";
    }
}
