package io.ddd4j.boot.sample.domain.order.model.aggregate;

import io.ddd4j.boot.sample.domain.order.event.*;
import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;
import io.ddd4j.boot.sample.domain.order.model.vo.Address;
import io.ddd4j.boot.sample.domain.order.model.vo.Money;
import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.ddd4j.core.ddd.model.AggregateRoot;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 订单聚合根。
 *
 * <p>订单限界上下文的一致性边界，封装订单的全部业务行为：
 * 创建、加减订单项、支付、取消、发货、确认收货与完成。
 * 所有状态变更都通过聚合根方法完成并校验状态机流转合法性，
 * 变更产生的领域事件先暂存在聚合内部，待事务提交后由基础设施层统一发布。</p>
 *
 * <p>状态流转规则（详见 {@link OrderStatus#canTransitionTo(OrderStatus)}）：
 * 待支付 → 已支付/已取消；已支付 → 已发货/已取消；
 * 已发货 → 已送达；已送达 → 已完成；已完成与已取消为终态。</p>
 */
public class Order extends AggregateRoot<Long> {

    /**
     * 聚合根主键 ID。
     */
    private Long id;

    /**
     * 订单编号（业务单号，全局唯一）。
     */
    private String orderNo;

    /**
     * 下单用户 ID。
     */
    private Long userId;

    /**
     * 订单当前状态。
     */
    private OrderStatus status;

    /**
     * 订单总金额，由订单项实时汇总得出。
     */
    private Money totalAmount;

    /**
     * 收货地址。
     */
    private Address shippingAddress;

    /**
     * 订单备注。
     */
    private String remark;

    /**
     * 支付时间。
     */
    private LocalDateTime paidTime;

    /**
     * 发货时间。
     */
    private LocalDateTime shippedTime;

    /**
     * 送达时间。
     */
    private LocalDateTime deliveredTime;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 订单项列表，属于聚合内部实体。
     */
    private List<OrderItem> items = new ArrayList<>();

    // 领域事件列表（不持久化）
    private transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 构造空订单聚合，仅供持久化框架与 {@link #create} 工厂方法内部使用。
     *
     * <p>业务创建订单请使用 {@link #create(String, Long, Address, List)}
     * 工厂方法，以完成必填校验、状态初始化与创建事件登记。</p>
     */
    public Order() {
    }

    /**
     * 获取聚合根标识。
     *
     * @return 订单主键 ID
     */
    @Override
    public Long id() {
        return id;
    }

    /**
     * 获取订单主键 ID。
     *
     * @return 订单主键 ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置订单主键 ID（通常由持久化回填）。
     *
     * @param id 订单主键 ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取订单编号。
     *
     * @return 订单编号
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置订单编号。
     *
     * @param orderNo 订单编号
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取下单用户 ID。
     *
     * @return 用户 ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置下单用户 ID。
     *
     * @param userId 用户 ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取订单状态。
     *
     * @return 订单状态
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 设置订单状态。
     *
     * @param status 订单状态
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 获取订单总金额。
     *
     * @return 订单总金额
     */
    public Money getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置订单总金额。
     *
     * @param totalAmount 订单总金额
     */
    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取收货地址。
     *
     * @return 收货地址
     */
    public Address getShippingAddress() {
        return shippingAddress;
    }

    /**
     * 设置收货地址。
     *
     * @param shippingAddress 收货地址
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * 获取订单备注。
     *
     * @return 订单备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置订单备注。
     *
     * @param remark 订单备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取支付时间。
     *
     * @return 支付时间
     */
    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    /**
     * 设置支付时间。
     *
     * @param paidTime 支付时间
     */
    public void setPaidTime(LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    /**
     * 获取发货时间。
     *
     * @return 发货时间
     */
    public LocalDateTime getShippedTime() {
        return shippedTime;
    }

    /**
     * 设置发货时间。
     *
     * @param shippedTime 发货时间
     */
    public void setShippedTime(LocalDateTime shippedTime) {
        this.shippedTime = shippedTime;
    }

    /**
     * 获取送达时间。
     *
     * @return 送达时间
     */
    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    /**
     * 设置送达时间。
     *
     * @param deliveredTime 送达时间
     */
    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /**
     * 获取创建时间。
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间。
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取订单项列表。
     *
     * @return 订单项列表
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * 设置订单项列表。
     *
     * @param items 订单项列表
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * 创建订单聚合。
     *
     * <p>工厂方法：校验订单号、用户与订单项非空，初始状态置为
     * {@link OrderStatus#PENDING}，按订单项汇总总金额，并登记
     * {@link OrderCreatedEvent} 领域事件。</p>
     *
     * @param orderNo         订单编号，不能为空
     * @param userId          下单用户 ID，不能为空
     * @param shippingAddress 收货地址，可为 null
     * @param items           订单项列表，不能为空
     * @return 新建的订单聚合实例
     * @throws IllegalArgumentException 订单号为空、用户 ID 为空或订单项为空时抛出
     */
    public static Order create(String orderNo, Long userId, Address shippingAddress, List<OrderItem> items) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("订单号不能为空");
        }
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("订单项不能为空");
        }

        Order order = new Order();
        order.orderNo = orderNo;
        order.userId = userId;
        order.status = OrderStatus.PENDING;
        order.shippingAddress = shippingAddress;
        order.items = new ArrayList<>(items);
        order.calculateTotalAmount();

        // 发布订单创建事件
        order.addDomainEvent(new OrderCreatedEvent(null, orderNo, userId, order.totalAmount.toString()));

        return order;
    }

    /**
     * 登记领域事件。
     *
     * @param event 待登记的领域事件，为 null 时忽略
     */
    public void addDomainEvent(DomainEvent event) {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        domainEvents.add(event);
    }

    /**
     * 获取本次变更累积的全部领域事件（不可变视图）。
     *
     * @return 领域事件列表，未登记事件时为空列表
     */
    public List<DomainEvent> getDomainEvents() {
        return domainEvents != null ? Collections.unmodifiableList(domainEvents) : Collections.emptyList();
    }

    /**
     * 清空已登记的领域事件。
     *
     * <p>通常在事件发布成功后由应用层或基础设施层调用，避免重复发布。</p>
     */
    public void clearDomainEvents() {
        if (domainEvents != null) {
            domainEvents.clear();
        }
    }

    /**
     * 重新计算订单总金额。
     *
     * <p>遍历订单项按行汇总小计，币种固定为 CNY；
     * 订单项为空时总金额记为 0。</p>
     */
    public void calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            this.totalAmount = new Money(BigDecimal.ZERO, "CNY");
            return;
        }

        Money total = items.stream()
                .map(OrderItem::calculateTotal)
                .reduce(new Money(BigDecimal.ZERO, "CNY"), Money::add);

        this.totalAmount = total;
    }

    /**
     * 向订单中添加订单项。
     *
     * <p>仅允许在待支付状态下修改订单构成，添加后重新汇总总金额。</p>
     *
     * @param item 订单项，不能为空
     * @throws IllegalArgumentException 订单项为 null 时抛出
     * @throws IllegalStateException    订单不处于待支付状态时抛出
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("订单项不能为空");
        }
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能添加订单项");
        }
        this.items.add(item);
        calculateTotalAmount();
    }

    /**
     * 按订单项 ID 移除订单项。
     *
     * <p>仅允许在待支付状态下修改订单构成，移除后重新汇总总金额。</p>
     *
     * @param itemId 待移除订单项的 ID
     * @throws IllegalStateException 订单不处于待支付状态时抛出
     */
    public void removeItem(Long itemId) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能移除订单项");
        }
        this.items.removeIf(item -> item.getId() != null && item.getId().equals(itemId));
        calculateTotalAmount();
    }

    /**
     * 支付订单。
     *
     * <p>状态由待支付流转为已支付，记录支付时间，并登记
     * {@link OrderPaidEvent} 领域事件。</p>
     *
     * @param paymentMethod 支付方式编码
     * @throws IllegalStateException 订单不处于待支付状态时抛出
     */
    public void pay(String paymentMethod) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能支付");
        }
        this.status = OrderStatus.PAID;
        this.paidTime = LocalDateTime.now();

        // 发布订单支付事件
        addDomainEvent(new OrderPaidEvent(this.id, this.orderNo, this.userId, paymentMethod));
    }

    /**
     * 取消订单。
     *
     * <p>已完成或已取消的订单为终态不可再取消，已发货后进入履约阶段
     * 同样不允许取消；取消成功后登记 {@link OrderCancelledEvent}。</p>
     *
     * @param reason 取消原因
     * @throws IllegalStateException 订单已完成、已取消或已发货/已送达时抛出
     */
    public void cancel(String reason) {
        if (this.status == OrderStatus.COMPLETED || this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("已完成或已取消的订单不能再次取消");
        }
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("已发货的订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;

        // 发布订单取消事件
        addDomainEvent(new OrderCancelledEvent(this.id, this.orderNo, this.userId, reason));
    }

    /**
     * 订单发货。
     *
     * <p>仅允许已支付订单发货，流转为已发货状态并记录发货时间，
     * 登记 {@link OrderShippedEvent} 领域事件。</p>
     *
     * @param trackingNumber  物流运单号
     * @param logisticsCompany 物流公司
     * @throws IllegalStateException 订单不处于已支付状态时抛出
     */
    public void ship(String trackingNumber, String logisticsCompany) {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付状态的订单才能发货");
        }
        this.status = OrderStatus.SHIPPED;
        this.shippedTime = LocalDateTime.now();

        // 发布订单发货事件
        addDomainEvent(new OrderShippedEvent(this.id, this.orderNo, this.userId, trackingNumber, logisticsCompany));
    }

    /**
     * 确认收货。
     *
     * <p>仅允许已发货订单确认收货，流转为已送达状态并记录送达时间。</p>
     *
     * @throws IllegalStateException 订单不处于已发货状态时抛出
     */
    public void confirmDelivery() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货状态的订单才能确认收货");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredTime = LocalDateTime.now();
    }

    /**
     * 完成订单。
     *
     * <p>仅允许已送达订单完成，流转为已完成终态。</p>
     *
     * @throws IllegalStateException 订单不处于已送达状态时抛出
     */
    public void complete() {
        if (this.status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("只有已送达状态的订单才能完成");
        }
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 更新收货地址。
     *
     * <p>仅允许在待支付状态下修改，进入履约流程后地址不可再变更。</p>
     *
     * @param newAddress 新的收货地址
     * @throws IllegalStateException 订单不处于待支付状态时抛出
     */
    public void updateShippingAddress(Address newAddress) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能修改收货地址");
        }
        this.shippingAddress = newAddress;
    }
}

