package io.ddd4j.boot.sample.infrastructure.order.persistence.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单项实体（持久化层）
 *
 * <p>对应 t_order_item 表，一行代表订单中的一件商品明细；
 * 单价、小计以数值列存储，币种单独成列，创建/更新时间由
 * MyBatis-Plus 自动填充。</p>
 */
@TableName("t_order_item")
public class OrderItemEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String currency;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 构造空订单项持久化实体，仅供 MyBatis-Plus 反射映射与查询结果填充使用。
     */
    public OrderItemEntity() {
    }

    /** 获取主键 ID。
     * @return 主键 ID */
    public Long getId() {
        return this.id;
    }

    /** 设置主键 ID。
     * @param id 主键 ID */
    public void setId(final Long id) {
        this.id = id;
    }

    /** 获取所属订单 ID。
     * @return 所属订单 ID */
    public Long getOrderId() {
        return this.orderId;
    }

    /** 设置所属订单 ID。
     * @param orderId 所属订单 ID */
    public void setOrderId(final Long orderId) {
        this.orderId = orderId;
    }

    /** 获取商品 ID。
     * @return 商品 ID */
    public String getProductId() {
        return this.productId;
    }

    /** 设置商品 ID。
     * @param productId 商品 ID */
    public void setProductId(final String productId) {
        this.productId = productId;
    }

    /** 获取商品名称。
     * @return 商品名称 */
    public String getProductName() {
        return this.productName;
    }

    /** 设置商品名称。
     * @param productName 商品名称 */
    public void setProductName(final String productName) {
        this.productName = productName;
    }

    /** 获取数量。
     * @return 数量 */
    public Integer getQuantity() {
        return this.quantity;
    }

    /** 设置数量。
     * @param quantity 数量 */
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    /** 获取单价。
     * @return 单价 */
    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    /** 设置单价。
     * @param unitPrice 单价 */
    public void setUnitPrice(final BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /** 获取小计金额。
     * @return 小计金额 */
    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    /** 设置小计金额。
     * @param totalPrice 小计金额 */
    public void setTotalPrice(final BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /** 获取币种。
     * @return 币种 */
    public String getCurrency() {
        return this.currency;
    }

    /** 设置币种。
     * @param currency 币种 */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** 获取创建时间。
     * @return 创建时间 */
    public LocalDateTime getCreateTime() {
        return this.createTime;
    }

    /** 设置创建时间。
     * @param createTime 创建时间 */
    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /** 获取更新时间。
     * @return 更新时间 */
    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }

    /** 设置更新时间。
     * @param updateTime 更新时间 */
    public void setUpdateTime(final LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}