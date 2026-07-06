package io.ddd4j.boot.mq.mqtt.spi;

import io.ddd4j.boot.mq.mqtt.ack.MqttAcknowledgment;
import io.ddd4j.boot.mq.mqtt.ack.MqttAcknowledgmentFactory;
import io.ddd4j.boot.mq.mqtt.consumer.MqttMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.mqtt.publisher.MqttEventPublisher;
import io.ddd4j.mq.consume.Acknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.listener.BrokerType;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;

import java.util.Objects;

/**
 * MQTT Broker 适配器，桥接 ddd4j MQ SPI 与 Spring Integration MQTT（Eclipse Paho）。
 * <p>2.0.x 重构：基于纯 Java {@link Message}，不再依赖 {@code org.springframework.messaging.Message}。
 * <p>注：{@link MessageChannel} 是 Spring Integration 的 outbound channel，
 * 这是 Spring Integration 客户端设计约束。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class MqttBrokerAdapter implements BrokerAdapter {

    private final MessageChannel mqttOutboundChannel;
    private final MQProperties properties;
    private final int defaultQos;
    private final MqttMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.MQTT;
    }

    @Override
    public EventPublisher createPublisher(MQProperties props) {
        return new MqttEventPublisher(mqttOutboundChannel, props, defaultQos);
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        // 2.0.x：直接基于纯 Java Message 解析（MQTT QoS 通过 nativeMessage 逃生口传入）
        MqttAcknowledgment mqttAck = message.nativeMessage(MqttAcknowledgment.class);
        if (Objects.nonNull(mqttAck)) {
            return mqttAck;
        }
        return MqttAcknowledgmentFactory.resolve(message).acknowledgment();
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.MQTT == configured;
    }

    /**
     * 返回当前 MQ 配置。
     */
    public MQProperties properties() {
        return properties;
    }
}
