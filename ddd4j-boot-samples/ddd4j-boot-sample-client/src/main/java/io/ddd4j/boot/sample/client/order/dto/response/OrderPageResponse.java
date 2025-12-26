package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "订单分页响应")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Schema(description = "订单列表")
    private List<OrderResponse> records;
    
    @Schema(description = "总记录数", example = "100")
    private Long total;
    
    @Schema(description = "当前页码（从1开始）", example = "1")
    private Integer pageNum;
    
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;
    
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;
    
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    @Schema(description = "是否有下一页", example = "true")
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

