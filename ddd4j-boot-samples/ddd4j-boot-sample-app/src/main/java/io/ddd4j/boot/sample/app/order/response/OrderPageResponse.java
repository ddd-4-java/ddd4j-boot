package io.ddd4j.boot.sample.app.order.response;

import io.ddd4j.boot.sample.app.order.dto.OrderDTO;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "订单分页响应")
public class OrderPageResponse implements Serializable {
    /** 序列化版本UID */
    private static final long serialVersionUID = 1L;
    /**
     * 订单列表数据
     */
    @Schema(description = "订单列表", required = true)
    private List<OrderDTO> records;
    /**
     * 总记录数
     */
    @Schema(description = "总记录数", example = "100", required = true)
    private Long total;
    /**
     * 当前页码
     * 从1开始
     */
    @Schema(description = "当前页码（从1开始）", example = "1", required = true)
    private Integer pageNum;
    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10", required = true)
    private Integer pageSize;
    /**
     * 总页数
     * 根据总记录数和每页大小计算得出
     */
    @Schema(description = "总页数", example = "10", required = true)
    private Integer totalPages;
    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页", example = "false")
    private Boolean hasPrevious;
    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页", example = "true")
    private Boolean hasNext;

    /**
     * 创建分页响应对象
     *
     * @param records  数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @return 分页响应对象
     */
    public static OrderPageResponse of(List<OrderDTO> records, Long total, Integer pageNum, Integer pageSize) {
        int totalPages = (int) Math.ceil((double) total / pageSize);
        boolean hasPrevious = pageNum > 1;
        boolean hasNext = pageNum < totalPages;
        return new OrderPageResponse(records, total, pageNum, pageSize, totalPages, hasPrevious, hasNext);
    }

    /**
     * 订单列表数据
     * @return 订单列表
     */
    public List<OrderDTO> getRecords() {
        return this.records;
    }

    /**
     * 总记录数
     * @return 总记录数
     */
    public Long getTotal() {
        return this.total;
    }

    /**
     * 当前页码
     * 从1开始
     * @return 当前页码（从1开始）
     */
    public Integer getPageNum() {
        return this.pageNum;
    }

    /**
     * 每页大小
     * @return 每页大小
     */
    public Integer getPageSize() {
        return this.pageSize;
    }

    /**
     * 总页数
     * 根据总记录数和每页大小计算得出
     * @return 总页数
     */
    public Integer getTotalPages() {
        return this.totalPages;
    }

    /**
     * 是否有上一页
     * @return 是否有上一页
     */
    public Boolean getHasPrevious() {
        return this.hasPrevious;
    }

    /**
     * 是否有下一页
     * @return 是否有下一页
     */
    public Boolean getHasNext() {
        return this.hasNext;
    }

    /**
     * 订单列表数据
     * @param records 订单列表
     */
    public void setRecords(final List<OrderDTO> records) {
        this.records = records;
    }

    /**
     * 总记录数
     * @param total 总记录数
     */
    public void setTotal(final Long total) {
        this.total = total;
    }

