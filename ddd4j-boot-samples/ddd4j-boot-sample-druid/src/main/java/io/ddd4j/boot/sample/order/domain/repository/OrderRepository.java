package io.ddd4j.boot.sample.order.domain.repository;

import io.ddd4j.boot.sample.order.domain.model.aggregate.Order;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口（领域层）
 */
public interface OrderRepository {
    
    /**
     * 保存订单
     */
    Order save(Order order);
    
    /**
     * 根据ID查询订单
     */
    Optional<Order> findById(Long id);
    
    /**
     * 根据订单号查询订单
     */
    Optional<Order> findByOrderNo(String orderNo);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> findByUserId(Long userId);
    
    /**
     * 根据查询条件查询订单列表（分页）
     */
    List<Order> findByQuery(OrderQuery query);
    
    /**
     * 根据查询条件统计订单数量
     */
    long countByQuery(OrderQuery query);
    
    /**
     * 删除订单
     */
    void delete(Long id);
}

