package io.ddd4j.ddd.aggregate;

import lombok.Getter;

import java.time.Instant;

/**
 * Ddd4j 聚合根基类（轻量版，兼容 fuinorg 0.7.0 API）。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-ddd / DddAggregateRoot。
 *
 * <p>由于 fuinorg 0.7.0 版本的 ddd-4-java-core 中 {@code AbstractAggregateRoot} 不再接受
 * 类型参数 ID 的构造器，本类改为提供通用的 createTime / updateTime 审计字段。
 * 业务聚合根应同时实现 fuinorg 的 {@code AggregateRoot} 接口。
 *
 * <p>业务领域事件应继承本类：
 * <pre>{@code
 * public class Order extends DddAggregateRoot {
 *     // 业务字段与行为
 * }
 * }</pre>
 */
@Getter
public abstract class DddAggregateRoot {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间。
     */
    private final Instant createTime;

    /**
     * 最后更新时间。
     */
    private volatile Instant updateTime;

    protected DddAggregateRoot() {
        this.createTime = Instant.now();
        this.updateTime = this.createTime;
    }

    /**
     * 由业务聚合根在应用领域事件后调用，更新最后更新时间。
     */
    protected void touch() {
        this.updateTime = Instant.now();
    }
}
