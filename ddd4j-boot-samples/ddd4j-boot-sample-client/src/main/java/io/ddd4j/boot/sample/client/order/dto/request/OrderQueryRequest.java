package io.ddd4j.boot.sample.client.order.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单查询请求对象（客户端SDK使用）
 */
@ApiModel(value = "OrderQueryRequest", description = "订单查询请求")
@Data
public class OrderQueryRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "用户ID", example = "1001")
    private Long userId;
    
    @ApiModelProperty(value = "订单状态", example = "PENDING")
    private String status;
    
    @ApiModelProperty(value = "开始时间（订单创建时间）", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;
    
    @ApiModelProperty(value = "结束时间（订单创建时间）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;
    
    @ApiModelProperty(value = "最小金额", example = "100.00")
    private BigDecimal minAmount;
    
    @ApiModelProperty(value = "最大金额", example = "10000.00")
    private BigDecimal maxAmount;
    
    @ApiModelProperty(value = "订单号（支持模糊查询）", example = "ORD2024")
    private String orderNo;
    
    @ApiModelProperty(value = "页码（从1开始）", example = "1")
    private Integer pageNum = 1;
    
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize = 10;
    
    @ApiModelProperty(value = "排序字段", example = "createTime")
    private String sortField = "createTime";
    
    @ApiModelProperty(value = "排序方向", example = "DESC")
    private String sortDirection = "DESC";
    
    /**
     * 验证查询参数
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

