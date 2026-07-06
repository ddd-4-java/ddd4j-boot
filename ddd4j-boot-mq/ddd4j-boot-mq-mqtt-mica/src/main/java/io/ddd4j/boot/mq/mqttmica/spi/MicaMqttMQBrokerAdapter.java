package io.ddd4j.boot.mq.mqttmica.spi;

import io.ddd4j.boot.mq.mqttmica.ack.MicaMqttHeaders;
import io.ddd4j.boot.mq.mqttmica.ack.MicaMqttAcknowledgment;
import io.ddd4j.boot.mq.mqttmica.ack.MicaMqttAcknowledgmentFactory;
import io.ddd4j.boot.mq.mqttmica.consumer.MicaMqttMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.mqttmica.publisher.MicaMqttMQEventPublisher;
import io.ddd4j.mq.consume.ack.Acknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.event.MQEventPublisher;
import io.ddd4j.mq.listener.BrokerType;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;

import java.util.Objects;

/**
 * mica-mqtt Broker 适配器，桥接 ddd4j MQ SPI 与 {@link MqttClientTemplate}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class MicaMqttBrokerAdapter implements BrokerAdapter {

    private final MqttClientTemplate mqttClientTemplate;
    private final MQProperties properties;
    private final int defaultQos;
    private final MicaMqttMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.MQTT_MICA;
    }

    @Override
    public MQEventPublisher createPublisher(MQProperties props) {
        return new MicaMqttMQEventPublisher(mqttClientTemplate, props, defaultQos);
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        // 逻辑块：优先从 mica 原生消息解析 QoS 确认
        MqttPublishMessage micaMessage = message.nativeMessage(MqttPublishMessage.class);
        if (Objects.nonNull(micaMessage)) {
            Object topicHeader = message.getHeaders().get(MicaMqttHeaders.TOPIC);
            String topic = Objects.isNull(topicHeader) ? null : String.valueOf(topicHeader);
            return MicaMqttAcknowledgmentFactory.from(topic, micaMessage, message.getHeaders())
                    .acknowledgment();
        }
        MicaMqttAcknowledgment micaAck = message.nativeMessage(MicaMqttAcknowledgment.class);
        return micaAck;
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.MQTT_MICA == configured;
    }

    /**
     * 返回当前 MQ 配置。
     */
    public MQProperties properties() {
        return properties;
    }
}
