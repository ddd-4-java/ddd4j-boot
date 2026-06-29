package io.ddd4j.boot.sample.app.order.dto;

import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据传输对象（DTO）
 *
 * <p>用于应用层与接口层之间的数据传输，包含订单的完整信息。
 * 不包含领域逻辑，仅用于数据展示和传输。</p>
 *
 * <p>遵循DTO模式，将领域对象与接口层解耦，避免领域模型直接暴露给外部。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "订单信息")
@Data
public class OrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     * 数据库主键，唯一标识一个订单
     */
    @Schema(description = "订单ID", example = "1", required = true)
    private Long id;

    /**
     * 订单号
     * 业务唯一标识，格式：ORD + 日期时间 + 随机数
     */
    @Schema(description = "订单号", example = "ORD202412011200001234", required = true)
    private String orderNo;

    /**
     * 用户ID
     * 订单所属用户的ID
     */
    @Schema(description = "用户ID", example = "1001", required = true)
    private Long userId;

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    @Schema(description = "订单状态", example = "PENDING", required = true,
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private OrderStatus status;

    /**
     * 订单状态描述
     * 订单状态的中文描述，用于前端展示
     */
    @Schema(description = "订单状态描述", example = "待支付")
    private String statusDescription;

    /**
     * 订单总金额
     * 订单所有商品的总金额，单位：元
     */
    @Schema(description = "订单总金额（元）", example = "8999.00", required = true)
    private java.math.BigDecimal totalAmount;

    /**
     * 货币类型
     * 订单金额的货币类型，默认：CNY（人民币）
     */
    @Schema(description = "货币类型", example = "CNY", defaultValue = "CNY")
    private String currency;

    /**
     * 收货地址
     * 订单的收货地址信息
     */
    @Schema(description = "收货地址")
    private AddressDTO shippingAddress;

    /**
     * 备注
     * 用户下单时的备注信息
     */
    @Schema(description = "备注", example = "请尽快发货")
    private String remark;

    /**
     * 支付时间
     * 订单支付完成的时间
     */
    @Schema(description = "支付时间", example = "2024-12-01T12:00:00")
    private LocalDateTime paidTime;

    /**
     * 发货时间
     * 订单发货的时间
     */
    @Schema(description = "发货时间", example = "2024-12-02T10:00:00")
    private LocalDateTime shippedTime;

    /**
     * 送达时间
     * 订单送达的时间
     */
    @Schema(description = "送达时间", example = "2024-12-03T15:00:00")
    private LocalDateTime deliveredTime;

    /**
     * 创建时间
     * 订单创建的时间
     */
    @Schema(description = "创建时间", example = "2024-12-01T10:00:00", required = true)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 订单最后更新的时间
     */
    @Schema(description = "更新时间", example = "2024-12-01T12:00:00", required = true)
    private LocalDateTime updatedAt;

    /**
     * 订单项列表
     * 订单包含的所有商品项
     */
    @Schema(description = "订单项列表", required = true)
    private List<OrderItemDTO> items;

    /**
     * 地址信息DTO
     * 用于传输订单的收货地址信息
     */
    @Schema(description = "地址信息")
    @Data
    public static class AddressDTO implements Serializable {

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
     * 订单项DTO
     * 用于传输订单项（商品）的详细信息
     */
    @Schema(description = "订单项信息")
    @Data
    public static class OrderItemDTO implements Serializable {

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
        private java.math.BigDecimal unitPrice;

        @Schema(description = "总价", example = "8999.00")
        private java.math.BigDecimal totalPrice;

        @Schema(description = "货币类型", example = "CNY")
        private String currency;
    }
}

