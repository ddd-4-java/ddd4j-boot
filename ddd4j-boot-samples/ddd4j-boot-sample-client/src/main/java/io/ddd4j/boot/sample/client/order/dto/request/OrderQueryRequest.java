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

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "订单状态", example = "PENDING",
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "开始时间（订单创建时间）", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间（订单创建时间）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "最小金额", example = "100.00")
    private BigDecimal minAmount;

    @Schema(description = "最大金额", example = "10000.00")
    private BigDecimal maxAmount;

    @Schema(description = "订单号（支持模糊查询）", example = "ORD2024")
    private String orderNo;

    @Schema(description = "页码（从1开始）", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10", maximum = "100")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "createTime",
            allowableValues = {"createTime", "totalAmount"})
    private String sortField = "createTime";

    @Schema(description = "排序方向", example = "DESC",
            allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public BigDecimal getMinAmount() { return minAmount; }
    public void setMinAmount(BigDecimal minAmount) { this.minAmount = minAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getSortField() { return sortField; }
    public void setSortField(String sortField) { this.sortField = sortField; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    /**
     * 验证查询参数。
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
