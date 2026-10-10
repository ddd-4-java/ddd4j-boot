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
    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;
    /** 订单ID */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;
    /** 订单号 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;
    /** 物流公司 */
    @Schema(description = "物流公司", example = "顺丰快递", required = true)
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;
    /** 物流单号 */
    @Schema(description = "物流单号", example = "SF1234567890", required = true)
    @NotBlank(message = "物流单号不能为空")
    private String trackingNumber;

    /** 构造 ShipOrderCommand 对象。 */
    public ShipOrderCommand() {
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

    /** 获取物流公司。
     * @return 物流公司 */
    public String getLogisticsCompany() {
        return this.logisticsCompany;
    }

    /** 获取物流单号。
     * @return 物流单号 */
    public String getTrackingNumber() {
        return this.trackingNumber;
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

    /** 设置物流公司。
     * @param logisticsCompany 物流公司 */
    public void setLogisticsCompany(final String logisticsCompany) {
        this.logisticsCompany = logisticsCompany;
    }

    /** 设置物流单号。
     * @param trackingNumber 物流单号 */
    public void setTrackingNumber(final String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
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
        if (this$logisticsCompany == null ? other$logisticsCompany != null : !this$logisticsCompany.equals(other$logisticsCompany))
            return false;
        final java.lang.Object this$trackingNumber = this.getTrackingNumber();
        final java.lang.Object other$trackingNumber = other.getTrackingNumber();
        if (this$trackingNumber == null ? other$trackingNumber != null : !this$trackingNumber.equals(other$trackingNumber))
            return false;
        return true;
    }

    /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
     * @param other 待判断对象
     * @return 可比较返回 {@code true}，否则返回 {@code false} */
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof ShipOrderCommand;
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
        final java.lang.Object $logisticsCompany = this.getLogisticsCompany();
        result = result * PRIME + ($logisticsCompany == null ? 43 : $logisticsCompany.hashCode());
        final java.lang.Object $trackingNumber = this.getTrackingNumber();
        result = result * PRIME + ($trackingNumber == null ? 43 : $trackingNumber.hashCode());
        return result;
    }

    /** 返回对象各字段拼接而成的字符串表示。
     * @return 字符串表示 */
    @java.lang.Override
    public java.lang.String toString() {
        return "ShipOrderCommand(orderId=" + this.getOrderId() + ", orderNo=" + this.getOrderNo() + ", logisticsCompany=" + this.getLogisticsCompany() + ", trackingNumber=" + this.getTrackingNumber() + ")";
    }
}
