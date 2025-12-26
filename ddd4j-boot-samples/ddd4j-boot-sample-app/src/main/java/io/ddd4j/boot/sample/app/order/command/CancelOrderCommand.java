package io.ddd4j.boot.sample.app.order.command;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(description = "取消订单请求")
@Data
public class CancelOrderCommand implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "订单ID", example = "1")
    private Long orderId;
    
    @ApiModelProperty(value = "订单号", example = "ORD1234567890")
    private String orderNo;
    
    @ApiModelProperty(value = "取消原因", example = "不想要了")
    private String reason;
}
