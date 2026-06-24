package io.ddd4j.boot.cmpt.activemq.consumer;

import io.ddd4j.boot.mq.config.Ddd4jMQProperties;
import io.ddd4j.boot.mq.consume.MQConsumerHandler;
import io.ddd4j.boot.mq.registry.MQListenerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @MQEventListener} 注册为 {@code @JmsListener} 端点的编排器（骨架实现）。
 */
@Slf4j
@RequiredArgsConstructor
public class ActiveMQConsumerEndpointRegistrar {

    private final ApplicationContext applicationContext;
    private final JmsListenerEndpointRegistry endpointRegistry;
    private final Ddd4jMQProperties properties;
    private final List<MQListenerDefinition> registeredDefinitions = new CopyOnWriteArrayList<>();

    /**
     * 注册单个监听器定义。
     *
     * @param definition 监听器定义
     * @param handler    消费处理函数
     */
    public void register(MQListenerDefinition definition, MQConsumerHandler handler) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");
        registeredDefinitions.add(definition);
        log.info("Queued ActiveMQ listener registration: bean={}, method={}, topic={}",
                definition.getBean().getClass().getSimpleName(),
                definition.getMethod().getName(),
                definition.getTopic());
        // TODO: 扫描 @MQEventListener 元数据，动态创建 JmsListenerEndpoint 并注册到 endpointRegistry
        // TODO: 将 JMS Message 转换为 MQMessage，并通过 ActiveMQMessageAcknowledgmentFactory 解析 ack
        // TODO: 根据 ddd4j.mq.consumer.ack-mode 配置手动/自动确认策略
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<MQListenerDefinition> definitions, MQConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @MQEventListener definitions found for ActiveMQ");
            return;
        }
        for (MQListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("ActiveMQ consumer registrar initialized with {} listener(s), ackMode={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode());
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<MQListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }
}
