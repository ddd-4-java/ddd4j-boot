package io.ddd4j.boot.sample.client.order.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求对象（客户端SDK使用）
 * 
 * <p>用于客户端调用订单服务创建订单的请求参数。</p>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
@ApiModel(description = "创建订单请求")
@Data
public class CreateOrderRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "用户ID", example = "1001", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @ApiModelProperty(value = "收货地址", required = true)
    @NotNull(message = "收货地址不能为空")
    @Valid
    private AddressRequest shippingAddress;
    
    @ApiModelProperty(value = "备注", example = "请尽快发货")
    private String remark;
    
    @ApiModelProperty(value = "订单项列表", required = true)
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    /**
     * 订单项请求
     */
    @ApiModel(description = "订单项信息")
    @Data
    public static class OrderItemRequest implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @ApiModelProperty(value = "商品ID", example = "P001", required = true)
        @NotNull(message = "商品ID不能为空")
        private String productId;
        
        @ApiModelProperty(value = "商品名称", example = "iPhone 15 Pro")
        private String productName;
        
        @ApiModelProperty(value = "数量", example = "1", required = true)
        @NotNull(message = "数量不能为空")
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        
        @ApiModelProperty(value = "单价", example = "8999.00", required = true)
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
        
        @ApiModelProperty(value = "货币类型", example = "CNY")
        private String currency = "CNY";
    }
    
    /**
     * 地址请求
     */
    @ApiModel(description = "地址信息")
    @Data
    public static class AddressRequest implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @ApiModelProperty(value = "省份", example = "广东省", required = true)
        @NotNull(message = "省份不能为空")
        private String province;
        
        @ApiModelProperty(value = "城市", example = "深圳市", required = true)
        @NotNull(message = "城市不能为空")
        private String city;
        
        @ApiModelProperty(value = "区县", example = "南山区")
        private String district;
        
        @ApiModelProperty(value = "详细地址", example = "科技园南区")
        private String detail;
        
        @ApiModelProperty(value = "邮编", example = "518000")
        private String zipCode;
    }
}

