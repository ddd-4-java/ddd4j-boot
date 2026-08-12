package io.ddd4j.boot.sample.app.order.query;

import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单查询参数
 *
 * <p>用于订单列表查询的查询条件，包含分页参数和业务查询条件。
 * 遵循CQRS模式，将查询参数与命令参数分离。</p>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Schema(description = "订单查询参数")
public class OrderQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户ID
     * 查询指定用户的订单列表
     */
    @Schema(description = "用户ID", example = "1001")
    private Long userId;
    /**
     * 订单状态
     * 根据订单状态筛选：PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    @Schema(description = "订单状态", example = "PENDING", allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private OrderStatus status;
    /**
     * 开始时间
     * 查询创建时间大于等于此时间的订单
     */
    @Schema(description = "开始时间（订单创建时间）", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;
    /**
     * 结束时间
     * 查询创建时间小于等于此时间的订单
     */
    @Schema(description = "结束时间（订单创建时间）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;
    /**
     * 最小金额
     * 查询订单总金额大于等于此金额的订单
     */
    @Schema(description = "最小金额", example = "100.00")
    private java.math.BigDecimal minAmount;
    /**
     * 最大金额
     * 查询订单总金额小于等于此金额的订单
     */
    @Schema(description = "最大金额", example = "10000.00")
    private java.math.BigDecimal maxAmount;
    /**
     * 订单号（模糊查询）
     * 支持订单号模糊匹配
     */
    @Schema(description = "订单号（支持模糊查询）", example = "ORD2024")
    private String orderNo;
    /**
     * 页码
     * 从1开始，默认为1
     */
    @Schema(description = "页码（从1开始）", example = "1", defaultValue = "1")
    private Integer pageNum = 1;
    /**
     * 每页大小
     * 默认每页10条记录，最大不超过100
     */
    @Schema(description = "每页大小", example = "10", defaultValue = "10", maximum = "100")
    private Integer pageSize = 10;
    /**
     * 排序字段
     * 可选值：createTime（创建时间）、totalAmount（订单金额）
     */
    @Schema(description = "排序字段", example = "createTime", allowableValues = {"createTime", "totalAmount"})
    private String sortField = "createTime";
    /**
     * 排序方向
     * ASC-升序，DESC-降序，默认为降序
     */
    @Schema(description = "排序方向", example = "DESC", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";

    /**
     * 验证查询参数
     *
     * @return 验证结果，true表示参数有效
     */
    public boolean isValid() {
        if (pageNum != null && pageNum < 1) {
            return false;
        }
        if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
            return false;
        }
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            return false;
        }
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            return false;
        }
        return true;
    }

    public OrderQuery() {
    }

    /**
     * 用户ID
     * 查询指定用户的订单列表
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * 订单状态
     * 根据订单状态筛选：PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    public OrderStatus getStatus() {
        return this.status;
    }

    /**
     * 开始时间
     * 查询创建时间大于等于此时间的订单
     */
    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    /**
     * 结束时间
     * 查询创建时间小于等于此时间的订单
     */
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * 最小金额
     * 查询订单总金额大于等于此金额的订单
     */
    public java.math.BigDecimal getMinAmount() {
        return this.minAmount;
    }

    /**
     * 最大金额
     * 查询订单总金额小于等于此金额的订单
     */
    public java.math.BigDecimal getMaxAmount() {
        return this.maxAmount;
    }

    /**
     * 订单号（模糊查询）
     * 支持订单号模糊匹配
     */
    public String getOrderNo() {
        return this.orderNo;
    }

    /**
     * 页码
     * 从1开始，默认为1
     */
    public Integer getPageNum() {
        return this.pageNum;
    }

    /**
     * 每页大小
     * 默认每页10条记录，最大不超过100
     */
    public Integer getPageSize() {
        return this.pageSize;
    }

    /**
     * 排序字段
     * 可选值：createTime（创建时间）、totalAmount（订单金额）
     */
    public String getSortField() {
        return this.sortField;
    }

    /**
     * 排序方向
     * ASC-升序，DESC-降序，默认为降序
     */
    public String getSortDirection() {
        return this.sortDirection;
    }

    /**
     * 用户ID
     * 查询指定用户的订单列表
     */
    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    /**
     * 订单状态
     * 根据订单状态筛选：PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    /**
     * 开始时间
     * 查询创建时间大于等于此时间的订单
     */
    public void setStartTime(final LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * 结束时间
     * 查询创建时间小于等于此时间的订单
     */
    public void setEndTime(final LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * 最小金额
     * 查询订单总金额大于等于此金额的订单
     */
    public void setMinAmount(final java.math.BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    /**
     * 最大金额
     * 查询订单总金额小于等于此金额的订单
     */
    public void setMaxAmount(final java.math.BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    /**
     * 订单号（模糊查询）
     * 支持订单号模糊匹配
     */
    public void setOrderNo(final String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * 页码
     * 从1开始，默认为1
     */
    public void setPageNum(final Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 每页大小
     * 默认每页10条记录，最大不超过100
     */
    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 排序字段
     * 可选值：createTime（创建时间）、totalAmount（订单金额）
     */
    public void setSortField(final String sortField) {
        this.sortField = sortField;
    }

    /**
     * 排序方向
     * ASC-升序，DESC-降序，默认为降序
     */
    public void setSortDirection(final String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderQuery)) return false;
        final OrderQuery other = (OrderQuery) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$userId = this.getUserId();
        final java.lang.Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final java.lang.Object this$pageNum = this.getPageNum();
        final java.lang.Object other$pageNum = other.getPageNum();
        if (this$pageNum == null ? other$pageNum != null : !this$pageNum.equals(other$pageNum)) return false;
        final java.lang.Object this$pageSize = this.getPageSize();
        final java.lang.Object other$pageSize = other.getPageSize();
        if (this$pageSize == null ? other$pageSize != null : !this$pageSize.equals(other$pageSize)) return false;
        final java.lang.Object this$status = this.getStatus();
        final java.lang.Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
        final java.lang.Object this$startTime = this.getStartTime();
        final java.lang.Object other$startTime = other.getStartTime();
        if (this$startTime == null ? other$startTime != null : !this$startTime.equals(other$startTime)) return false;
        final java.lang.Object this$endTime = this.getEndTime();
        final java.lang.Object other$endTime = other.getEndTime();
        if (this$endTime == null ? other$endTime != null : !this$endTime.equals(other$endTime)) return false;
        final java.lang.Object this$minAmount = this.getMinAmount();
        final java.lang.Object other$minAmount = other.getMinAmount();
        if (this$minAmount == null ? other$minAmount != null : !this$minAmount.equals(other$minAmount)) return false;
        final java.lang.Object this$maxAmount = this.getMaxAmount();
        final java.lang.Object other$maxAmount = other.getMaxAmount();
        if (this$maxAmount == null ? other$maxAmount != null : !this$maxAmount.equals(other$maxAmount)) return false;
        final java.lang.Object this$orderNo = this.getOrderNo();
        final java.lang.Object other$orderNo = other.getOrderNo();
        if (this$orderNo == null ? other$orderNo != null : !this$orderNo.equals(other$orderNo)) return false;
        final java.lang.Object this$sortField = this.getSortField();
        final java.lang.Object other$sortField = other.getSortField();
        if (this$sortField == null ? other$sortField != null : !this$sortField.equals(other$sortField)) return false;
        final java.lang.Object this$sortDirection = this.getSortDirection();
        final java.lang.Object other$sortDirection = other.getSortDirection();
        if (this$sortDirection == null ? other$sortDirection != null : !this$sortDirection.equals(other$sortDirection)) return false;
        return true;
    }

    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OrderQuery;
    }

    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final java.lang.Object $pageNum = this.getPageNum();
        result = result * PRIME + ($pageNum == null ? 43 : $pageNum.hashCode());
        final java.lang.Object $pageSize = this.getPageSize();
        result = result * PRIME + ($pageSize == null ? 43 : $pageSize.hashCode());
        final java.lang.Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        final java.lang.Object $startTime = this.getStartTime();
        result = result * PRIME + ($startTime == null ? 43 : $startTime.hashCode());
        final java.lang.Object $endTime = this.getEndTime();
        result = result * PRIME + ($endTime == null ? 43 : $endTime.hashCode());
        final java.lang.Object $minAmount = this.getMinAmount();
        result = result * PRIME + ($minAmount == null ? 43 : $minAmount.hashCode());
        final java.lang.Object $maxAmount = this.getMaxAmount();
        result = result * PRIME + ($maxAmount == null ? 43 : $maxAmount.hashCode());
        final java.lang.Object $orderNo = this.getOrderNo();
        result = result * PRIME + ($orderNo == null ? 43 : $orderNo.hashCode());
        final java.lang.Object $sortField = this.getSortField();
        result = result * PRIME + ($sortField == null ? 43 : $sortField.hashCode());
        final java.lang.Object $sortDirection = this.getSortDirection();
        result = result * PRIME + ($sortDirection == null ? 43 : $sortDirection.hashCode());
        return result;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "OrderQuery(userId=" + this.getUserId() + ", status=" + this.getStatus() + ", startTime=" + this.getStartTime() + ", endTime=" + this.getEndTime() + ", minAmount=" + this.getMinAmount() + ", maxAmount=" + this.getMaxAmount() + ", orderNo=" + this.getOrderNo() + ", pageNum=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ", sortField=" + this.getSortField() + ", sortDirection=" + this.getSortDirection() + ")";
    }
}
