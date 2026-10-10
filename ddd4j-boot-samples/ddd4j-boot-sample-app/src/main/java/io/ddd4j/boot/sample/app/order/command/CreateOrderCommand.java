package io.ddd4j.boot.sample.app.order.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单命令（Command）
 *
 * <p>遵循CQRS模式，用于创建订单的写操作。
 * 命令对象包含创建订单所需的所有信息，由接口层传入应用层。</p>
 *
 * <p>命令对象的特点：</p>
 * <ul>
 *   <li>不可变（建议使用final字段）</li>
 *   <li>包含验证规则</li>
 *   <li>表达用户意图</li>
 * </ul>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "创建订单请求")
public class CreateOrderCommand implements Serializable {
    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;
    /** 用户ID */
    @Schema(description = "用户ID", example = "1001", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    /** 收货地址 */
    @Schema(description = "收货地址", required = true)
    @NotNull(message = "收货地址不能为空")
    @Valid
    private AddressCommand shippingAddress;
    /** 备注 */
    @Schema(description = "备注", example = "请尽快发货")
    private String remark;
    /** 订单项列表 */
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
        /** 序列化版本UID */
        private static final long serialVersionUID = 1L;
        /** 商品ID */
        @Schema(description = "商品ID", example = "P001", required = true)
        @NotNull(message = "商品ID不能为空")
        private String productId;
        /** 商品名称 */
        @Schema(description = "商品名称", example = "iPhone 15 Pro")
        private String productName;
        /** 数量 */
        @Schema(description = "数量", example = "1", required = true)
        @NotNull(message = "数量不能为空")
        @Positive(message = "数量必须大于0")
        private Integer quantity;
        /** 单价 */
        @Schema(description = "单价", example = "8999.00", required = true)
        @NotNull(message = "单价不能为空")
        private BigDecimal unitPrice;
        /** 货币类型 */
        @Schema(description = "货币类型", example = "CNY")
        private String currency;

        /** 构造 OrderItemCommand 对象。 */
        public OrderItemCommand() {
        }

        /** 获取商品ID。
         * @return 商品ID */
        public String getProductId() {
            return this.productId;
        }

        /** 获取商品名称。
         * @return 商品名称 */
        public String getProductName() {
            return this.productName;
        }

        /** 获取数量。
         * @return 数量 */
        public Integer getQuantity() {
            return this.quantity;
        }

        /** 获取单价。
         * @return 单价 */
        public BigDecimal getUnitPrice() {
            return this.unitPrice;
        }

        /** 获取货币类型。
         * @return 货币类型 */
        public String getCurrency() {
            return this.currency;
        }

        /** 设置商品ID。
         * @param productId 商品ID */
        public void setProductId(final String productId) {
            this.productId = productId;
        }

        /** 设置商品名称。
         * @param productName 商品名称 */
        public void setProductName(final String productName) {
            this.productName = productName;
        }

        /** 设置数量。
         * @param quantity 数量 */
        public void setQuantity(final Integer quantity) {
            this.quantity = quantity;
        }

        /** 设置单价。
         * @param unitPrice 单价 */
        public void setUnitPrice(final BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        /** 设置货币类型。
         * @param currency 货币类型 */
        public void setCurrency(final String currency) {
            this.currency = currency;
        }

        /** 判断当前对象与指定对象是否相等。
         * @param o 待比较对象
         * @return 相等返回 {@code true}，否则返回 {@code false} */
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
            if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId))
                return false;
            final java.lang.Object this$productName = this.getProductName();
            final java.lang.Object other$productName = other.getProductName();
            if (this$productName == null ? other$productName != null : !this$productName.equals(other$productName))
                return false;
            final java.lang.Object this$unitPrice = this.getUnitPrice();
            final java.lang.Object other$unitPrice = other.getUnitPrice();
            if (this$unitPrice == null ? other$unitPrice != null : !this$unitPrice.equals(other$unitPrice))
                return false;
            final java.lang.Object this$currency = this.getCurrency();
            final java.lang.Object other$currency = other.getCurrency();
            if (this$currency == null ? other$currency != null : !this$currency.equals(other$currency)) return false;
            return true;
        }

        /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
         * @param other 待判断对象
         * @return 可比较返回 {@code true}，否则返回 {@code false} */
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof CreateOrderCommand.OrderItemCommand;
        }

        /** 返回基于各字段计算的哈希码。
         * @return 哈希码 */
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

        /** 返回对象各字段拼接而成的字符串表示。
         * @return 字符串表示 */
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
        /** 序列化版本UID */
        private static final long serialVersionUID = 1L;
        /** 省份 */
        @Schema(description = "省份", example = "广东省", required = true)
        @NotNull(message = "省份不能为空")
        private String province;
        /** 城市 */
        @Schema(description = "城市", example = "深圳市", required = true)
        @NotNull(message = "城市不能为空")
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

        /** 构造 AddressCommand 对象。 */
        public AddressCommand() {
        }

        /** 获取省份。
         * @return 省份 */
        public String getProvince() {
            return this.province;
        }

        /** 获取城市。
         * @return 城市 */
        public String getCity() {
            return this.city;
        }

        /** 获取区县。
         * @return 区县 */
        public String getDistrict() {
            return this.district;
        }

        /** 获取详细地址。
         * @return 详细地址 */
        public String getDetail() {
            return this.detail;
        }

        /** 获取邮编。
         * @return 邮编 */
        public String getZipCode() {
            return this.zipCode;
        }

        /** 设置省份。
         * @param province 省份 */
        public void setProvince(final String province) {
            this.province = province;
        }

        /** 设置城市。
         * @param city 城市 */
        public void setCity(final String city) {
            this.city = city;
        }

        /** 设置区县。
         * @param district 区县 */
        public void setDistrict(final String district) {
            this.district = district;
        }

        /** 设置详细地址。
         * @param detail 详细地址 */
        public void setDetail(final String detail) {
            this.detail = detail;
        }

        /** 设置邮编。
         * @param zipCode 邮编 */
        public void setZipCode(final String zipCode) {
            this.zipCode = zipCode;
        }

        /** 判断当前对象与指定对象是否相等。
         * @param o 待比较对象
         * @return 相等返回 {@code true}，否则返回 {@code false} */
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

        /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
         * @param other 待判断对象
         * @return 可比较返回 {@code true}，否则返回 {@code false} */
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof CreateOrderCommand.AddressCommand;
        }

        /** 返回基于各字段计算的哈希码。
         * @return 哈希码 */
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

        /** 返回对象各字段拼接而成的字符串表示。
         * @return 字符串表示 */
        @java.lang.Override
        public java.lang.String toString() {
            return "CreateOrderCommand.AddressCommand(province=" + this.getProvince() + ", city=" + this.getCity() + ", district=" + this.getDistrict() + ", detail=" + this.getDetail() + ", zipCode=" + this.getZipCode() + ")";
        }
    }

    /** 构造 CreateOrderCommand 对象。 */
    public CreateOrderCommand() {
    }

    /** 获取用户ID。
     * @return 用户ID */
    public Long getUserId() {
        return this.userId;
    }

    /** 获取收货地址。
     * @return 收货地址 */
    public AddressCommand getShippingAddress() {
        return this.shippingAddress;
    }

    /** 获取备注。
     * @return 备注 */
    public String getRemark() {
        return this.remark;
    }

    /** 获取订单项列表。
     * @return 订单项列表 */
    public List<OrderItemCommand> getItems() {
        return this.items;
    }

    /** 设置用户ID。
     * @param userId 用户ID */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    /** 设置收货地址。
     * @param shippingAddress 收货地址 */
    public void setShippingAddress(final AddressCommand shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /** 设置备注。
     * @param remark 备注 */
    public void setRemark(final String remark) {
        this.remark = remark;
    }

    /** 设置订单项列表。
     * @param items 订单项列表 */
    public void setItems(final List<OrderItemCommand> items) {
        this.items = items;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
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
        if (this$shippingAddress == null ? other$shippingAddress != null : !this$shippingAddress.equals(other$shippingAddress))
            return false;
        final java.lang.Object this$remark = this.getRemark();
        final java.lang.Object other$remark = other.getRemark();
        if (this$remark == null ? other$remark != null : !this$remark.equals(other$remark)) return false;
        final java.lang.Object this$items = this.getItems();
        final java.lang.Object other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
        return true;
    }

    /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
     * @param other 待判断对象
     * @return 可比较返回 {@code true}，否则返回 {@code false} */
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof CreateOrderCommand;
    }

    /** 返回基于各字段计算的哈希码。
     * @return 哈希码 */
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

    /** 返回对象各字段拼接而成的字符串表示。
     * @return 字符串表示 */
    @java.lang.Override
    public java.lang.String toString() {
        return "CreateOrderCommand(userId=" + this.getUserId() + ", shippingAddress=" + this.getShippingAddress() + ", remark=" + this.getRemark() + ", items=" + this.getItems() + ")";
    }
}
