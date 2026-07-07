package io.ddd4j.boot.mq.pulsar.consumer;

import io.ddd4j.mq.message.Acknowledgment;
import io.ddd4j.mq.consume.ack.NoOpAcknowledgment;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.pulsar.ack.PulsarAcknowledgment;
import io.ddd4j.mq.pulsar.ack.PulsarAcknowledgmentFactory;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.listener.EndpointNaming;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @EventListener} 动态注册为 Pulsar 消费端点。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class PulsarConsumerEndpointRegistrar implements AutoCloseable {

    private final ApplicationContext applicationContext;
    private final MQProperties properties;
    private final List<ListenerDefinition> registeredDefinitions = new CopyOnWriteArrayList<>();
    private final List<Consumer<String>> consumers = new CopyOnWriteArrayList<>();

    /**
     * 注册单个监听器定义。
     *
     * @param definition 监听器定义
     * @param handler    消费处理函数
     */
    public void register(ListenerDefinition definition, ConsumerHandler handler) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");

        PulsarClient pulsarClient = applicationContext.getBean(PulsarClient.class);
        String topic = EndpointNaming.physicalTopic(properties, definition);
        String subscriptionName = definition.getGroup();
        String endpointId = EndpointNaming.endpointId("pulsar", definition);
        String queueName = EndpointNaming.queueName(definition);

        try {
            Consumer<String> consumer = pulsarClient.newConsumer(Schema.STRING)
                    .topic(topic)
                    .subscriptionName(subscriptionName)
                    .subscriptionType(SubscriptionType.Shared)
                    .consumerName(endpointId)
                    .messageListener((c, msg) -> onMessage(c, msg, definition, handler))
                    .subscribe();
            consumers.add(consumer);
            registeredDefinitions.add(definition);

            log.info("Registered Pulsar listener: id={}, topic={}, subscription={}, queueName={}, ackMode={}",
                    endpointId, topic, subscriptionName, queueName, properties.getConsumer().getAckMode());
        } catch (PulsarClientException ex) {
            throw new IllegalStateException(
                    "Failed to start Pulsar consumer for topic=" + topic + ", subscription=" + subscriptionName, ex);
        }
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<ListenerDefinition> definitions, ConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @EventListener definitions found for Pulsar");
            return;
        }
        for (ListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("Pulsar consumer registrar initialized with {} listener(s), ackMode={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode());
    }

    @Override
    public void close() {
        for (Consumer<String> consumer : consumers) {
            try {
                consumer.close();
            } catch (Exception ex) {
                log.warn("Failed to close Pulsar consumer", ex);
            }
        }
        consumers.clear();
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<ListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }

    /**
     * 处理 Pulsar 消息并委托 {@link ConsumerHandler}。
     */
    private void onMessage(
            Consumer<String> consumer,
            Message<String> pulsarMessage,
            ListenerDefinition definition,
            ConsumerHandler handler) {

        try {
            String payloadText = pulsarMessage.getValue();

            // 2.0.x：直接构造纯 Java Message，Pulsar 原生消息通过 nativeMessage 逃生口传入
            Map<String, Object> headers = new HashMap<>();
            headers.put(PulsarAcknowledgment.HEADER_PULSAR_CONSUMER, consumer);
            headers.put(PulsarAcknowledgment.HEADER_PULSAR_MESSAGE, pulsarMessage);

            Message<String> mqMessage = Message.of(
                    payloadText,
                    headers,
                    pulsarMessage.getMessageId().toString(),
                    pulsarMessage.getProperty("correlationId"),
                    pulsarMessage);

            Acknowledgment ack = PulsarAcknowledgmentFactory.from(mqMessage)
                    .map(a -> (Acknowledgment) a)
                    .orElseGet(NoOpAcknowledgment::new);

            handler.handle(mqMessage, ack);
        } catch (Exception ex) {
            log.error("Pulsar consumer failed: bean={}, method={}",
                    definition.getBean().getClass().getSimpleName(),
                    definition.getMethod().getName(),
                    ex);
            if (properties.getConsumer().isManualAck()) {
                consumer.negativeAcknowledge(pulsarMessage);
            }
            throw new RuntimeException(ex);
        }
    }
}
