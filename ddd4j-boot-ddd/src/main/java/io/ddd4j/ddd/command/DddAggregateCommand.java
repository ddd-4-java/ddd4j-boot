package io.ddd4j.ddd.command;

import lombok.Getter;

import java.io.Serializable;

/**
 * Ddd4j 聚合命令基类（CQRS 写侧，轻量版，兼容 fuinorg 0.7.0 API）。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-ddd / DddAggregateCommand。
 *
 * <p>注：0.6.0 版本的 fuinorg cqrs-4-java-core 中 {@code AggregateCommand} 接口的 {@code getType()}
 * 返回类型与 0.7.0 略有差异（且 {@code Command.Type} 嵌套类在某些版本中移除），
 * 本实现提供一个自包含的命令基类，保留 aggregateId / aggregateVersion 元数据。
 *
 * <p>业务写操作命令应继承本类：
 * <pre>{@code
 * public class PlaceOrderCommand extends DddAggregateCommand {
 *     public PlaceOrderCommand(String aggregateId, long version) {
 *         super(aggregateId, version);
 *     }
 * }
 * }</pre>
 */
@Getter
public abstract class DddAggregateCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 聚合根 ID。
     */
    private final String aggregateId;

    /**
     * 聚合根版本号。
     */
    private final long aggregateVersion;

    protected DddAggregateCommand() {
        this(null, 0L);
    }

    protected DddAggregateCommand(String aggregateId, long aggregateVersion) {
        this.aggregateId = aggregateId;
        this.aggregateVersion = aggregateVersion;
    }
}
