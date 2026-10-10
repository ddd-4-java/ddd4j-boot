package io.ddd4j.boot.sample.order.domain.model.entity;

import io.ddd4j.boot.sample.order.domain.model.vo.Money;
import io.ddd4j.core.ddd.model.Entity;



import java.math.BigDecimal;

/**
 * 订单项实体
 */


public class OrderItem implements Entity<Long> {

    @Override
    public Long id() {
        return id;
    }

    /** id。 */
    private Long id;
    /** orderId。 */
    private Long orderId;
    /** productId。 */
    private String productId;
    /** productName。 */
    private String productName;
    /** quantity。 */
    private Integer quantity;
    /** unitPrice。 */
    private Money unitPrice;
    /** totalPrice。 */
    private Money totalPrice;

    /**
     * 构造 OrderItem 实例。
     *
     * @param productId 商品 ID
     * @param productName 商品名称
     * @param quantity 数量
     * @param unitPrice 单价
     *
     */
    public OrderItem(String productId, String productName, Integer quantity, Money unitPrice) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("单价不能为空");
        }
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 执行 updateQuantity 操作。
     *
     * @param newQuantity 新数量
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        this.quantity = newQuantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
    }

    /**
     * 执行 calculateTotal 操作。
     *
     * @return 计算结果
     */
    public Money calculateTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }


    /**
     * 获取Id。
     *
     * @return Id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置Id。
     *
     * @param id Id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取OrderId。
     *
     * @return OrderId
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * 设置OrderId。
     *
     * @param orderId OrderId
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取ProductId。
     *
     * @return ProductId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * 设置ProductId。
     *
     * @param productId ProductId
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * 获取ProductName。
     *
     * @return ProductName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 设置ProductName。
     *
     * @param productName ProductName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * 获取Quantity。
     *
     * @return Quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * 设置Quantity。
     *
     * @param quantity Quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取UnitPrice。
     *
     * @return UnitPrice
     */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /**
     * 设置UnitPrice。
     *
     * @param unitPrice UnitPrice
     */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 获取TotalPrice。
     *
     * @return TotalPrice
     */
    public Money getTotalPrice() {
        return totalPrice;
    }

    /**
     * 设置TotalPrice。
     *
     * @param totalPrice TotalPrice
     */
    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }
}

