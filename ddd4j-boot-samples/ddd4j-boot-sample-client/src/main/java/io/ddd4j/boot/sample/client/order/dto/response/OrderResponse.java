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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public AddressResponse getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(AddressResponse shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getPaidTime() { return paidTime; }
    public void setPaidTime(LocalDateTime paidTime) { this.paidTime = paidTime; }
    public LocalDateTime getShippedTime() { return shippedTime; }
    public void setShippedTime(LocalDateTime shippedTime) { this.shippedTime = shippedTime; }
    public LocalDateTime getDeliveredTime() { return deliveredTime; }
    public void setDeliveredTime(LocalDateTime deliveredTime) { this.deliveredTime = deliveredTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }

    /**
     * 地址信息。
     */
    @Schema(description = "地址信息")
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

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getDetail() { return detail; }
        public void setDetail(String detail) { this.detail = detail; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getFullAddress() { return fullAddress; }
        public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    }

    /**
     * 订单项信息。
     */
    @Schema(description = "订单项信息")
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

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}
