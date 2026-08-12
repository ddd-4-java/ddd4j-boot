package io.ddd4j.boot.sample.app.order.dto;

import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "订单状态", example = "PENDING", required = true, allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
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

        public AddressDTO() {
        }

        public String getProvince() {
            return this.province;
        }

        public String getCity() {
            return this.city;
        }

        public String getDistrict() {
            return this.district;
        }

        public String getDetail() {
            return this.detail;
        }

        public String getZipCode() {
            return this.zipCode;
        }

        public String getFullAddress() {
            return this.fullAddress;
        }

        public void setProvince(final String province) {
            this.province = province;
        }

        public void setCity(final String city) {
            this.city = city;
        }

        public void setDistrict(final String district) {
            this.district = district;
        }

        public void setDetail(final String detail) {
            this.detail = detail;
        }

        public void setZipCode(final String zipCode) {
            this.zipCode = zipCode;
        }

        public void setFullAddress(final String fullAddress) {
            this.fullAddress = fullAddress;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OrderDTO.AddressDTO)) return false;
            final OrderDTO.AddressDTO other = (OrderDTO.AddressDTO) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$province = this.getProvince();
            final java.lang.Object other$province = other.getProvince();
            if (this$province == null ? other$province != null : !this$province.equals(other$province)) return false;
            final java.lang.Object this$city = this.getCity();
            final java.lang.Object other$city = other.getCity();
            if (this$city == null ? other$city != null : !this$city.equals(other$city)) return false;
            final java.lang.Object this$district = this.getDistrict();
            final java.lang.Object other$district = other.getDistrict();
            if (this$district == null ? other$district != null : !this$district.equals(other$district)) return false;
            final java.lang.Object this$detail = this.getDetail();
            final java.lang.Object other$detail = other.getDetail();
            if (this$detail == null ? other$detail != null : !this$detail.equals(other$detail)) return false;
            final java.lang.Object this$zipCode = this.getZipCode();
            final java.lang.Object other$zipCode = other.getZipCode();
            if (this$zipCode == null ? other$zipCode != null : !this$zipCode.equals(other$zipCode)) return false;
            final java.lang.Object this$fullAddress = this.getFullAddress();
            final java.lang.Object other$fullAddress = other.getFullAddress();
            if (this$fullAddress == null ? other$fullAddress != null : !this$fullAddress.equals(other$fullAddress)) return false;
            return true;
        }

        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OrderDTO.AddressDTO;
        }

        @java.lang.Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $province = this.getProvince();
            result = result * PRIME + ($province == null ? 43 : $province.hashCode());
            final java.lang.Object $city = this.getCity();
            result = result * PRIME + ($city == null ? 43 : $city.hashCode());
            final java.lang.Object $district = this.getDistrict();
            result = result * PRIME + ($district == null ? 43 : $district.hashCode());
            final java.lang.Object $detail = this.getDetail();
            result = result * PRIME + ($detail == null ? 43 : $detail.hashCode());
            final java.lang.Object $zipCode = this.getZipCode();
            result = result * PRIME + ($zipCode == null ? 43 : $zipCode.hashCode());
            final java.lang.Object $fullAddress = this.getFullAddress();
            result = result * PRIME + ($fullAddress == null ? 43 : $fullAddress.hashCode());
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "OrderDTO.AddressDTO(province=" + this.getProvince() + ", city=" + this.getCity() + ", district=" + this.getDistrict() + ", detail=" + this.getDetail() + ", zipCode=" + this.getZipCode() + ", fullAddress=" + this.getFullAddress() + ")";
        }
    }


    /**
     * 订单项DTO
     * 用于传输订单项（商品）的详细信息
     */
    @Schema(description = "订单项信息")
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

        public OrderItemDTO() {
        }

        public Long getId() {
            return this.id;
        }

        public String getProductId() {
            return this.productId;
        }

        public String getProductName() {
            return this.productName;
        }

        public Integer getQuantity() {
            return this.quantity;
        }

        public java.math.BigDecimal getUnitPrice() {
            return this.unitPrice;
        }

        public java.math.BigDecimal getTotalPrice() {
            return this.totalPrice;
        }

        public String getCurrency() {
            return this.currency;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public void setProductId(final String productId) {
            this.productId = productId;
        }

        public void setProductName(final String productName) {
            this.productName = productName;
        }

        public void setQuantity(final Integer quantity) {
            this.quantity = quantity;
        }

        public void setUnitPrice(final java.math.BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setTotalPrice(final java.math.BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public void setCurrency(final String currency) {
            this.currency = currency;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof OrderDTO.OrderItemDTO)) return false;
            final OrderDTO.OrderItemDTO other = (OrderDTO.OrderItemDTO) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$id = this.getId();
            final java.lang.Object other$id = other.getId();
            if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
            final java.lang.Object this$quantity = this.getQuantity();
            final java.lang.Object other$quantity = other.getQuantity();
            if (this$quantity == null ? other$quantity != null : !this$quantity.equals(other$quantity)) return false;
            final java.lang.Object this$productId = this.getProductId();
            final java.lang.Object other$productId = other.getProductId();
            if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId)) return false;
            final java.lang.Object this$productName = this.getProductName();
            final java.lang.Object other$productName = other.getProductName();
            if (this$productName == null ? other$productName != null : !this$productName.equals(other$productName)) return false;
            final java.lang.Object this$unitPrice = this.getUnitPrice();
            final java.lang.Object other$unitPrice = other.getUnitPrice();
            if (this$unitPrice == null ? other$unitPrice != null : !this$unitPrice.equals(other$unitPrice)) return false;
            final java.lang.Object this$totalPrice = this.getTotalPrice();
            final java.lang.Object other$totalPrice = other.getTotalPrice();
            if (this$totalPrice == null ? other$totalPrice != null : !this$totalPrice.equals(other$totalPrice)) return false;
            final java.lang.Object this$currency = this.getCurrency();
            final java.lang.Object other$currency = other.getCurrency();
            if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) return false;
            return true;
        }

        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof OrderDTO.OrderItemDTO;
        }

        @java.lang.Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $id = this.getId();
            result = result * PRIME + ($id == null ? 43 : $id.hashCode());
            final java.lang.Object $quantity = this.getQuantity();
            result = result * PRIME + ($quantity == null ? 43 : $quantity.hashCode());
            final java.lang.Object $productId = this.getProductId();
            result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
            final java.lang.Object $productName = this.getProductName();
            result = result * PRIME + ($productName == null ? 43 : $productName.hashCode());
            final java.lang.Object $unitPrice = this.getUnitPrice();
            result = result * PRIME + ($unitPrice == null ? 43 : $unitPrice.hashCode());
            final java.lang.Object $totalPrice = this.getTotalPrice();
            result = result * PRIME + ($totalPrice == null ? 43 : $totalPrice.hashCode());
            final java.lang.Object $currency = this.getCurrency();
            result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "OrderDTO.OrderItemDTO(id=" + this.getId() + ", productId=" + this.getProductId() + ", productName=" + this.getProductName() + ", quantity=" + this.getQuantity() + ", unitPrice=" + this.getUnitPrice() + ", totalPrice=" + this.getTotalPrice() + ", currency=" + this.getCurrency() + ")";
        }
    }

    public OrderDTO() {
    }

    /**
     * 订单ID
     * 数据库主键，唯一标识一个订单
     */
    public Long getId() {
        return this.id;
    }

    /**
     * 订单号
     * 业务唯一标识，格式：ORD + 日期时间 + 随机数
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * 用户ID
     * 订单所属用户的ID
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    public OrderStatus getStatus() {
        return this.status;
    }

    /**
     * 订单状态描述
     * 订单状态的中文描述，用于前端展示
     */
    public String getStatusDescription() {
        return this.statusDescription;
    }

    /**
     * 订单总金额
     * 订单所有商品的总金额，单位：元
     */
    public java.math.BigDecimal getTotalAmount() {
        return this.totalAmount;
    }

    /**
     * 货币类型
     * 订单金额的货币类型，默认：CNY（人民币）
     */
    public String getCurrency() {
        return this.currency;
    }

    /**
     * 收货地址
     * 订单的收货地址信息
     */
    public AddressDTO getShippingAddress() {
        return this.shippingAddress;
    }

    /**
     * 备注
     * 用户下单时的备注信息
     */
    public String getRemark() {
        return this.remark;
    }

    /**
     * 支付时间
     * 订单支付完成的时间
     */
    public LocalDateTime getPaidTime() {
        return this.paidTime;
    }

    /**
     * 发货时间
     * 订单发货的时间
     */
    public LocalDateTime getShippedTime() {
        return this.shippedTime;
    }

    /**
     * 送达时间
     * 订单送达的时间
     */
    public LocalDateTime getDeliveredTime() {
        return this.deliveredTime;
    }

    /**
     * 创建时间
     * 订单创建的时间
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    /**
     * 更新时间
     * 订单最后更新的时间
     */
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * 订单项列表
     * 订单包含的所有商品项
     */
    public List<OrderItemDTO> getItems() {
        return this.items;
    }

    /**
     * 订单ID
     * 数据库主键，唯一标识一个订单
     */
    public void setId(final Long id) {
        this.id = id;
    }

    /**
     * 订单号
     * 业务唯一标识，格式：ORD + 日期时间 + 随机数
     */
    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 用户ID
     * 订单所属用户的ID
     */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    /**
     * 订单状态
     * PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    /**
     * 订单状态描述
     * 订单状态的中文描述，用于前端展示
     */
    public void setStatusDescription(final String statusDescription) {
        this.statusDescription = statusDescription;
    }

    /**
     * 订单总金额
     * 订单所有商品的总金额，单位：元
     */
    public void setTotalAmount(final java.math.BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 货币类型
     * 订单金额的货币类型，默认：CNY（人民币）
     */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /**
     * 收货地址
     * 订单的收货地址信息
     */
    public void setShippingAddress(final AddressDTO shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * 备注
     * 用户下单时的备注信息
     */
    public void setRemark(final String remark) {
        this.remark = remark;
    }

    /**
     * 支付时间
     * 订单支付完成的时间
     */
    public void setPaidTime(final LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    /**
     * 发货时间
     * 订单发货的时间
     */
    public void setShippedTime(final LocalDateTime shippedTime) {
        this.shippedTime = shippedTime;
    }

    /**
     * 送达时间
     * 订单送达的时间
     */
    public void setDeliveredTime(final LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /**
     * 创建时间
     * 订单创建的时间
     */
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 更新时间
     * 订单最后更新的时间
     */
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 订单项列表
     * 订单包含的所有商品项
     */
    public void setItems(final List<OrderItemDTO> items) {
        this.items = items;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderDTO)) return false;
        final OrderDTO other = (OrderDTO) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$orderNo = this.getOrderNo();
        final java.lang.Object other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final java.lang.Object this$statusDescription = this.getStatusDescription();
        final java.lang.Object other$statusDescription = other.getStatusDescription();
        if (this$statusDescription == null ? other$statusDescription != null : !this$statusDescription.equals(other$statusDescription)) return false;
        final java.lang.Object this$totalAmount = this.getTotalAmount();
        final java.lang.Object other$totalAmount = other.getTotalAmount();
        if (this$totalAmount == null ? other$totalAmount != null : !this$totalAmount.equals(other$totalAmount)) return false;
        final java.lang.Object this$currency = this.getCurrency();
        final java.lang.Object other$currency = other.getCurrency();
        if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) return false;
        final java.lang.Object this$shippingAddress = this.getShippingAddress();
        final java.lang.Object other$shippingAddress = other.getShippingAddress();
        if (this$shippingAddress == null ? other$shippingAddress != null : !this$shippingAddress.equals(other$shippingAddress)) return false;
        final java.lang.Object this$remark = this.getRemark();
        final java.lang.Object other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) return false;
        final java.lang.Object this$paidTime = this.getPaidTime();
        final java.lang.Object other$paidTime = other.getPaidTime();
        if (this$paidTime == null ? other$paidTime != null : !this$paidTime.equals(other$paidTime)) return false;
        final java.lang.Object this$shippedTime = this.getShippedTime();
        final java.lang.Object other$shippedTime = other.getShippedTime();
        if (this$shippedTime == null ? other$shippedTime != null : !this$shippedTime.equals(other$shippedTime)) return false;
        final java.lang.Object this$deliveredTime = this.getDeliveredTime();
        final java.lang.Object other$deliveredTime = other.getDeliveredTime();
        if (this$deliveredTime == null ? other$deliveredTime != null : !this$deliveredTime.equals(other$deliveredTime)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$updatedAt = this.getUpdatedAt();
        final java.lang.Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        final java.lang.Object this$items = this.getItems();
        final java.lang.Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OrderDTO;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $orderNo = this.getOrderNo();
        result = result * PRIME + ($orderNo == null ? 43 : $orderNo.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $statusDescription = this.getStatusDescription();
        result = result * PRIME + ($statusDescription == null ? 43 : $statusDescription.hashCode());
        final java.lang.Object $totalAmount = this.getTotalAmount();
        result = result * PRIME + ($totalAmount == null ? 43 : $totalAmount.hashCode());
        final java.lang.Object $currency = this.getCurrency();
        result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
        final java.lang.Object $shippingAddress = this.getShippingAddress();
        result = result * PRIME + ($shippingAddress == null ? 43 : $shippingAddress.hashCode());
        final java.lang.Object $remark = this.getRemark();
        result = result * PRIME + ($remark == null ? 43 : $remark.hashCode());
        final java.lang.Object $paidTime = this.getPaidTime();
        result = result * PRIME + ($paidTime == null ? 43 : $paidTime.hashCode());
        final java.lang.Object $shippedTime = this.getShippedTime();
        result = result * PRIME + ($shippedTime == null ? 43 : $shippedTime.hashCode());
        final java.lang.Object $deliveredTime = this.getDeliveredTime();
        result = result * PRIME + ($deliveredTime == null ? 43 : $deliveredTime.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        final java.lang.Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "OrderDTO(id=" + this.getId() + ", orderNo=" + this.getOrderNo() + ", userId=" + this.getUserId() + ", status=" + this.getStatus() + ", statusDescription=" + this.getStatusDescription() + ", totalAmount=" + this.getTotalAmount() + ", currency=" + this.getCurrency() + ", shippingAddress=" + this.getShippingAddress() + ", remark=" + this.getRemark() + ", paidTime=" + this.getPaidTime() + ", shippedTime=" + this.getShippedTime() + ", deliveredTime=" + this.getDeliveredTime() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", items=" + this.getItems() + ")";
    }
}
