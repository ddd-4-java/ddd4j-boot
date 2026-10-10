package io.ddd4j.boot.sample.domain.order.repository;

import io.ddd4j.boot.sample.domain.order.model.entity.OrderItem;

import java.util.List;

/**
 * 订单项仓储接口（领域层）
 *
 * <p>领域语义：订单项是订单聚合内部的实体，不独立存在，因此本仓储
 * 只暴露「按订单维度」的整组读写，避免订单项被单独创建或悬空引用。
 * 实现位于基础设施层（OrderItemRepositoryImpl）。</p>
 */
public interface OrderItemRepository {

    /**
     * 保存单个订单项（新增或按主键更新）。
     *
     * @param orderItem 待保存的订单项实体
     * @return 保存后的订单项（含持久化回填的主键）
     */
    OrderItem save(OrderItem orderItem);

    /**
     * 批量保存订单项。
     *
     * @param orderItems 待保存的订单项列表
     * @return 保存后的订单项列表（顺序与入参一致，含回填主键）
     */
    List<OrderItem> saveAll(List<OrderItem> orderItems);

    /**
     * 根据订单ID查询该订单下的全部订单项。
     *
     * @param orderId 所属订单主键 ID
     * @return 订单项列表，无记录时返回空列表
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 根据订单ID级联删除该订单的全部订单项。
     *
     * <p>通常在删除订单聚合时由仓储实现联动调用，保证不留下悬空明细。</p>
     *
     * @param orderId 所属订单主键 ID
     */
    void deleteByOrderId(Long orderId);

    /**
     * 删除单个订单项。
     *
     * @param id 订单项主键 ID
     */
    void delete(Long id);
}
