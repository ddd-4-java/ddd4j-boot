package io.ddd4j.boot.sample.infrastructure.order.messaging;

import io.ddd4j.boot.sample.domain.order.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 领域事件发布器（基础设施层）
 * 负责将领域事件发布到Spring事件总线
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发布领域事件
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            log.debug("发布领域事件: {}", event.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(event);
        }
    }

    /**
     * 批量发布领域事件
     */
    public void publishAll(List<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            events.forEach(this::publish);
        }
    }
}

