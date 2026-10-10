package io.ddd4j.boot.sample.order.application.command;

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
    /**
     * 构造 PayOrderCommand 实例。
     *
     */
    public PayOrderCommand() {
    }

    private static final long serialVersionUID = 1L;

    /** 订单ID。 */
    @Schema(description = "订单ID", example = "1")
    private Long orderId;

    /** 订单号。 */
    @Schema(description = "订单号", example = "ORD1234567890")
    private String orderNo;

    /** 支付方式。 */
    @Schema(description = "支付方式", example = "ALIPAY", required = true)
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;

    /** 支付流水号。 */
    @Schema(description = "支付流水号", example = "PAY202312011234567890")
    private String paymentNo;


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
     * 获取支付方式。
     *
     * @return 支付方式
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * 设置支付方式。
     *
     * @param paymentMethod 支付方式
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * 获取支付流水号。
     *
     * @return 支付流水号
     */
    public String getPaymentNo() {
        return paymentNo;
    }

    /**
     * 设置支付流水号。
     *
     * @param paymentNo 支付流水号
     */
    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }
}
