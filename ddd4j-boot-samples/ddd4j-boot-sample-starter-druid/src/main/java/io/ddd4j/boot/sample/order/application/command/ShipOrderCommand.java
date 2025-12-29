package io.ddd4j.boot.sample.order.application.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
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
@ApiModel(description = "发货订单请求")
@Data
public class ShipOrderCommand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "订单ID", example = "1")
    private Long orderId;
    
    @ApiModelProperty(value = "订单号", example = "ORD1234567890")
    private String orderNo;
    
    @ApiModelProperty(value = "物流公司", example = "顺丰快递", required = true)
    @NotBlank(message = "物流公司不能为空")
    private String logisticsCompany;
    
    @ApiModelProperty(value = "物流单号", example = "SF1234567890", required = true)
    @NotBlank(message = "物流单号不能为空")
    private String trackingNumber;
}
