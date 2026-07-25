package io.ddd4j.boot.sample.domain.order.model.entity;

import io.ddd4j.boot.sample.domain.order.model.vo.Money;
import io.ddd4j.core.ddd.model.Entity;

import java.math.BigDecimal;

/**
 * 订单项实体。
 *
 * <p>ddd4j 2.0.x 中实体基类已收敛为 {@link Entity} 接口，不存在
 * 旧的 {@code io.ddd4j.core.entity.BaseEntity}。本类显式实现该接口并提供
 * 显式 getter/setter，避免依赖未启用的 Lombok 注解处理器。</p>
 */
public class OrderItem implements Entity<Long> {

    private Long id;
    private Long orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Money unitPrice;
    private Money totalPrice;

    public OrderItem() {
    }

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

    @Override
    public Long id() {
        return id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

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

    public Money getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Money getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        this.quantity = newQuantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
    }

    public Money calculateTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
