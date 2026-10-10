package io.ddd4j.boot.sample.order.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.ddd4j.core.ddd.model.Entity;


import java.math.BigDecimal;

/**
 * 订单项实体（持久化层）
 */


@TableName("t_order_item")
public class OrderItemEntity implements Entity<Long> {
    /**
     * 构造 OrderItemEntity 实例。
     *
     */
    public OrderItemEntity() {
    }

    @Override
    public Long id() {
        return id;
    }

    /** id。 */
    @TableId(type = IdType.AUTO)
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
    private BigDecimal unitPrice;

    /** totalPrice。 */
    private BigDecimal totalPrice;

    /** currency。 */
    private String currency;

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
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * 设置UnitPrice。
     *
     * @param unitPrice UnitPrice
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * 获取TotalPrice。
     *
     * @return TotalPrice
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * 设置TotalPrice。
     *
     * @param totalPrice TotalPrice
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 获取Currency。
     *
     * @return Currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * 设置Currency。
     *
     * @param currency Currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
