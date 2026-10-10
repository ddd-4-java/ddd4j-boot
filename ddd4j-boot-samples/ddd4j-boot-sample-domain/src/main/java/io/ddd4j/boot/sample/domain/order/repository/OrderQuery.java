package io.ddd4j.boot.sample.domain.order.repository;

import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单查询条件（领域层）
 *
 * <p>用于仓储层的查询条件对象，封装领域层的查询参数。
 * 与应用层的查询对象（application.query.OrderQuery）分离，
 * 保持领域层的独立性。</p>
 *
 * <p>领域语义：以「过滤条件 + 分页参数」的值对象形式表达一次订单检索意图，
 * 采用链式（fluent）setter 便于调用方逐项拼装；各过滤字段为 {@code null}
 * 时表示该条件不参与过滤。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
public class OrderQuery {

    /**
     * 构造订单查询条件，字段取默认值。
     */
    public OrderQuery() {
    }


    /** 下单用户 ID 过滤条件，为 {@code null} 时不按用户过滤。 */
    private Long userId;
    /** 订单状态过滤条件，为 {@code null} 时不按状态过滤。 */
    private OrderStatus status;
    /** 创建时间范围起始（含），为 {@code null} 时不校验下界。 */
    private LocalDateTime startTime;
    /** 创建时间范围结束（含），为 {@code null} 时不校验上界。 */
    private LocalDateTime endTime;
    /** 金额下限过滤条件，为 {@code null} 时不校验下限。 */
    private java.math.BigDecimal minAmount;
    /** 金额上限过滤条件，为 {@code null} 时不校验上限。 */
    private java.math.BigDecimal maxAmount;
    /** 页码，从 1 开始，默认 1。 */
    private Integer pageNum = 1;
    /** 每页条数，默认 10。 */
    private Integer pageSize = 10;

    /** 获取下单用户 ID 过滤条件。
     * @return 用户 ID，未设置时为 {@code null} */
    public Long getUserId() {
        return userId;
    }

    /** 设置下单用户 ID 过滤条件。
     * @param userId 用户 ID
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    /** 获取订单状态过滤条件。
     * @return 订单状态，未设置时为 {@code null} */
    public OrderStatus getStatus() {
        return status;
    }

    /** 设置订单状态过滤条件。
     * @param status 订单状态
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    /** 获取创建时间范围起始值。
     * @return 起始时间，未设置时为 {@code null} */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /** 设置创建时间范围起始值（含）。
     * @param startTime 起始时间
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /** 获取创建时间范围结束值。
     * @return 结束时间，未设置时为 {@code null} */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /** 设置创建时间范围结束值（含）。
     * @param endTime 结束时间
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    /** 获取金额下限过滤条件。
     * @return 金额下限，未设置时为 {@code null} */
    public java.math.BigDecimal getMinAmount() {
        return minAmount;
    }

    /** 设置金额下限过滤条件。
     * @param minAmount 金额下限
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setMinAmount(java.math.BigDecimal minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    /** 获取金额上限过滤条件。
     * @return 金额上限，未设置时为 {@code null} */
    public java.math.BigDecimal getMaxAmount() {
        return maxAmount;
    }

    /** 设置金额上限过滤条件。
     * @param maxAmount 金额上限
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setMaxAmount(java.math.BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    /** 获取页码。
     * @return 当前页码（从 1 开始） */
    public Integer getPageNum() {
        return pageNum;
    }

    /** 设置页码。
     * @param pageNum 页码，从 1 开始
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    /** 获取每页条数。
     * @return 每页条数 */
    public Integer getPageSize() {
        return pageSize;
    }

    /** 设置每页条数。
     * @param pageSize 每页条数
     * @return 当前查询条件对象自身，支持链式调用 */
    public OrderQuery setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }
}
