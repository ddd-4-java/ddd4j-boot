package io.ddd4j.boot.cmpt.ons.consumer;

import com.aliyun.openservices.ons.api.Producer;
import io.ddd4j.boot.mq.config.Ddd4jMQProperties;
import io.ddd4j.boot.mq.consume.MQConsumerHandler;
import io.ddd4j.boot.mq.registry.MQListenerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @MQEventListener} 注册为 ONS 消费端点的编排器（骨架实现，委托 Rocket 兼容 API）。
 */
@Slf4j
@RequiredArgsConstructor
public class OnsMQConsumerEndpointRegistrar {

    private final Producer producer;
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
        log.info("Queued ONS listener registration: bean={}, method={}, topic={}",
                definition.getBean().getClass().getSimpleName(),
                definition.getMethod().getName(),
                definition.getTopic());
        // TODO: 基于 ONSFactory.createConsumer 创建 PushConsumer 并订阅 topic/tag
        // TODO: MessageListener 内将 ONS Message 转为 MQMessage，通过 OnsMessageAcknowledgmentFactory 解析 ack
        // TODO: 根据 ddd4j.mq.consumer.ack-mode 映射 Action.CommitMessage / ReconsumeLater
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<MQListenerDefinition> definitions, MQConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @MQEventListener definitions found for ONS");
            return;
        }
        for (MQListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("ONS consumer registrar initialized with {} listener(s), ackMode={}, producerReady={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode(), producer != null);
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<MQListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }
}
