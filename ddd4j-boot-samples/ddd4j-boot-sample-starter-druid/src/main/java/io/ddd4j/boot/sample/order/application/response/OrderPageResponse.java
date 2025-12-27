package io.ddd4j.boot.sample.order.application.response;

import io.ddd4j.boot.sample.order.application.dto.OrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 订单分页响应
 * 
 * <p>统一的分页响应结构，包含分页信息和数据列表。
 * 遵循RESTful API设计规范，提供标准的分页响应格式。</p>
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
    
    /**
     * 订单列表数据
     */
    @ApiModelProperty(value = "订单列表", required = true)
    private List<OrderDTO> records;
    
    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数", example = "100", required = true)
    private Long total;
    
    /**
     * 当前页码
     * 从1开始
     */
    @ApiModelProperty(value = "当前页码（从1开始）", example = "1", required = true)
    private Integer pageNum;
    
    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", example = "10", required = true)
    private Integer pageSize;
    
    /**
     * 总页数
     * 根据总记录数和每页大小计算得出
     */
    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer totalPages;
    
    /**
     * 是否有上一页
     */
    @ApiModelProperty(value = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    
    /**
     * 是否有下一页
     */
    @ApiModelProperty(value = "是否有下一页", example = "true")
    private Boolean hasNext;
    
    /**
     * 创建分页响应对象
     * 
     * @param records 数据列表
     * @param total 总记录数
     * @param pageNum 当前页码
     * @param pageSize 每页大小
     * @return 分页响应对象
     */
    public static OrderPageResponse of(List<OrderDTO> records, Long total, Integer pageNum, Integer pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasPrevious = pageNum > 1;
        boolean hasNext = pageNum < totalPages;
        
        return new OrderPageResponse(records, total, pageNum, pageSize, totalPages, hasPrevious, hasNext);
    }
}

