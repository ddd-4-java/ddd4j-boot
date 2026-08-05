package io.ddd4j.boot.sample.order.application.command;

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


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
        }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AddressCommand getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(AddressCommand shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderItemCommand> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCommand> items) {
        this.items = items;
    }
}
