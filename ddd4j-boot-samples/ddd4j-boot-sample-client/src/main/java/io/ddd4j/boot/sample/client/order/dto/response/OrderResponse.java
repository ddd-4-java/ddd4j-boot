package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应对象（客户端SDK使用）
 * 
 * <p>用于客户端SDK与服务端之间的数据传输。
 * 这是对外提供的接口响应对象，不包含内部实现细节。</p>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
@ApiModel(description = "订单信息")
@Data
public class OrderResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "订单ID", example = "1")
    private Long id;
    
    @ApiModelProperty(value = "订单号", example = "ORD202412011200001234")
    private String orderNo;
    
    @ApiModelProperty(value = "用户ID", example = "1001")
    private Long userId;
    
    @ApiModelProperty(value = "订单状态", example = "PENDING", 
            allowableValues = "PENDING,PAID,SHIPPED,DELIVERED,COMPLETED,CANCELLED")
    private String status;
    
    @ApiModelProperty(value = "订单状态描述", example = "待支付")
    private String statusDescription;
    
    @ApiModelProperty(value = "订单总金额（元）", example = "8999.00")
    private BigDecimal totalAmount;
    
    @ApiModelProperty(value = "货币类型", example = "CNY")
    private String currency;
    
    @ApiModelProperty(value = "收货地址")
    private AddressResponse shippingAddress;
    
    @ApiModelProperty(value = "备注", example = "请尽快发货")
    private String remark;
    
    @ApiModelProperty(value = "支付时间")
    private LocalDateTime paidTime;
    
    @ApiModelProperty(value = "发货时间")
    private LocalDateTime shippedTime;
    
    @ApiModelProperty(value = "送达时间")
    private LocalDateTime deliveredTime;
    
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createdAt;
    
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updatedAt;
    
    @ApiModelProperty(value = "订单项列表")
    private List<OrderItemResponse> items;
    
    /**
     * 地址信息
     */
    @ApiModel(value = "地址信息")
    @Data
    public static class AddressResponse implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @ApiModelProperty(value = "省份", example = "广东省")
        private String province;
        
        @ApiModelProperty(value = "城市", example = "深圳市")
        private String city;
        
        @ApiModelProperty(value = "区县", example = "南山区")
        private String district;
        
        @ApiModelProperty(value = "详细地址", example = "科技园南区")
        private String detail;
        
        @ApiModelProperty(value = "邮编", example = "518000")
        private String zipCode;
        
        @ApiModelProperty(value = "完整地址", example = "广东省深圳市南山区科技园南区")
        private String fullAddress;
    }
    
    /**
     * 订单项信息
     */
    @ApiModel(value = "订单项信息")
    @Data
    public static class OrderItemResponse implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
        @ApiModelProperty(value = "订单项ID", example = "1")
        private Long id;
        
        @ApiModelProperty(value = "商品ID", example = "P001")
        private String productId;
        
        @ApiModelProperty(value = "商品名称", example = "iPhone 15 Pro")
        private String productName;
        
        @ApiModelProperty(value = "数量", example = "1")
        private Integer quantity;
        
        @ApiModelProperty(value = "单价", example = "8999.00")
        private BigDecimal unitPrice;
        
        @ApiModelProperty(value = "总价", example = "8999.00")
        private BigDecimal totalPrice;
        
        @ApiModelProperty(value = "货币类型", example = "CNY")
        private String currency;
    }
}

