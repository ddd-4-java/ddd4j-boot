package io.ddd4j.boot.sample.domain.order.repository;

import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;

import java.util.List;

/**
 * 订单项仓储接口（领域层）
 */
public interface OrderItemRepository {
    
    /**
     * 保存订单项
     */
    OrderItem save(OrderItem orderItem);
    
    /**
     * 批量保存订单项
     */
    List<OrderItem> saveAll(List<OrderItem> orderItems);
    
    /**
     * 根据订单ID查询订单项列表
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 根据订单ID删除订单项
     */
    void deleteByOrderId(Long orderId);
    
    /**
     * 删除订单项
     */
    void delete(Long id);
}

