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
    
    private Long userId;
    private OrderStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private java.math.BigDecimal minAmount;
    private java.math.BigDecimal maxAmount;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    public Long getUserId() {
        return userId;
    }
    
    public OrderQuery setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public OrderQuery setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public OrderQuery setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public OrderQuery setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
    
    public java.math.BigDecimal getMinAmount() {
        return minAmount;
    }
    
    public OrderQuery setMinAmount(java.math.BigDecimal minAmount) {
        this.minAmount = minAmount;
        return this;
    }
    
    public java.math.BigDecimal getMaxAmount() {
        return maxAmount;
    }
    
    public OrderQuery setMaxAmount(java.math.BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }
    
    public Integer getPageNum() {
        return pageNum;
    }
    
    public OrderQuery setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
        return this;
    }
    
    public Integer getPageSize() {
        return pageSize;
    }
    
    public OrderQuery setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }
}

