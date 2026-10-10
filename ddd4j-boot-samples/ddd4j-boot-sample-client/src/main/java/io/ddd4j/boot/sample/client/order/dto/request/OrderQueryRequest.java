package io.ddd4j.boot.sample.client.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单查询请求对象（客户端SDK使用）。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式实现 getter/setter，
 * 保持 JSON 绑定、校验与示例语义不变。</p>
 */
@Schema(description = "订单查询请求")
public class OrderQueryRequest implements Serializable {

    /**
     * 构造OrderQueryRequest对象（默认无参构造，字段由调用方逐个设置）。
     */
    public OrderQueryRequest() {
    }

    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    /** 订单状态 */
    @Schema(description = "订单状态", example = "PENDING",
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private String status;

    /** 开始时间（订单创建时间） */
    @Schema(description = "开始时间（订单创建时间）", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;

    /** 结束时间（订单创建时间） */
    @Schema(description = "结束时间（订单创建时间）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

    /** 最小金额 */
    @Schema(description = "最小金额", example = "100.00")
    private BigDecimal minAmount;

    /** 最大金额 */
    @Schema(description = "最大金额", example = "10000.00")
    private BigDecimal maxAmount;

    /** 订单号（支持模糊查询） */
    @Schema(description = "订单号（支持模糊查询）", example = "ORD2024")
    private String orderNo;

    /** 页码（从1开始） */
    @Schema(description = "页码（从1开始）", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页大小 */
    @Schema(description = "每页大小", example = "10", defaultValue = "10", maximum = "100")
    private Integer pageSize = 10;

    /** 排序字段 */
    @Schema(description = "排序字段", example = "createTime",
            allowableValues = {"createTime", "totalAmount"})
    private String sortField = "createTime";

    /** 排序方向 */
    @Schema(description = "排序方向", example = "DESC",
            allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";

    /** 获取用户ID。
     * @return 用户ID */
    public Long getUserId() { return userId; }
    /** 设置用户ID。
     * @param userId 用户ID */
    public void setUserId(Long userId) { this.userId = userId; }
    /** 获取订单状态。
     * @return 订单状态 */
    public String getStatus() { return status; }
    /** 设置订单状态。
     * @param status 订单状态 */
    public void setStatus(String status) { this.status = status; }
    /** 获取开始时间（订单创建时间）。
     * @return 开始时间（订单创建时间） */
    public LocalDateTime getStartTime() { return startTime; }
    /** 设置开始时间（订单创建时间）。
     * @param startTime 开始时间（订单创建时间） */
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    /** 获取结束时间（订单创建时间）。
     * @return 结束时间（订单创建时间） */
    public LocalDateTime getEndTime() { return endTime; }
    /** 设置结束时间（订单创建时间）。
     * @param endTime 结束时间（订单创建时间） */
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    /** 获取最小金额。
     * @return 最小金额 */
    public BigDecimal getMinAmount() { return minAmount; }
    /** 设置最小金额。
     * @param minAmount 最小金额 */
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    /** 获取最大金额。
     * @return 最大金额 */
    public BigDecimal getMaxAmount() { return maxAmount; }
    /** 设置最大金额。
     * @param maxAmount 最大金额 */
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    /** 获取订单号（支持模糊查询）。
     * @return 订单号（支持模糊查询） */
    public String getOrderNo() { return orderNo; }
    /** 设置订单号（支持模糊查询）。
     * @param orderNo 订单号（支持模糊查询） */
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    /** 获取页码（从1开始）。
     * @return 页码（从1开始） */
    public Integer getPageNum() { return pageNum; }
    /** 设置页码（从1开始）。
     * @param pageNum 页码（从1开始） */
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    /** 获取每页大小。
     * @return 每页大小 */
    public Integer getPageSize() { return pageSize; }
    /** 设置每页大小。
     * @param pageSize 每页大小 */
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    /** 获取排序字段。
     * @return 排序字段 */
    public String getSortField() { return sortField; }
    /** 设置排序字段。
     * @param sortField 排序字段 */
    public void setSortField(String sortField) { this.sortField = sortField; }
    /** 获取排序方向。
     * @return 排序方向 */
    public String getSortDirection() { return sortDirection; }
    /** 设置排序方向。
     * @param sortDirection 排序方向（ASC/DESC） */
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    /**
     * 验证查询参数。
     *
     * @return 参数有效返回 {@code true}，否则返回 {@code false}
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
}
