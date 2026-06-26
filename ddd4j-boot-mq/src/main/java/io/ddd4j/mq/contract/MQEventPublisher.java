package io.ddd4j.mq.contract;

import org.springframework.context.ApplicationEventPublisher;

/**
 * MQ 事件发布器 Port 接口。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-mq Port 层。具体 broker（Kafka / RocketMQ / RabbitMQ 等）
 * 由 ddd4j-boot-extensions 下的 mq-* 子模块提供实现。
 *
 * <p>业务系统应通过该接口发布事件，而非直接耦合到具体 broker。
 */
public interface MQEventPublisher {

    /**
     * 发布 MQ 事件。
     *
     * @param topic   主题/队列
     * @param event   事件内容（任意可序列化对象）
     * @param <T>     事件类型
     */
    <T> void publish(String topic, T event);

    /**
     * Spring 事件发布器（用于桥接 ApplicationEvent -&gt; MQ）。
     */
    ApplicationEventPublisher getApplicationEventPublisher();
}
