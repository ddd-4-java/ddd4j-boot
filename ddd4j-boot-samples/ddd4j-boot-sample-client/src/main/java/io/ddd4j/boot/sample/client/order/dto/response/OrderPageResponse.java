package io.ddd4j.boot.sample.client.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

/**
 * 订单分页响应对象（客户端SDK使用）。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式实现 getter/setter 与全参构造器，
 * 保持 JSON 绑定、Swagger 注解与示例语义不变。</p>
 */
@Schema(description = "订单分页响应")
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

    public OrderPageResponse() {
    }

    public OrderPageResponse(List<OrderResponse> records, Long total, Integer pageNum,
                             Integer pageSize, Integer totalPages,
                             Boolean hasPrevious, Boolean hasNext) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }

    /**
     * 创建分页响应对象。
     */
    public static OrderPageResponse of(List<OrderResponse> records, Long total, Integer pageNum, Integer pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasPrevious = pageNum > 1;
        boolean hasNext = pageNum < totalPages;

        return new OrderPageResponse(records, total, pageNum, pageSize, totalPages, hasPrevious, hasNext);
    }

    public List<OrderResponse> getRecords() { return records; }
    public void setRecords(List<OrderResponse> records) { this.records = records; }
    public Long getTotal() { return total; }
    public void setTotal(Long total) { this.total = total; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    public Boolean getHasPrevious() { return hasPrevious; }
    public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    public Boolean getHasNext() { return hasNext; }
    public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }
}
