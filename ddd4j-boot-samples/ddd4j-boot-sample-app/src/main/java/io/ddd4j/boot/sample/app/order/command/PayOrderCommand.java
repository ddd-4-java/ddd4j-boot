package io.ddd4j.boot.sample.app.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
@Data
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
}
