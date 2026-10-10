package io.ddd4j.boot.sample.infrastructure.order.messaging;

import io.ddd4j.boot.sample.domain.order.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 领域事件发布器（基础设施层）
 * 负责将领域事件发布到Spring事件总线
 *
 * <p>领域语义：领域层只负责把事件登记在聚合内部，真正对外投递由本类
 * 在事务边界处完成——将 {@link DomainEvent} 作为 Spring 应用事件发布，
 * 由容器内的监听器（含异步监听器）订阅消费，实现领域层与投递机制解耦。</p>
 */
@Component
public class OrderDomainEventPublisher {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderDomainEventPublisher.class);
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 发布单个领域事件到 Spring 事件总线。
     *
     * @param event 领域事件，为 {@code null} 时忽略不发布
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            log.debug("发布领域事件: {}", event.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(event);
        }
    }

    /**
     * 批量发布领域事件，按列表顺序逐个投递。
     *
     * @param events 领域事件列表，为 {@code null} 或空列表时不执行任何发布
     */
    public void publishAll(List<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            events.forEach(this::publish);
        }
    }

    /**
     * 构造领域事件发布器。
     *
     * @param applicationEventPublisher Spring 应用事件发布器，由容器注入
     */
    public OrderDomainEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
