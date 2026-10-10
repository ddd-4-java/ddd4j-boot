package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应对象（客户端SDK使用）。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式实现 getter/setter，
 * 保持 JSON 绑定、Swagger 注解与示例语义不变。</p>
 */
@Schema(description = "订单信息")
public class OrderResponse implements Serializable {

    /**
     * 构造OrderResponse对象（默认无参构造，字段由调用方逐个设置）。
     */
    public OrderResponse() {
    }

    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @Schema(description = "订单ID", example = "1")
    private Long id;

    /** 订单号 */
    @Schema(description = "订单号", example = "ORD202412011200001234")
    private String orderNo;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    /** 订单状态 */
    @Schema(description = "订单状态", example = "PENDING",
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private String status;

    /** 订单状态描述 */
    @Schema(description = "订单状态描述", example = "待支付")
    private String statusDescription;

    /** 订单总金额（元） */
    @Schema(description = "订单总金额（元）", example = "8999.00")
    private BigDecimal totalAmount;

    /** 货币类型 */
    @Schema(description = "货币类型", example = "CNY")
    private String currency;

    /** 收货地址 */
    @Schema(description = "收货地址")
    private AddressResponse shippingAddress;

    /** 备注 */
    @Schema(description = "备注", example = "请尽快发货")
    private String remark;

    /** 支付时间 */
    @Schema(description = "支付时间")
    private LocalDateTime paidTime;

    /** 发货时间 */
    @Schema(description = "发货时间")
    private LocalDateTime shippedTime;

    /** 送达时间 */
    @Schema(description = "送达时间")
    private LocalDateTime deliveredTime;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /** 订单项列表 */
    @Schema(description = "订单项列表")
    private List<OrderItemResponse> items;

    /** 获取订单ID。
     * @return 订单ID */
    public Long getId() { return id; }
    /** 设置订单ID。
     * @param id 订单ID */
    public void setId(Long id) { this.id = id; }
    /** 获取订单号。
     * @return 订单号 */
    public String getOrderNo() { return orderNo; }
    /** 设置订单号。
     * @param orderNo 订单号 */
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    /** 获取用户ID。
     * @return 用户ID */
    public Long getUserId() { return userId; }
    /** 设置用户ID。
     * @param userId 用户ID */
    public void setUserId(Long userId) { this.userId = userId; }
    /** 获取订单状态。
     * @return 订单状态 */
    public String getStatus() { return status; }
    /** 设置订单状态。
     * @param status 订单状态 */
    public void setStatus(String status) { this.status = status; }
    /** 获取订单状态描述。
     * @return 订单状态描述 */
    public String getStatusDescription() { return statusDescription; }
    /** 设置订单状态描述。
     * @param statusDescription 订单状态描述 */
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    /** 获取订单总金额（元）。
     * @return 订单总金额（元） */
    public BigDecimal getTotalAmount() { return totalAmount; }
    /** 设置订单总金额（元）。
     * @param totalAmount 订单总金额（元） */
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    /** 获取货币类型。
     * @return 货币类型 */
    public String getCurrency() { return currency; }
    /** 设置货币类型。
     * @param currency 货币类型 */
    public void setCurrency(String currency) { this.currency = currency; }
    /** 获取收货地址。
     * @return 收货地址 */
    public AddressResponse getShippingAddress() { return shippingAddress; }
    /** 设置收货地址。
     * @param shippingAddress 收货地址 */
    public void setShippingAddress(AddressResponse shippingAddress) { this.shippingAddress = shippingAddress; }
    /** 获取备注。
     * @return 备注 */
    public String getRemark() { return remark; }
    /** 设置备注。
     * @param remark 备注 */
    public void setRemark(String remark) { this.remark = remark; }
    /** 获取支付时间。
     * @return 支付时间 */
    public LocalDateTime getPaidTime() { return paidTime; }
    /** 设置支付时间。
     * @param paidTime 支付时间 */
    public void setPaidTime(LocalDateTime paidTime) { this.paidTime = paidTime; }
    /** 获取发货时间。
     * @return 发货时间 */
    public LocalDateTime getShippedTime() { return shippedTime; }
    /** 设置发货时间。
     * @param shippedTime 发货时间 */
    public void setShippedTime(LocalDateTime shippedTime) { this.shippedTime = shippedTime; }
    /** 获取送达时间。
     * @return 送达时间 */
    public LocalDateTime getDeliveredTime() { return deliveredTime; }
    /** 设置送达时间。
     * @param deliveredTime 送达时间 */
    public void setDeliveredTime(LocalDateTime deliveredTime) { this.deliveredTime = deliveredTime; }
    /** 获取创建时间。
     * @return 创建时间 */
    public LocalDateTime getCreatedAt() { return createdAt; }
    /** 设置创建时间。
     * @param createdAt 创建时间 */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    /** 获取更新时间。
     * @return 更新时间 */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    /** 设置更新时间。
     * @param updatedAt 更新时间 */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    /** 获取订单项列表。
     * @return 订单项列表 */
    public List<OrderItemResponse> getItems() { return items; }
    /** 设置订单项列表。
     * @param items 订单项列表 */
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    /**
     * 地址信息。
     */
    @Schema(description = "地址信息")
    public static class AddressResponse implements Serializable {

        /**
         * 构造AddressResponse对象（默认无参构造，字段由调用方逐个设置）。
         */
        public AddressResponse() {
        }

        /** 序列化版本UID */
        private static final long serialVersionUID = 1L;

        /** 省份 */
        @Schema(description = "省份", example = "广东省")
        private String province;
        /** 城市 */
        @Schema(description = "城市", example = "深圳市")
        private String city;
        /** 区县 */
        @Schema(description = "区县", example = "南山区")
        private String district;
        /** 详细地址 */
        @Schema(description = "详细地址", example = "科技园南区")
        private String detail;
        /** 邮编 */
        @Schema(description = "邮编", example = "518000")
        private String zipCode;
        /** 完整地址 */
        @Schema(description = "完整地址", example = "广东省深圳市南山区科技园南区")
        private String fullAddress;

        /** 获取省份。
         * @return 省份 */
        public String getProvince() { return province; }
        /** 设置省份。
         * @param province 省份 */
        public void setProvince(String province) { this.province = province; }
        /** 获取城市。
         * @return 城市 */
        public String getCity() { return city; }
        /** 设置城市。
         * @param city 城市 */
        public void setCity(String city) { this.city = city; }
        /** 获取区县。
         * @return 区县 */
        public String getDistrict() { return district; }
        /** 设置区县。
         * @param district 区县 */
        public void setDistrict(String district) { this.district = district; }
        /** 获取详细地址。
         * @return 详细地址 */
        public String getDetail() { return detail; }
        /** 设置详细地址。
         * @param detail 详细地址 */
        public void setDetail(String detail) { this.detail = detail; }
        /** 获取邮编。
         * @return 邮编 */
        public String getZipCode() { return zipCode; }
        /** 设置邮编。
         * @param zipCode 邮编 */
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        /** 获取完整地址。
         * @return 完整地址 */
        public String getFullAddress() { return fullAddress; }
        /** 设置完整地址。
         * @param fullAddress 完整地址 */
        public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    }

    /**
     * 订单项信息。
     */
    @Schema(description = "订单项信息")
    public static class OrderItemResponse implements Serializable {

        /**
         * 构造OrderItemResponse对象（默认无参构造，字段由调用方逐个设置）。
         */
        public OrderItemResponse() {
        }

        /** 序列化版本UID */
        private static final long serialVersionUID = 1L;

        /** 订单项ID */
        @Schema(description = "订单项ID", example = "1")
        private Long id;
        /** 商品ID */
        @Schema(description = "商品ID", example = "P001")
        private String productId;
        /** 商品名称 */
        @Schema(description = "商品名称", example = "iPhone 15 Pro")
        private String productName;
        /** 数量 */
        @Schema(description = "数量", example = "1")
        private Integer quantity;
        /** 单价 */
        @Schema(description = "单价", example = "8999.00")
        private BigDecimal unitPrice;
        /** 总价 */
        @Schema(description = "总价", example = "8999.00")
        private BigDecimal totalPrice;
        /** 货币类型 */
        @Schema(description = "货币类型", example = "CNY")
        private String currency;

        /** 获取订单项ID。
         * @return 订单项ID */
        public Long getId() { return id; }
        /** 设置订单项ID。
         * @param id 订单项ID */
        public void setId(Long id) { this.id = id; }
        /** 获取商品ID。
         * @return 商品ID */
        public String getProductId() { return productId; }
        /** 设置商品ID。
         * @param productId 商品ID */
        public void setProductId(String productId) { this.productId = productId; }
        /** 获取商品名称。
         * @return 商品名称 */
        public String getProductName() { return productName; }
        /** 设置商品名称。
         * @param productName 商品名称 */
        public void setProductName(String productName) { this.productName = productName; }
        /** 获取数量。
         * @return 数量 */
        public Integer getQuantity() { return quantity; }
        /** 设置数量。
         * @param quantity 数量 */
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        /** 获取单价。
         * @return 单价 */
        public BigDecimal getUnitPrice() { return unitPrice; }
        /** 设置单价。
         * @param unitPrice 单价 */
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        /** 获取总价。
         * @return 总价 */
        public BigDecimal getTotalPrice() { return totalPrice; }
        /** 设置总价。
         * @param totalPrice 总价 */
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
        /** 获取货币类型。
         * @return 货币类型 */
        public String getCurrency() { return currency; }
        /** 设置货币类型。
         * @param currency 货币类型 */
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
