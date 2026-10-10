package io.ddd4j.boot.sample.client.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 创建订单请求对象（客户端SDK使用）。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式实现 getter/setter 与全参构造器，
 * 保持 JSON 绑定、校验注解、Swagger 注解与示例语义不变。</p>
 */
@Schema(description = "创建订单请求")
public class CreateOrderRequest implements Serializable {

    /**
     * 构造CreateOrderRequest对象（默认无参构造，字段由调用方逐个设置）。
     */
    public CreateOrderRequest() {
    }

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
    private AddressRequest shippingAddress;

    /** 备注 */
    @Schema(description = "备注", example = "请尽快发货")
    private String remark;

    /** 订单项列表 */
    @Schema(description = "订单项列表", required = true)
    @NotEmpty(message = "订单项不能为空")
    @Valid
    private List<OrderItemRequest> items;

    /** 获取用户ID。
     * @return 用户ID */
    public Long getUserId() { return userId; }
    /** 设置用户ID。
     * @param userId 用户ID */
    public void setUserId(Long userId) { this.userId = userId; }
    /** 获取收货地址。
     * @return 收货地址 */
    public AddressRequest getShippingAddress() { return shippingAddress; }
    /** 设置收货地址。
     * @param shippingAddress 收货地址 */
    public void setShippingAddress(AddressRequest shippingAddress) { this.shippingAddress = shippingAddress; }
    /** 获取备注。
     * @return 备注 */
    public String getRemark() { return remark; }
    /** 设置备注。
     * @param remark 备注 */
    public void setRemark(String remark) { this.remark = remark; }
    /** 获取订单项列表。
     * @return 订单项列表 */
    public List<OrderItemRequest> getItems() { return items; }
    /** 设置订单项列表。
     * @param items 订单项列表 */
    public void setItems(List<OrderItemRequest> items) { this.items = items; }

    /**
     * 订单项请求。
     */
    @Schema(description = "订单项信息")
    public static class OrderItemRequest implements Serializable {

        /**
         * 构造OrderItemRequest对象（默认无参构造，字段由调用方逐个设置）。
         */
        public OrderItemRequest() {
        }

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
        @Schema(description = "货币类型", example = "CNY", defaultValue = "CNY")
        private String currency = "CNY";

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
        /** 获取货币类型。
         * @return 货币类型 */
        public String getCurrency() { return currency; }
        /** 设置货币类型。
         * @param currency 货币类型 */
        public void setCurrency(String currency) { this.currency = currency; }
    }

    /**
     * 地址请求。
     */
    @Schema(description = "地址信息")
    public static class AddressRequest implements Serializable {

        /**
         * 构造AddressRequest对象（默认无参构造，字段由调用方逐个设置）。
         */
        public AddressRequest() {
        }

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
    }
}
