package io.ddd4j.boot.cmpt.tdmq.consumer;

import io.ddd4j.boot.cmpt.tdmq.client.TdmqClient;
import io.ddd4j.boot.mq.config.Ddd4jMQProperties;
import io.ddd4j.boot.mq.consume.MQConsumerHandler;
import io.ddd4j.boot.mq.registry.MQListenerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @MQEventListener} 注册为 TDMQ 消费端点的编排器（占位骨架）。
 */
@Slf4j
@RequiredArgsConstructor
public class TdmqMQConsumerEndpointRegistrar {

    private final TdmqClient tdmqClient;
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
        log.info("Queued TDMQ listener registration: bean={}, method={}, topic={}",
                definition.getBean().getClass().getSimpleName(),
                definition.getMethod().getName(),
                definition.getTopic());
        // TODO: 接入 tdmq-client 创建 Consumer 并订阅
        // TODO: 将 TDMQ Message 转为 MQMessage，通过 TdmqMessageAcknowledgmentFactory 解析 ack
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<MQListenerDefinition> definitions, MQConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @MQEventListener definitions found for TDMQ");
            return;
        }
        for (MQListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("TDMQ consumer registrar initialized with {} listener(s), ackMode={}, clientReady={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode(), tdmqClient.isReady());
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<MQListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }
}
