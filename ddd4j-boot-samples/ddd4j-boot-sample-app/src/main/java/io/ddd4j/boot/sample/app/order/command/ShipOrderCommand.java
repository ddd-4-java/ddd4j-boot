package io.ddd4j.boot.sample.app.order.command;

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
    private static final long serialVersionUID = 1L;
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;
    @Schema(description = "物流公司", example = "顺丰快递", required = true)
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;
    @Schema(description = "物流单号", example = "SF1234567890", required = true)
    @NotBlank(message = "物流单号不能为空")
    private String trackingNumber;

    public ShipOrderCommand() {
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public String getLogisticsCompany() {
        return this.logisticsCompany;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    public void setLogisticsCompany(final String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    public void setTrackingNumber(final String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof ShipOrderCommand)) return false;
        final ShipOrderCommand other = (ShipOrderCommand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$orderId = this.getOrderId();
        final java.lang.Object other$orderId = other.getOrderId();
        if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
        final java.lang.Object this$orderNo = this.getOrderNo();
        final java.lang.Object other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) return false;
        final java.lang.Object this$logisticsCompany = this.getLogisticsCompany();
        final java.lang.Object other$logisticsCompany = other.getLogisticsCompany();
        if (this$logisticsCompany == null ? other$logisticsCompany != null : !this$logisticsCompany.equals(other$logisticsCompany)) return false;
        final java.lang.Object this$trackingNumber = this.getTrackingNumber();
        final java.lang.Object other$trackingNumber = other.getTrackingNumber();
        if (this$trackingNumber == null ? other$trackingNumber != null : !this$trackingNumber.equals(other$trackingNumber)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof ShipOrderCommand;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $orderId = this.getOrderId();
        result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
        final java.lang.Object $orderNo = this.getOrderNo();
        result = result * PRIME + ($orderNo == null ? 43 : $orderNo.hashCode());
        final java.lang.Object $logisticsCompany = this.getLogisticsCompany();
        result = result * PRIME + ($logisticsCompany == null ? 43 : $logisticsCompany.hashCode());
        final java.lang.Object $trackingNumber = this.getTrackingNumber();
        result = result * PRIME + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ShipOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", logisticsCompany=" + this.getLogisticsCompany() + ", trackingNumber=" + this.getTrackingNumber() + ")";
    }
}
