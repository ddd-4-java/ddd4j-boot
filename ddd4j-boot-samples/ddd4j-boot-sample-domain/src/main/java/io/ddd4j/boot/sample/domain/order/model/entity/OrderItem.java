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
 *
 * <p>业务语义：一条订单项描述「订单中某个商品买了几件、单价多少」，
 * 小计金额在构造与改量时由单价乘数量即时推导，保证金额不被外部直接篡改。</p>
 */
public class OrderItem implements Entity<Long> {

    /** 订单项主键 ID，由持久化层回填。 */
    private Long id;
    /** 所属订单主键 ID，用于归属校验与级联查询。 */
    private Long orderId;
    /** 商品ID，构造时不允许为空串。 */
    private String productId;
    /** 商品名称，下单时刻的快照，不随后续商品改名而变化。 */
    private String productName;
    /** 购买数量，必须为正数。 */
    private Integer quantity;
    /** 商品单价（下单时刻价格）。 */
    private Money unitPrice;
    /** 小计金额，等于单价乘以数量，构造与改量时重算。 */
    private Money totalPrice;

    /**
     * 构造空订单项，仅供持久化框架与查询映射使用。
     *
     * <p>业务创建请使用带参构造器，以便完成必填校验与小计计算。</p>
     */
    public OrderItem() {
    }

    /**
     * 以商品、数量与单价构造订单项，并即时计算小计金额。
     *
     * @param productId   商品ID，不能为空或纯空白
     * @param productName 商品名称（下单时刻快照）
     * @param quantity    购买数量，必须大于 0
     * @param unitPrice   商品单价，不能为空
     * @throws IllegalArgumentException 商品 ID 为空、数量为空或不大于 0、单价为空时抛出
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
     * 以实体接口约定的方式暴露主键。
     *
     * @return 订单项主键 ID
     */
    @Override
    public Long id() {
        return id;
    }

    /** 获取订单项主键 ID。
     * @return 订单项主键 ID */
    public Long getId() {
        return id;
    }

    /** 订单项主键 ID（持久化回填）。
     * @param id 订单项主键 ID（持久化回填） */
    public void setId(Long id) {
        this.id = id;
    }

    /** 获取所属订单主键 ID。
     * @return 所属订单主键 ID */
    public Long getOrderId() {
        return orderId;
    }

    /** 所属订单主键 ID。
     * @param orderId 所属订单主键 ID */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /** 获取商品 ID。
     * @return 商品 ID */
    public String getProductId() {
        return productId;
    }

    /** 商品 ID。
     * @param productId 商品 ID */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /** 获取商品名称快照。
     * @return 商品名称 */
    public String getProductName() {
        return productName;
    }

    /** 商品名称快照。
     * @param productName 商品名称快照 */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /** 获取购买数量。
     * @return 购买数量 */
    public Integer getQuantity() {
        return quantity;
    }

    /** 购买数量（不触发小计重算，业务改量请走 {@link #updateQuantity(Integer)}）。
     * @param quantity 购买数量（不触发小计重算，业务改量请走 {@link #updateQuantity(Integer)}） */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /** 获取商品单价。
     * @return 商品单价 */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /** 商品单价（不触发小计重算）。
     * @param unitPrice 商品单价（不触发小计重算） */
    public void setUnitPrice(Money unitPrice) {
        this.unitPrice = unitPrice;
    }

    /** 获取小计金额。
     * @return 小计金额 */
    public Money getTotalPrice() {
        return totalPrice;
    }

    /** 小计金额（一般由持久化回填，业务改量请走 {@link #updateQuantity(Integer)}）。
     * @param totalPrice 小计金额（一般由持久化回填，业务改量请走 {@link #updateQuantity(Integer)}） */
    public void setTotalPrice(Money totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * 修改购买数量并同步重算小计金额。
     *
     * <p>领域语义：数量是订单项的可变属性，但小计必须由「单价 × 数量」
     * 推导得出，因此这里同时更新两者，避免金额与数量不一致。</p>
     *
     * @param newQuantity 新的购买数量，必须大于 0
     * @throws IllegalArgumentException 数量为空或不大于 0 时抛出
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }
        this.quantity = newQuantity;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(newQuantity));
    }

    /**
     * 依据当前单价与数量重算小计金额（不修改字段值，仅返回结果）。
     *
     * @return 单价乘以数量得到的小计金额
     */
    public Money calculateTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
