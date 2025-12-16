package io.ddd4j.boot.cmpt.express.infrastructure.event;

import io.ddd4j.boot.cmpt.express.domain.event.DomainEventPublisher;
import io.ddd4j.boot.cmpt.express.domain.event.RuleCreatedEvent;
import io.ddd4j.boot.cmpt.express.domain.event.RuleDeletedEvent;
import io.ddd4j.boot.cmpt.express.domain.event.RuleUpdatedEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Spring领域事件发布者实现
 * 基础设施层：使用Spring的ApplicationEventPublisher实现事件发布
 * 
 * 注意：此类是可选的，只有在使用Spring事件机制时才需要
 */
@Component
@ConditionalOnClass(name = "org.springframework.context.ApplicationEventPublisher")
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void publishRuleCreated(RuleCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishRuleUpdated(RuleUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publishRuleDeleted(RuleDeletedEvent event) {
        eventPublisher.publishEvent(event);
    }
}

