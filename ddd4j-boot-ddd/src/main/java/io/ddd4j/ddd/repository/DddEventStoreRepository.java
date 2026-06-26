package io.ddd4j.ddd.repository;

/**
 * Ddd4j 事件存储仓储基类占位符。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-ddd / DddEventStoreRepository。
 *
 * <p>由于 fuinorg 0.7.0 的 ddd-4-java-esc API 在不同小版本间存在不兼容（构造函数签名变化），
 * 本类提供抽象签名供业务方继承。业务方应在具体的 ddd-* 子模块中按需引入合适的 EventStore 实现。
 *
 * @param <A> 聚合根类型
 */
public abstract class DddEventStoreRepository<A> {

    private static final long serialVersionUID = 1L;

    /**
     * 聚合根类型。
     */
    private final Class<A> aggregateType;

    protected DddEventStoreRepository(Class<A> aggregateType) {
        this.aggregateType = aggregateType;
    }

    public final Class<A> getAggregateType() {
        return aggregateType;
    }
}
