package io.ddd4j.boot.sample.order.domain.repository;

import io.ddd4j.boot.sample.order.domain.model.vo.OrderStatus;

import java.time.LocalDateTime;

/**
 * 订单查询条件（领域层）
 *
 * <p>用于仓储层的查询条件对象，封装领域层的查询参数。
 * 与应用层的查询对象（application.query.OrderQuery）分离，
 * 保持领域层的独立性。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
public class OrderQuery {
    /**
     * 构造 OrderQuery 实例。
     *
     */
    public OrderQuery() {
    }

    private Long userId;
    private OrderStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private java.math.BigDecimal minAmount;
    private java.math.BigDecimal maxAmount;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

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
     *
     * @return 处理结果
     */
    public OrderQuery setUserId(Long userId) {
        this.userId = userId;
        return this;
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
     *
     * @return 处理结果
     */
    public OrderQuery setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    /**
     * 获取StartTime。
     *
     * @return StartTime
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 设置StartTime。
     *
     * @param startTime StartTime
     *
     * @return 处理结果
     */
    public OrderQuery setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * 获取EndTime。
     *
     * @return EndTime
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * 设置EndTime。
     *
     * @param endTime EndTime
     *
     * @return 处理结果
     */
    public OrderQuery setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    /**
     * 获取MinAmount。
     *
     * @return MinAmount
     */
    public java.math.BigDecimal getMinAmount() {
        return minAmount;
    }

    /**
     * 设置MinAmount。
     *
     * @param minAmount MinAmount
     *
     * @return 处理结果
     */
    public OrderQuery setMinAmount(java.math.BigDecimal minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    /**
     * 获取MaxAmount。
     *
     * @return MaxAmount
     */
    public java.math.BigDecimal getMaxAmount() {
        return maxAmount;
    }

    /**
     * 设置MaxAmount。
     *
     * @param maxAmount MaxAmount
     *
     * @return 处理结果
     */
    public OrderQuery setMaxAmount(java.math.BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    /**
     * 获取PageNum。
     *
     * @return PageNum
     */
    public Integer getPageNum() {
        return pageNum;
    }

    /**
     * 设置PageNum。
     *
     * @param pageNum PageNum
     *
     * @return 处理结果
     */
    public OrderQuery setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }

    /**
     * 获取PageSize。
     *
     * @return PageSize
     */
    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * 设置PageSize。
     *
     * @param pageSize PageSize
     *
     * @return 处理结果
     */
    public OrderQuery setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }
}

