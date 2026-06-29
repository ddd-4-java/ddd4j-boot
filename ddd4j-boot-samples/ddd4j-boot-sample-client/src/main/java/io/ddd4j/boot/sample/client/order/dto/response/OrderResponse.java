package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "订单信息")
@Data
public class OrderResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID", example = "1")
    private Long id;

    @Schema(description = "订单号", example = "ORD202412011200001234")
    private String orderNo;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "订单状态", example = "PENDING",
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "订单状态描述", example = "待支付")
    private String statusDescription;

    @Schema(description = "订单总金额（元）", example = "8999.00")
    private BigDecimal totalAmount;

    @Schema(description = "货币类型", example = "CNY")
    private String currency;

    @Schema(description = "收货地址")
    private AddressResponse shippingAddress;

    @Schema(description = "备注", example = "请尽快发货")
    private String remark;

    @Schema(description = "支付时间")
    private LocalDateTime paidTime;

    @Schema(description = "发货时间")
    private LocalDateTime shippedTime;

    @Schema(description = "送达时间")
    private LocalDateTime deliveredTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "订单项列表")
    private List<OrderItemResponse> items;

    /**
     * 地址信息
     */
    @Schema(description = "地址信息")
    @Data
    public static class AddressResponse implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "省份", example = "广东省")
        private String province;

        @Schema(description = "城市", example = "深圳市")
        private String city;

        @Schema(description = "区县", example = "南山区")
        private String district;

        @Schema(description = "详细地址", example = "科技园南区")
        private String detail;

        @Schema(description = "邮编", example = "518000")
        private String zipCode;

        @Schema(description = "完整地址", example = "广东省深圳市南山区科技园南区")
        private String fullAddress;
    }

    /**
     * 订单项信息
     */
    @Schema(description = "订单项信息")
    @Data
    public static class OrderItemResponse implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "订单项ID", example = "1")
        private Long id;

        @Schema(description = "商品ID", example = "P001")
        private String productId;

        @Schema(description = "商品名称", example = "iPhone 15 Pro")
        private String productName;

        @Schema(description = "数量", example = "1")
        private Integer quantity;

        @Schema(description = "单价", example = "8999.00")
        private BigDecimal unitPrice;

        @Schema(description = "总价", example = "8999.00")
        private BigDecimal totalPrice;

        @Schema(description = "货币类型", example = "CNY")
        private String currency;
    }
}

