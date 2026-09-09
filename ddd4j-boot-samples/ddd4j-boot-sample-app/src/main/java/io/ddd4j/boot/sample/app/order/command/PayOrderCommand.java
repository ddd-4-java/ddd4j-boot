package io.ddd4j.boot.sample.app.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
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
    private static final long serialVersionUID = 1L;
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;
    @Schema(description = "支付方式", example = "ALIPAY", required = true)
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
    @Schema(description = "支付流水号", example = "PAY202312011234567890")
    private String paymentNo;

    public PayOrderCommand() {
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getPaymentMethod() {
        return this.paymentMethod;
    }

    public String getPaymentNo() {
        return this.paymentNo;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentNo(final String paymentNo) {
        this.paymentNo = paymentNo;
    }

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
        if (this$paymentMethod == null ? other$paymentMethod != null : !this$paymentMethod.equals(other$paymentMethod)) return false;
        final java.lang.Object this$paymentNo = this.getPaymentNo();
        final java.lang.Object other$paymentNo = other.getPaymentNo();
        if (this$paymentNo == null ? other$paymentNo != null : !this$paymentNo.equals(other$paymentNo)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof PayOrderCommand;
    }

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

    @java.lang.Override
    public java.lang.String toString() {
        return "PayOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", paymentMethod=" + this.getPaymentMethod() + ", paymentNo=" + this.getPaymentNo() + ")";
    }
}