    /**
     * 当前页码
     * 从1开始
     * @param pageNum 当前页码（从1开始）
     */
    public void setPageNum(final Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 每页大小
     * @param pageSize 每页大小
     */
    public void setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 总页数
     * 根据总记录数和每页大小计算得出
     * @param totalPages 总页数
     */
    public void setTotalPages(final Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 是否有上一页
     * @param hasPrevious 是否有上一页
     */
    public void setHasPrevious(final Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    /**
     * 是否有下一页
     * @param hasNext 是否有下一页
     */
    public void setHasNext(final Boolean hasNext) {
        this.hasNext = hasNext;
    }

    /** 判断当前对象与指定对象是否相等。
     * @param o 待比较对象
     * @return 相等返回 {@code true}，否则返回 {@code false} */
    @java.lang.Override
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderPageResponse)) return false;
        final OrderPageResponse other = (OrderPageResponse) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$total = this.getTotal();
        final java.lang.Object other$total = other.getTotal();
        if (this$total == null ? other$total != null : !this$total.equals(other$total)) return false;
        final java.lang.Object this$pageNum = this.getPageNum();
        final java.lang.Object other$pageNum = other.getPageNum();
        if (this$pageNum == null ? other$pageNum != null : !this$pageNum.equals(other$pageNum)) return false;
        final java.lang.Object this$pageSize = this.getPageSize();
        final java.lang.Object other$pageSize = other.getPageSize();
        if (this$pageSize == null ? other$pageSize != null : !this$pageSize.equals(other$pageSize)) return false;
        final java.lang.Object this$totalPages = this.getTotalPages();
        final java.lang.Object other$totalPages = other.getTotalPages();
        if (this$totalPages == null ? other$totalPages != null : !this$totalPages.equals(other$totalPages))
            return false;
        final java.lang.Object this$hasPrevious = this.getHasPrevious();
        final java.lang.Object other$hasPrevious = other.getHasPrevious();
        if (this$hasPrevious == null ? other$hasPrevious != null : !this$hasPrevious.equals(other$hasPrevious))
            return false;
        final java.lang.Object this$hasNext = this.getHasNext();
        final java.lang.Object other$hasNext = other.getHasNext();
        if (this$hasNext == null ? other$hasNext != null : !this$hasNext.equals(other$hasNext)) return false;
        final java.lang.Object this$records = this.getRecords();
        final java.lang.Object other$records = other.getRecords();
        if (this$records == null ? other$records != null : !this$records.equals(other$records)) return false;
        return true;
    }

    /** 判断指定对象是否可与当前对象进行相等比较（供 equals 协作的子类扩展点）。
     * @param other 待判断对象
     * @return 可比较返回 {@code true}，否则返回 {@code false} */
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof OrderPageResponse;
    }

    /** 返回基于各字段计算的哈希码。
     * @return 哈希码 */
    @java.lang.Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $total = this.getTotal();
        result = result * PRIME + ($total == null ? 43 : $total.hashCode());
        final java.lang.Object $pageNum = this.getPageNum();
        result = result * PRIME + ($pageNum == null ? 43 : $pageNum.hashCode());
        final java.lang.Object $pageSize = this.getPageSize();
        result = result * PRIME + ($pageSize == null ? 43 : $pageSize.hashCode());
        final java.lang.Object $totalPages = this.getTotalPages();
        result = result * PRIME + ($totalPages == null ? 43 : $totalPages.hashCode());
        final java.lang.Object $hasPrevious = this.getHasPrevious();
        result = result * PRIME + ($hasPrevious == null ? 43 : $hasPrevious.hashCode());
        final java.lang.Object $hasNext = this.getHasNext();
        result = result * PRIME + ($hasNext == null ? 43 : $hasNext.hashCode());
        final java.lang.Object $records = this.getRecords();
        result = result * PRIME + ($records == null ? 43 : $records.hashCode());
        return result;
    }

    /** 返回对象各字段拼接而成的字符串表示。
     * @return 字符串表示 */
    @java.lang.Override
    public java.lang.String toString() {
        return "OrderPageResponse(records=" + this.getRecords() + ", total=" + this.getTotal() + ", pageNum=" + this.getPageNum() + ", pageSize=" + this.getPageSize() + ", totalPages=" + this.getTotalPages() + ", hasPrevious=" + this.getHasPrevious() + ", hasNext=" + this.getHasNext() + ")";
    }

    /** 构造 OrderPageResponse 对象。 */
    public OrderPageResponse() {
    }

    /**
     * Creates a new {@code OrderPageResponse} instance.
     *
     * @param records     订单列表数据
     * @param total       总记录数
     * @param pageNum     当前页码
     *                    从1开始
     * @param pageSize    每页大小
     * @param totalPages  总页数
     *                    根据总记录数和每页大小计算得出
     * @param hasPrevious 是否有上一页
     * @param hasNext     是否有下一页
     */
    public OrderPageResponse(final List<OrderDTO> records, final Long total, final Integer pageNum, final Integer pageSize, final Integer totalPages, final Boolean hasPrevious, final Boolean hasNext) {
        this.records = records;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.hasPrevious = hasPrevious;
        this.hasNext = hasNext;
    }
}
