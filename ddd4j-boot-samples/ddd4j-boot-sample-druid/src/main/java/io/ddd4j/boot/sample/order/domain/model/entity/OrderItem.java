package io.ddd4j.boot.sample.order.domain.model.entity;

import io.ddd4j.boot.core.entity.BaseEntity;
import io.ddd4j.boot.sample.order.domain.model.vo.Money;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单项实体
 */
@Getter
@Setter
public class OrderItem extends BaseEntity<OrderItem> {
    
    private Long id;
    private Long orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private Money unitPrice;
    private Money totalPrice;
    
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

