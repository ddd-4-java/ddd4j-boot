package io.ddd4j.ddd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ddd4j DDD 基础配置属性。
 *
 * <p>业务系统可在 application.yml 中通过 {@code ddd4j.ddd.*} 进行配置。
 */
@ConfigurationProperties(prefix = "ddd4j.ddd")
public class DddProperties {

    /**
     * 事件存储类型。
     * <ul>
     *     <li>memory：内存事件存储（默认，dev/test）</li>
     *     <li>kurrentdb：KurrentDB（生产环境）</li>
     * </ul>
     */
    private String eventStoreType = "memory";

    /**
     * 聚合根快照阈值（事件数）。
     */
    private int aggregateSnapshotThreshold = 100;

    public String getEventStoreType() {
        return eventStoreType;
    }

    public void setEventStoreType(String eventStoreType) {
        this.eventStoreType = eventStoreType;
    }

    public int getAggregateSnapshotThreshold() {
        return aggregateSnapshotThreshold;
    }

    public void setAggregateSnapshotThreshold(int aggregateSnapshotThreshold) {
        this.aggregateSnapshotThreshold = aggregateSnapshotThreshold;
    }
}
