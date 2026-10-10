package io.ddd4j.boot.sample.app.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 支付订单命令（Command）
 *
 * <p>用于执行订单支付操作的命令对象。
 * 包含支付所需的订单标识和支付信息。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "支付订单请求")
public class PayOrderCommand implements Serializable {
    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;
    /** 订单ID */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    /** 订单号 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;
    /** 支付方式 */
    @Schema(description = "支付方式", example = "ALIPAY", required = true)
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
    /** 支付流水号 */
    @Schema(description = "支付流水号", example = "PAY202312011234567890")
    private String paymentNo;

    /** 构造 PayOrderCommand 对象。 */
    public PayOrderCommand() {
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

    /** 获取支付方式。
     * @return 支付方式 */
    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    /** 获取支付流水号。
     * @return 支付流水号 */
    public String getPaymentNo() {
        return this.paymentNo;
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

    /** 设置支付方式。
     * @param paymentMethod 支付方式 */
    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /** 设置支付流水号。
     * @param paymentNo 支付流水号 */
    public void setPaymentNo(final String paymentNo) {
        this.paymentNo = paymentNo;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof PayOrderCommand)) return false;
        final PayOrderCommand other = (PayOrderCommand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$orderId = this.getOrderId();
        final java.lang.Object other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
        final java.lang.Object this$orderNo = this.getOrderNo();
        final java.lang.Object other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) return false;
        final java.lang.Object this$paymentMethod = this.getPaymentMethod();
        final java.lang.Object other$paymentMethod = other.getPaymentMethod();
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod))
            return false;
        final java.lang.Object this$paymentNo = this.getPaymentNo();
        final java.lang.Object other$paymentNo = other.getPaymentNo();
        if (this$paymentNo == null ? other$paymentNo != null : !this$paymentNo.equals(other$paymentNo)) return false;
        return true;
    }

    /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
     * @param other 待判断对象
     * @return 可比较返回 {@code true}，否则返回 {@code false} */
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PayOrderCommand;
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
        final java.lang.Object $paymentMethod = this.getPaymentMethod();
        result = result * PRIME + ($paymentMethod == null ? 43 : $paymentMethod.hashCode());
        final java.lang.Object $paymentNo = this.getPaymentNo();
        result = result * PRIME + ($paymentNo == null ? 43 : $paymentNo.hashCode());
        return result;
    }

    /** 返回对象各字段拼接而成的字符串表示。
     * @return 字符串表示 */
    @java.lang.Override
    public java.lang.String toString() {
        return "PayOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentNo=" + this.getPaymentNo() + ")";
    }
}
