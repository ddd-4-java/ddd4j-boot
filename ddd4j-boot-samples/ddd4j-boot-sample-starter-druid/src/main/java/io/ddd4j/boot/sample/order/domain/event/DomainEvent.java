package io.ddd4j.boot.sample.order.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 */
public abstract class DomainEvent {

    private final LocalDateTime occurredOn;

    /**
     * 构造 DomainEvent 实例。
     *
     */
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取OccurredOn。
     *
     * @return OccurredOn
     */
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}

