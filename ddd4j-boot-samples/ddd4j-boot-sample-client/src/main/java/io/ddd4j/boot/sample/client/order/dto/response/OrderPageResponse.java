package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 订单分页响应对象（客户端SDK使用）
 * 
 * <p>统一的分页响应结构，包含分页信息和数据列表。</p>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
@ApiModel(description = "订单分页响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @ApiModelProperty(value = "订单列表")
    private List<OrderResponse> records;
    
    @ApiModelProperty(value = "总记录数", example = "100")
    private Long total;
    
    @ApiModelProperty(value = "当前页码（从1开始）", example = "1")
    private Integer pageNum;
    
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;
    
    @ApiModelProperty(value = "总页数", example = "10")
    private Integer totalPages;
    
    @ApiModelProperty(value = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    @ApiModelProperty(value = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    /**
     * 创建分页响应对象
     */
    public static OrderPageResponse of(List<OrderResponse> records, Long total, Integer pageNum, Integer pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasPrevious = pageNum > 1;
        boolean hasNext = pageNum < totalPages;
        
        return new OrderPageResponse(records, total, pageNum, pageSize, totalPages, hasPrevious, hasNext);
    }
}

