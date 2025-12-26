package io.ddd4j.boot.sample.app.order.query;

import io.ddd4j.boot.sample.domain.order.model.vo.OrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
@ApiModel(description = "订单查询参数")
@Data
public class OrderQuery implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     * 查询指定用户的订单列表
     */
    @ApiModelProperty(value = "用户ID", example = "1001")
    private Long userId;
    
    /**
     * 订单状态
     * 根据订单状态筛选：PENDING-待支付, PAID-已支付, SHIPPED-已发货, DELIVERED-已送达, COMPLETED-已完成, CANCELLED-已取消
     */
    @ApiModelProperty(value = "订单状态", example = "PENDING", allowableValues = "PENDING,PAID,SHIPPED,DELIVERED,COMPLETED,CANCELLED")
    private OrderStatus status;
    
    /**
     * 开始时间
     * 查询创建时间大于等于此时间的订单
     */
    @ApiModelProperty(value = "开始时间（订单创建时间）", example = "2024-01-01T00:00:00")
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     * 查询创建时间小于等于此时间的订单
     */
    @ApiModelProperty(value = "结束时间（订单创建时间）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;
    
    /**
     * 最小金额
     * 查询订单总金额大于等于此金额的订单
     */
    @ApiModelProperty(value = "最小金额", example = "100.00")
    private java.math.BigDecimal minAmount;
    
    /**
     * 最大金额
     * 查询订单总金额小于等于此金额的订单
     */
    @ApiModelProperty(value = "最大金额", example = "10000.00")
    private java.math.BigDecimal maxAmount;
    
    /**
     * 订单号（模糊查询）
     * 支持订单号模糊匹配
     */
    @ApiModelProperty(value = "订单号（支持模糊查询）", example = "ORD2024")
    private String orderNo;
    
    /**
     * 页码
     * 从1开始，默认为1
     */
    @ApiModelProperty(value = "页码（从1开始）", example = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     * 默认每页10条记录，最大不超过100
     */
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize = 10;
    
    /**
     * 排序字段
     * 可选值：createTime（创建时间）、totalAmount（订单金额）
     */
    @ApiModelProperty(value = "排序字段", example = "createTime", allowableValues = "createTime,totalAmount")
    private String sortField = "createTime";
    
    /**
     * 排序方向
     * ASC-升序，DESC-降序，默认为降序
     */
    @ApiModelProperty(value = "排序方向", example = "DESC", allowableValues = "ASC,DESC")
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
}

