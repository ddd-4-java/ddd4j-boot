package io.ddd4j.boot.sample.domain.order.repository;

import io.ddd4j.boot.sample.domain.order.model.aggregate.Order;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口（领域层）
 *
 * <p>领域语义：聚合根 {@link Order} 的持久化网关。领域层只依赖本接口，
 * 具体的 JPA/MyBatis 实现位于基础设施层（OrderRepositoryImpl），
 * 以此保证聚合的生命周期管理不泄漏存储细节（依赖倒置）。</p>
 *
 * <p>约定：所有查询返回的都是完整聚合，供领域逻辑继续调用行为方法；
 * 查不到时统一以 {@link Optional#empty()} 表达，而不是返回 {@code null}。</p>
 */
public interface OrderRepository {

    /**
     * 保存订单（新增或按主键整体更新）。
     *
     * @param order 待保存的订单聚合根
     * @return 保存后的订单（含持久化回填的主键等标识属性）
     */
    Order save(Order order);

    /**
     * 根据ID查询订单。
     *
     * @param id 订单主键 ID
     * @return 订单聚合根；不存在时返回 {@link Optional#empty()}
     */
    Optional<Order> findById(Long id);

    /**
     * 根据订单号查询订单。
     *
     * @param orderNo 订单编号（业务单号，全局唯一）
     * @return 订单聚合根；不存在时返回 {@link Optional#empty()}
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单列表。
     *
     * @param userId 下单用户 ID
     * @return 该用户的订单列表，无记录时返回空列表
     */
    List<Order> findByUserId(Long userId);

    /**
     * 根据查询条件查询订单列表（分页）。
     *
     * @param query 领域层查询条件对象，含过滤字段与分页参数
     * @return 命中当前页的订单列表，无记录时返回空列表
     */
    List<Order> findByQuery(OrderQuery query);

    /**
     * 根据查询条件统计订单数量。
     *
     * <p>与 {@link #findByQuery(OrderQuery)} 使用同一套过滤条件，
     * 但忽略分页参数，返回满足条件的总记录数，供分页计算总页数使用。</p>
     *
     * @param query 领域层查询条件对象，分页参数不参与统计
     * @return 满足条件的订单总数
     */
    long countByQuery(OrderQuery query);

    /**
     * 删除订单。
     *
     * @param id 订单主键 ID
     */
    void delete(Long id);
}
