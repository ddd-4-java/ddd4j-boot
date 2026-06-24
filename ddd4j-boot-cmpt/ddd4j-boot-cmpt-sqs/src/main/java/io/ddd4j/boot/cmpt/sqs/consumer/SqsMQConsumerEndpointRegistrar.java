package io.ddd4j.boot.cmpt.sqs.consumer;

import com.amazonaws.services.sqs.AmazonSQS;
import io.ddd4j.boot.mq.config.Ddd4jMQProperties;
import io.ddd4j.boot.mq.consume.MQConsumerHandler;
import io.ddd4j.boot.mq.registry.MQListenerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @MQEventListener} 注册为 SQS 消费端点的编排器（骨架实现）。
 */
@Slf4j
@RequiredArgsConstructor
public class SqsMQConsumerEndpointRegistrar {

    private final AmazonSQS amazonSqs;
    private final String defaultQueueUrl;
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
        log.info("Queued SQS listener registration: bean={}, method={}, topic={}",
                definition.getBean().getClass().getSimpleName(),
                definition.getMethod().getName(),
                definition.getTopic());
        // TODO: 基于 AmazonSQS.receiveMessage 长轮询创建消费循环或接入 spring-cloud-aws-sqs
        // TODO: 将 SQS Message 转为 MQMessage，通过 SqsMessageAcknowledgmentFactory 解析 ack
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<MQListenerDefinition> definitions, MQConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @MQEventListener definitions found for SQS");
            return;
        }
        for (MQListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("SQS consumer registrar initialized with {} listener(s), ackMode={}, queueUrl={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode(), defaultQueueUrl);
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<MQListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }
}
