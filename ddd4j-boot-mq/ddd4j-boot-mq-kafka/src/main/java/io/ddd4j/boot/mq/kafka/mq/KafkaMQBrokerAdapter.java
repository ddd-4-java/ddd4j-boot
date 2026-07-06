package io.ddd4j.boot.mq.kafka.mq;

import io.ddd4j.mq.consume.Acknowledgment;
import io.ddd4j.mq.consume.NoOpAcknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.listener.BrokerType;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.serialization.EventSerialization;
import io.ddd4j.mq.spi.BrokerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Kafka Broker 适配 SPI 实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
public class KafkaBrokerAdapter implements BrokerAdapter, DisposableBean {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;
    private final EventSerialization serialization;
    private final List<ConcurrentMessageListenerContainer<String, String>> containers = new CopyOnWriteArrayList<>();

    /**
     * 构建 Kafka Broker 适配器。
     *
     * @param kafkaTemplate   Kafka 模板
     * @param consumerFactory 消费者工厂
     * @param serialization   序列化器
     */
    public KafkaBrokerAdapter(
            KafkaTemplate<String, String> kafkaTemplate,
            ConsumerFactory<String, String> consumerFactory,
            EventSerialization serialization) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
        this.serialization = serialization;
    }

    @Override
    public BrokerType brokerType() {
        return BrokerType.KAFKA;
    }

    @Override
    public EventPublisher createPublisher(MQProperties props) {
        Objects.requireNonNull(kafkaTemplate, "KafkaTemplate is required for KafkaEventPublisher");
        Objects.requireNonNull(serialization, "EventSerialization is required for KafkaEventPublisher");
        return new KafkaEventPublisher(kafkaTemplate, serialization, props);
    }

    /**
     * 以编程方式注册 {@link ConcurrentMessageListenerContainer} 消费端点（最小实现）。
     *
     * @param definition 监听器定义
     * @param handler    消费处理器
     */
    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        Objects.requireNonNull(consumerFactory, "ConsumerFactory is required to register Kafka consumer");
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");

        String topic = resolveTopic(definition);
        String groupId = resolveGroupId(definition);

        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setGroupId(groupId);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener((AcknowledgingMessageListener<String, String>) (record, ack) ->
                consumeRecord(definition, handler, record, ack));

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setBeanName("ddd4j-kafka-" + definition.bindingKey());
        container.start();
        containers.add(container);
        log.info("Registered Kafka MQ consumer: topic={}, group={}, bindingKey={}", topic, groupId, definition.bindingKey());
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        if (message == null || message.getHeaders() == null) {
            return new NoOpAcknowledgment();
        }
        Object ackObj = message.getHeaders().get(KafkaAcknowledgment.HEADER_KAFKA_ACK);
        Object recordObj = message.getHeaders().get(KafkaAcknowledgment.HEADER_KAFKA_RECORD);
        if (ackObj instanceof Acknowledgment acknowledgment && recordObj instanceof ConsumerRecord<?, ?> record) {
            return new KafkaAcknowledgment(acknowledgment, record);
        }
        return new NoOpAcknowledgment();
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.KAFKA == configured;
    }

    /**
     * 停止所有已注册的消费容器。
     */
    @Override
    public void destroy() {
        for (ConcurrentMessageListenerContainer<String, String> container : containers) {
            try {
                container.stop();
            } catch (Exception ex) {
                log.warn("Failed to stop Kafka listener container {}", container.getBeanName(), ex);
            }
        }
        containers.clear();
    }

    private void consumeRecord(
            ListenerDefinition definition,
            ConsumerHandler handler,
            ConsumerRecord<String, String> record,
            Acknowledgment ack) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaAcknowledgment.HEADER_KAFKA_ACK, ack);
        headers.put(KafkaAcknowledgment.HEADER_KAFKA_RECORD, record);

        Message<String> message = Message.of(record.value(), headers, null, null);
        KafkaAcknowledgment acknowledgment = new KafkaAcknowledgment(ack, record);

        try {
            handler.handle(message, acknowledgment);
            if (!acknowledgment.isAcknowledged()) {
                acknowledgment.ackSingle();
            }
        } catch (Exception ex) {
            log.error("Kafka MQ consumer failed: topic={}, offset={}", record.topic(), record.offset(), ex);
            if (!acknowledgment.isAcknowledged()) {
                acknowledgment.requeue();
            }
        }
    }

    private String resolveTopic(ListenerDefinition definition) {
        String namespace = definition.getNamespace();
        String concat = StringUtils.hasText(definition.getConcat()) ? definition.getConcat() : "_";
        String topic = StringUtils.hasText(definition.getTopic()) ? definition.getTopic() : "DEFAULT";
        if (StringUtils.hasText(namespace)) {
            return namespace + concat + topic;
        }
        return topic;
    }

    private String resolveGroupId(ListenerDefinition definition) {
        if (StringUtils.hasText(definition.getGroup())) {
            return definition.getGroup();
        }
        return "ddd4j-" + definition.bindingKey();
    }
}
