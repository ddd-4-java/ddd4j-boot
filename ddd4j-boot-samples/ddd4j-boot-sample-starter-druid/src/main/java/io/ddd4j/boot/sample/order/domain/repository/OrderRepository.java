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
     *
     * @param order 订单
     * @return 新增结果
     */
    Order save(Order order);

    /**
     * 根据ID查询订单
     *
     * @param id 标识 ID
     * @return 查询结果
     */
    Optional<Order> findById(Long id);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 查询结果
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户 ID
     * @return 查询结果
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据查询条件查询订单列表（分页）
     *
     * @param query 查询条件
     * @return 查询结果
     */
    List<Order> findByQuery(OrderQuery query);

    /**
     * 根据查询条件统计订单数量
     *
     * @param query 查询条件
     * @return 处理结果
     */
    long countByQuery(OrderQuery query);

    /**
     * 删除订单
     *
     * @param id 标识 ID
     */
    void delete(Long id);
}

