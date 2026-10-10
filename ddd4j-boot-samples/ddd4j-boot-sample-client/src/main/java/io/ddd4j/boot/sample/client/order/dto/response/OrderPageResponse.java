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

    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;

    /** 订单列表 */
    @Schema(description = "订单列表")
    private List<OrderResponse> records;

    /** 总记录数 */
    @Schema(description = "总记录数", example = "100")
    private Long total;

    /** 当前页码（从1开始） */
    @Schema(description = "当前页码（从1开始）", example = "1")
    private Integer pageNum;

    /** 每页大小 */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;

    /** 总页数 */
    @Schema(description = "总页数", example = "10")
    private Integer totalPages;

    /** 是否有上一页 */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    /** 是否有下一页 */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    /** 构造 OrderPageResponse 对象。 */
    public OrderPageResponse() {
    }

    /**
     * 构造订单分页响应对象（全参构造）。
     *
     * @param records    订单列表
     * @param total      总记录数
     * @param pageNum    当前页码（从1开始）
     * @param pageSize   每页大小
     * @param totalPages 总页数
     * @param hasPrevious 是否有上一页
     * @param hasNext    是否有下一页
     */
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
     *
     * @param records  订单列表
     * @param total    总记录数
     * @param pageNum  当前页码（从1开始）
     * @param pageSize 每页大小
     * @return 分页响应对象（总页数与上下页标志自动计算）
     */
    public static OrderPageResponse of(List<OrderResponse> records, Long total, Integer pageNum, Integer pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasPrevious = pageNum > 1;
        boolean hasNext = pageNum < totalPages;

        return new OrderPageResponse(records, total, pageNum, pageSize, totalPages, hasPrevious, hasNext);
    }

    /** 获取订单列表。
     * @return 订单列表 */
    public List<OrderResponse> getRecords() { return records; }
    /** 设置订单列表。
     * @param records 订单列表 */
    public void setRecords(List<OrderResponse> records) { this.records = records; }
    /** 获取总记录数。
     * @return 总记录数 */
    public Long getTotal() { return total; }
    /** 设置总记录数。
     * @param total 总记录数 */
    public void setTotal(Long total) { this.total = total; }
    /** 获取当前页码（从1开始）。
     * @return 当前页码（从1开始） */
    public Integer getPageNum() { return pageNum; }
    /** 设置当前页码（从1开始）。
     * @param pageNum 当前页码（从1开始） */
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    /** 获取每页大小。
     * @return 每页大小 */
    public Integer getPageSize() { return pageSize; }
    /** 设置每页大小。
     * @param pageSize 每页大小 */
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    /** 获取总页数。
     * @return 总页数 */
    public Integer getTotalPages() { return totalPages; }
    /** 设置总页数。
     * @param totalPages 总页数 */
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
    /** 获取是否有上一页。
     * @return 是否有上一页 */
    public Boolean getHasPrevious() { return hasPrevious; }
    /** 设置是否有上一页。
     * @param hasPrevious 是否有上一页 */
    public void setHasPrevious(Boolean hasPrevious) { this.hasPrevious = hasPrevious; }
    /** 获取是否有下一页。
     * @return 是否有下一页 */
    public Boolean getHasNext() { return hasNext; }
    /** 设置是否有下一页。
     * @param hasNext 是否有下一页 */
    public void setHasNext(Boolean hasNext) { this.hasNext = hasNext; }
}
