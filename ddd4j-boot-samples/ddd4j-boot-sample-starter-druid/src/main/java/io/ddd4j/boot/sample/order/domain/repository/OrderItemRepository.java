package io.ddd4j.boot.sample.order.domain.repository;

import io.ddd4j.boot.sample.order.domain.model.entity.OrderItem;

import java.util.List;

/**
 * 订单项仓储接口（领域层）
 */
public interface OrderItemRepository {

    /**
     * 保存订单项
     *
     * @param orderItem 订单项
     * @return 新增结果
     */
    OrderItem save(OrderItem orderItem);

    /**
     * 批量保存订单项
     *
     * @param orderItems 订单项集合
     * @return 新增结果
     */
    List<OrderItem> saveAll(List<OrderItem> orderItems);

    /**
     * 根据订单ID查询订单项列表
     *
     * @param orderId 订单 ID
     * @return 查询结果
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据订单ID删除订单项
     *
     * @param orderId 订单 ID
     */
    void deleteByOrderId(Long orderId);

    /**
     * 删除订单项
     *
     * @param id 标识 ID
     */
    void delete(Long id);
}

