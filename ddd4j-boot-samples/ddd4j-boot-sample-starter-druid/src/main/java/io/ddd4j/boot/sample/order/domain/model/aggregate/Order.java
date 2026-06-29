package io.ddd4j.boot.sample.order.domain.model.aggregate;

import io.ddd4j.boot.sample.order.domain.event.*;
import io.ddd4j.boot.sample.order.domain.model.entity.OrderItem;
import io.ddd4j.boot.sample.order.domain.model.vo.Address;
import io.ddd4j.boot.sample.order.domain.model.vo.Money;
import io.ddd4j.boot.sample.order.domain.model.vo.OrderStatus;
import io.ddd4j.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 订单聚合根
 */
@Getter
@Setter
public class Order extends BaseEntity<Order> {

    private Long id;
    private String orderNo;
    private Long userId;
    private OrderStatus status;
    private Money totalAmount;
    private Address shippingAddress;
    private String remark;
    private LocalDateTime paidTime;
    private LocalDateTime shippedTime;
    private LocalDateTime deliveredTime;

    private List<OrderItem> items = new ArrayList<>();

    // 领域事件列表（不持久化）
    private transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 创建订单
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
     */
    public void addDomainEvent(DomainEvent event) {
        if (domainEvents == null) {
            domainEvents = new ArrayList<>();
        }
        domainEvents.add(event);
    }

    /**
     * 获取所有领域事件
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
     */
    public void updateShippingAddress(Address newAddress) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待支付状态的订单才能修改收货地址");
        }
        this.shippingAddress = newAddress;
    }
}

