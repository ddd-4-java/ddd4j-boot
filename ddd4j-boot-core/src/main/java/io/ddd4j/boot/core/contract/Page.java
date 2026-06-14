package io.ddd4j.boot.core.contract;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 框架无关的分页结果（不依赖 MyBatis Plus 的 IPage）。
 *
 * <p>这是 ddd4j-boot 纯净 DDD 轨道的分页容器。与 MyBatis Plus 的
 * {@code com.baomidou.mybatisplus.extension.plugins.pagination.Page} 完全独立。
 *
 * <p>使用方式：
 * <pre>
 * Page&lt;User&gt; page = Page.of(userList, 100L, 1, 10);
 * page.getRecords();   // 当前页数据
 * page.getTotal();     // 总记录数
 * page.getPages();     // 总页数
 * </pre>
 *
 * @param <T> 记录类型
 * @author wandl
 * @since 3.4.x
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页记录列表 */
    private final List<T> records;

    /** 总记录数 */
    private final long total;

    /** 当前页码（从 1 开始） */
    private final long current;

    /** 每页大小 */
    private final long size;

    /**
     * 构造分页结果。
     *
     * @param records 当前页记录
     * @param total   总记录数
     * @param current 当前页码（从 1 开始）
     * @param size    每页大小
     */
    public Page(List<T> records, long total, long current, long size) {
        this.records = records == null ? Collections.emptyList() : records;
        this.total = total;
        this.current = current;
        this.size = size;
    }

    /**
     * 工厂方法。
     *
     * @param records 当前页记录
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     记录类型
     * @return 分页结果
     */
    public static <T> Page<T> of(List<T> records, long total, long current, long size) {
        return new Page<>(records, total, current, size);
    }

    /**
     * 空页工厂方法。
     *
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     记录类型
     * @return 空的分页结果
     */
    public static <T> Page<T> empty(long current, long size) {
        return new Page<>(Collections.emptyList(), 0L, current, size);
    }

    public List<T> getRecords() {
        return records;
    }

    public long getTotal() {
        return total;
    }

    public long getCurrent() {
        return current;
    }

    public long getSize() {
        return size;
    }

    /**
     * 计算总页数。
     *
     * @return 总页数（size 为 0 时返回 0）
     */
    public long getPages() {
        return size <= 0 ? 0 : (total + size - 1) / size;
    }

    /**
     * 是否有下一页。
     *
     * @return 有下一页返回 true
     */
    public boolean hasNext() {
        return current < getPages();
    }

    /**
     * 是否有上一页。
     *
     * @return 有上一页返回 true
     */
    public boolean hasPrevious() {
        return current > 1;
    }

}
