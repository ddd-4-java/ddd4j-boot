package io.ddd4j.boot.sample.order.domain.model.aggregate;

import io.ddd4j.boot.sample.order.domain.event.*;
import io.ddd4j.boot.sample.order.domain.model.entity.OrderItem;
import io.ddd4j.boot.sample.order.domain.model.vo.Address;
import io.ddd4j.boot.sample.order.domain.model.vo.Money;
import io.ddd4j.boot.sample.order.domain.model.vo.OrderStatus;
import io.ddd4j.core.ddd.model.Entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 订单聚合根
 */


public class Order implements Entity<Long> {
    /**
     * 构造 Order 实例。
     *
     */
    public Order() {
    }

    @Override
    public Long id() {
        return id;
    }

    /** id。 */
    private Long id;
    /** orderNo。 */
    private String orderNo;
    /** userId。 */
    private Long userId;
    /** status。 */
    private OrderStatus status;
    /** totalAmount。 */
    private Money totalAmount;
    /** shippingAddress。 */
    private Address shippingAddress;
    /** remark。 */
    private String remark;
    /** paidTime。 */
    private LocalDateTime paidTime;
    /** shippedTime。 */
    private LocalDateTime shippedTime;
    /** deliveredTime。 */
    private LocalDateTime deliveredTime;
    /** createTime。 */
    private LocalDateTime createTime;
    /** updateTime。 */
    private LocalDateTime updateTime;

    /** 订单项列表。 */
    private List<OrderItem> items = new ArrayList<>();

    // 领域事件列表（不持久化）
    private transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 创建订单
     *
     * @param orderNo 订单号
     * @param userId 用户 ID
     * @param shippingAddress 收货地址
     * @param items 条目集合
     * @return 新增结果
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
     * 添加领域事件
     *
     * @param event 领域事件
     */
    public void addDomainEvent(DomainEvent event) {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        domainEvents.add(event);
    }

    /**
     * 获取所有领域事件
     *
     * @return 查询结果
     */
    public List<DomainEvent> getDomainEvents() {
        return domainEvents != null ? Collections.unmodifiableList(domainEvents) : Collections.emptyList();
    }

    /**
     * 清空领域事件
     */
    public void clearDomainEvents() {
        if (domainEvents != null) {
            domainEvents.clear();
        }
    }

    /**
     * 计算订单总金额
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
     * 添加订单项
     *
     * @param item 条目
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
     * 移除订单项
     *
     * @param itemId itemId
     */
    public void removeItem(Long itemId) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能移除订单项");
        }
        this.items.removeIf(item -> item.getId() != null && item.getId().equals(itemId));
        calculateTotalAmount();
    }

    /**
     * 支付订单
     *
     * @param paymentMethod 支付方式
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
     * 取消订单
     *
     * @param reason 原因
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
     * 发货
     *
     * @param trackingNumber 物流单号
     * @param logisticsCompany 物流公司
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
     * 确认收货
     */
    public void confirmDelivery() {
        if (this.status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("只有已发货状态的订单才能确认收货");
        }
        this.status = OrderStatus.DELIVERED;
        this.deliveredTime = LocalDateTime.now();
    }

    /**
     * 完成订单
     */
    public void complete() {
        if (this.status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("只有已送达状态的订单才能完成");
        }
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 更新收货地址
     *
     * @param newAddress 新地址
     */
    public void updateShippingAddress(Address newAddress) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能修改收货地址");
        }
        this.shippingAddress = newAddress;
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
     * 获取OrderNo。
     *
     * @return OrderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 设置OrderNo。
     *
     * @param orderNo OrderNo
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 获取UserId。
     *
     * @return UserId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置UserId。
     *
     * @param userId UserId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取Status。
     *
     * @return Status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 设置Status。
     *
     * @param status Status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 获取TotalAmount。
     *
     * @return TotalAmount
     */
    public Money getTotalAmount() {
        return totalAmount;
    }

    /**
     * 设置TotalAmount。
     *
     * @param totalAmount TotalAmount
     */
    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取ShippingAddress。
     *
     * @return ShippingAddress
     */
    public Address getShippingAddress() {
        return shippingAddress;
    }

    /**
     * 设置ShippingAddress。
     *
     * @param shippingAddress ShippingAddress
     */
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /**
     * 获取Remark。
     *
     * @return Remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置Remark。
     *
     * @param remark Remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取PaidTime。
     *
     * @return PaidTime
     */
    public LocalDateTime getPaidTime() {
        return paidTime;
    }

    /**
     * 设置PaidTime。
     *
     * @param paidTime PaidTime
     */
    public void setPaidTime(LocalDateTime paidTime) {
        this.paidTime = paidTime;
    }

    /**
     * 获取ShippedTime。
     *
     * @return ShippedTime
     */
    public LocalDateTime getShippedTime() {
        return shippedTime;
    }

    /**
     * 设置ShippedTime。
     *
     * @param shippedTime ShippedTime
     */
    public void setShippedTime(LocalDateTime shippedTime) {
        this.shippedTime = shippedTime;
    }

    /**
     * 获取DeliveredTime。
     *
     * @return DeliveredTime
     */
    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    /**
     * 设置DeliveredTime。
     *
     * @param deliveredTime DeliveredTime
     */
    public void setDeliveredTime(LocalDateTime deliveredTime) {
        this.deliveredTime = deliveredTime;
    }

    /**
     * 获取CreateTime。
     *
     * @return CreateTime
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置CreateTime。
     *
     * @param createTime CreateTime
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取UpdateTime。
     *
     * @return UpdateTime
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置UpdateTime。
     *
     * @param updateTime UpdateTime
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取Items。
     *
     * @return Items
     */
    public List<OrderItem> getItems() {
        return items;
    }

    /**
     * 设置Items。
     *
     * @param items Items
     */
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}

