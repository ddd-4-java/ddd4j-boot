package io.ddd4j.boot.sample.app.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单命令（Command）
 *
 * <p>遵循CQRS模式，用于创建订单的写操作。
 * 命令对象包含创建订单所需的所有信息，由接口层传入应用层。</p>
 *
 * <p>命令对象的特点：
 * <ul>
 *   <li>不可变（建议使用final字段）</li>
 *   <li>包含验证规则</li>
 *   <li>表达用户意图</li>
 * </ul>
 * </p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "创建订单请求")
public class CreateOrderCommand implements Serializable {
    private static final long serialVersionUID = 1L;
    @Schema(description = "用户ID", example = "1001", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @Schema(description = "收货地址", required = true)
    @NotNull(message = "收货地址不能为空")
    @Valid
    private AddressCommand shippingAddress;
    @Schema(description = "备注", example = "请尽快发货")
    private String remark;
    @Schema(description = "订单项列表", required = true)
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemCommand> items;


    /**
     * 订单项命令
     * 创建订单时包含的商品项信息
     */
    @Schema(description = "订单项信息")
    public static class OrderItemCommand implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "商品ID", example = "P001", required = true)
        @NotNull(message = "商品ID不能为空")
        private String productId;
        @Schema(description = "商品名称", example = "iPhone 15 Pro")
        private String productName;
        @Schema(description = "数量", example = "1", required = true)
        @NotNull(message = "数量不能为空")
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        @Schema(description = "单价", example = "8999.00", required = true)
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
        @Schema(description = "货币类型", example = "CNY")
        private String currency;

        public OrderItemCommand() {
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

        public BigDecimal getUnitPrice() {
            return this.unitPrice;
        }

        public String getCurrency() {
            return this.currency;
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

        public void setUnitPrice(final BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public void setCurrency(final String currency) {
            this.currency = currency;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof CreateOrderCommand.OrderItemCommand)) return false;
            final CreateOrderCommand.OrderItemCommand other = (CreateOrderCommand.OrderItemCommand) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
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
            final java.lang.Object this$currency = this.getCurrency();
            final java.lang.Object other$currency = other.getCurrency();
            if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) return false;
            return true;
        }

        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof CreateOrderCommand.OrderItemCommand;
        }

        @java.lang.Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $quantity = this.getQuantity();
            result = result * PRIME + ($quantity == null ? 43 : $quantity.hashCode());
            final java.lang.Object $productId = this.getProductId();
            result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
            final java.lang.Object $productName = this.getProductName();
            result = result * PRIME + ($productName == null ? 43 : $productName.hashCode());
            final java.lang.Object $unitPrice = this.getUnitPrice();
            result = result * PRIME + ($unitPrice == null ? 43 : $unitPrice.hashCode());
            final java.lang.Object $currency = this.getCurrency();
            result = result * PRIME + ($currency == null ? 43 : $currency.hashCode());
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "CreateOrderCommand.OrderItemCommand(productId=" + this.getProductId() + ", productName=" + this.getProductName() + ", quantity=" + this.getQuantity() + ", unitPrice=" + this.getUnitPrice() + ", currency=" + this.getCurrency() + ")";
        }
    }


    /**
     * 地址命令
     * 创建订单时的收货地址信息
     */
    @Schema(description = "地址信息")
    public static class AddressCommand implements Serializable {
        private static final long serialVersionUID = 1L;
        @Schema(description = "省份", example = "广东省", required = true)
        @NotNull(message = "省份不能为空")
        private String province;
        @Schema(description = "城市", example = "深圳市", required = true)
        @NotNull(message = "城市不能为空")
        private String city;
        @Schema(description = "区县", example = "南山区")
        private String district;
        @Schema(description = "详细地址", example = "科技园南区")
        private String detail;
        @Schema(description = "邮编", example = "518000")
        private String zipCode;

        public AddressCommand() {
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

        @java.lang.Override
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof CreateOrderCommand.AddressCommand)) return false;
            final CreateOrderCommand.AddressCommand other = (CreateOrderCommand.AddressCommand) o;
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
            return true;
        }

        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof CreateOrderCommand.AddressCommand;
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
            return result;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "CreateOrderCommand.AddressCommand(province=" + this.getProvince() + ", city=" + this.getCity() + ", district=" + this.getDistrict() + ", detail=" + this.getDetail() + ", zipCode=" + this.getZipCode() + ")";
        }
    }

    public CreateOrderCommand() {
    }

    public Long getUserId() {
        return this.userId;
    }

    public AddressCommand getShippingAddress() {
        return this.shippingAddress;
    }

    public String getRemark() {
        return this.remark;
    }

    public List<OrderItemCommand> getItems() {
        return this.items;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public void setShippingAddress(final AddressCommand shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public void setItems(final List<OrderItemCommand> items) {
        this.items = items;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof CreateOrderCommand)) return false;
        final CreateOrderCommand other = (CreateOrderCommand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$shippingAddress = this.getShippingAddress();
        final java.lang.Object other$shippingAddress = other.getShippingAddress();
        if (this$shippingAddress == null ? other$shippingAddress != null : !this$shippingAddress.equals(other$shippingAddress)) return false;
        final java.lang.Object this$remark = this.getRemark();
        final java.lang.Object other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) return false;
        final java.lang.Object this$items = this.getItems();
        final java.lang.Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CreateOrderCommand;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $shippingAddress = this.getShippingAddress();
        result = result * PRIME + ($shippingAddress == null ? 43 : $shippingAddress.hashCode());
        final java.lang.Object $remark = this.getRemark();
        result = result * PRIME + ($remark == null ? 43 : $remark.hashCode());
        final java.lang.Object $items = this.getItems();
        result = result * PRIME + ($items == null ? 43 : $items.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "CreateOrderCommand(userId=" + this.getUserId() + ", shippingAddress=" + this.getShippingAddress() + ", remark=" + this.getRemark() + ", items=" + this.getItems() + ")";
    }
}
