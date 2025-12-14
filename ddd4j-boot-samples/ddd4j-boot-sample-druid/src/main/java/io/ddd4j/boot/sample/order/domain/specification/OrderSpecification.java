package io.ddd4j.boot.sample.order.domain.specification;

import io.ddd4j.boot.sample.order.domain.model.aggregate.Order;
import io.ddd4j.boot.sample.order.domain.model.vo.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单规格（Specification Pattern）
 * 用于封装复杂的业务规则查询条件
 */
public class OrderSpecification {
    
    /**
     * 订单是否可以取消
     */
    public static boolean canCancel(Order order) {
        if (order == null) {
            return false;
        }
        OrderStatus status = order.getStatus();
        return status == OrderStatus.PENDING || status == OrderStatus.PAID;
    }
    
    /**
     * 订单是否可以支付
     */
    public static boolean canPay(Order order) {
        return order != null && order.getStatus() == OrderStatus.PENDING;
    }
    
    /**
     * 订单是否可以发货
     */
    public static boolean canShip(Order order) {
        return order != null && order.getStatus() == OrderStatus.PAID;
    }
    
    /**
     * 订单是否可以确认收货
     */
    public static boolean canConfirmDelivery(Order order) {
        return order != null && order.getStatus() == OrderStatus.SHIPPED;
    }
    
    /**
     * 订单是否可以完成
     */
    public static boolean canComplete(Order order) {
        return order != null && order.getStatus() == OrderStatus.DELIVERED;
    }
    
    /**
     * 订单是否在指定时间范围内创建
     */
    public static boolean isCreatedBetween(Order order, LocalDateTime start, LocalDateTime end) {
        if (order == null || order.getCreateTime() == null) {
            return false;
        }
        LocalDateTime createTime = order.getCreateTime();
        return (start == null || !createTime.isBefore(start)) 
            && (end == null || !createTime.isAfter(end));
    }
    
    /**
     * 订单金额是否大于指定金额
     */
    public static boolean isAmountGreaterThan(Order order, java.math.BigDecimal amount) {
        return order != null 
            && order.getTotalAmount() != null 
            && order.getTotalAmount().amount().compareTo(amount) > 0;
    }
}

