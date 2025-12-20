package io.ddd4j.boot.sample.order.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件基类
 */
public abstract class DomainEvent {
    
    private final LocalDateTime occurredOn;
    
    protected DomainEvent() {
        this.occurredOn = LocalDateTime.now();
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
}

