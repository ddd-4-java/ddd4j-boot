package io.ddd4j.boot.sample.domain.order.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类。
 *
 * <p>订单限界上下文中所有领域事件的公共抽象，负责记录事件发生时间，
 * 供聚合根收集事件、事件发布器投递以及事后审计追踪使用。</p>
 */
public abstract class DomainEvent {

    /**
     * 事件发生时间，由构造时取当前系统时间。
     */
    private final LocalDateTime occurredOn;

    /**
     * 构造领域事件，事件发生时间取当前系统时间。
     */
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取事件发生时间。
     *
     * @return 事件发生时间
     */
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}

