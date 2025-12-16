package io.ddd4j.boot.cmpt.express.domain.event;

/**
 * 领域事件发布者接口
 * 领域层接口：定义事件发布的抽象
 * 
 * 实现应该在基础设施层或应用层，使用Spring的事件机制或其他消息中间件
 */
public interface DomainEventPublisher {

    /**
     * 发布规则创建事件
     */
    void publishRuleCreated(RuleCreatedEvent event);

    /**
     * 发布规则更新事件
     */
    void publishRuleUpdated(RuleUpdatedEvent event);

    /**
     * 发布规则删除事件
     */
    void publishRuleDeleted(RuleDeletedEvent event);
}

