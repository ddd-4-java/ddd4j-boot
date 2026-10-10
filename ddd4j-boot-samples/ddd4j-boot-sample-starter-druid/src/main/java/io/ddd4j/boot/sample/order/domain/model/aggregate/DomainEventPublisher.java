package io.ddd4j.boot.sample.order.domain.model.aggregate;

import io.ddd4j.boot.sample.order.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 领域事件发布器（聚合根内部使用）
 */
public class DomainEventPublisher {
    /**
     * 构造 DomainEventPublisher 实例。
     *
     */
    public DomainEventPublisher() {
    }

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            domainEvents.add(event);
        }
    }

    /**
     * 获取所有领域事件
     *
     * @return 查询结果
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    /**
     * 清空领域事件
     */
    public void clear() {
        domainEvents.clear();
    }
}

