package io.ddd4j.boot.mq.mqttmica.publisher;

import io.ddd4j.mq.event.MQEvent;
import io.ddd4j.kit.lang.JsonKit;
import io.ddd4j.kit.lang.StrKit;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.message.Destination;
import io.ddd4j.mq.event.MQEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.codec.message.builder.MqttPublishBuilder;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 基于 mica-mqtt {@link MqttClientTemplate} 的领域事件发布实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class MicaMqttMQEventPublisher implements MQEventPublisher {

    private final MqttClientTemplate mqttClientTemplate;
    private final MQProperties mqProperties;
    private final int defaultQos;

    @Override
    public <T extends MQEvent> void publish(T event, Destination destination) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(destination, "destination");

        // 逻辑块：补齐事件元数据
        if (!StrKit.isNotBlank(event.getTopic())) {
            event.setTopic(mqProperties.getDefaultTopic());
        }
        if (!StrKit.isNotBlank(event.getNamespace())) {
            event.setNamespace(mqProperties.getNamespace());
        }
        if (Objects.isNull(event.getMsgId())) {
            event.setMsgId(String.valueOf(System.currentTimeMillis()));
        }

        // 逻辑块：序列化并发布到 MQTT 主题
        String mqttTopic = buildMqttTopic(destination, event.getTag());
        String payload = JsonKit.toJson(event);
        MqttQoS mqttQoS = toMqttQoS(defaultQos);
        mqttClientTemplate.publish(new MqttPublishBuilder()
                .topicName(mqttTopic)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .qos(mqttQoS));

        log.debug("Published mica-mqtt event, topic={}, msgId={}, qos={}", mqttTopic, event.getMsgId(), defaultQos);
    }

    /**
     * 根据目的地与 tag 生成 MQTT 主题（namespace.topic[.tag]）。
     */
    private String buildMqttTopic(Destination destination, String eventTag) {
        String namespace = StrKit.isNotBlank(destination.getNamespace())
                ? destination.getNamespace()
                : mqProperties.getNamespace();
        String topic = StrKit.isNotBlank(destination.getTopic())
                ? destination.getTopic()
                : mqProperties.getDefaultTopic();
        String tag = StrKit.isNotBlank(destination.getTag()) ? destination.getTag() : eventTag;
        String concat = ".";
        String base = StrKit.isNotBlank(namespace) ? namespace + concat + topic : topic;
        if (!StrKit.isNotBlank(tag)) {
            return base;
        }
        return base + concat + tag;
    }

    /**
     * 将整数 QoS 映射为 mica {@link MqttQoS}。
     */
    private MqttQoS toMqttQoS(int qos) {
        return switch (qos) {
            case 2 -> MqttQoS.QOS2;
            case 1 -> MqttQoS.QOS1;
            default -> MqttQoS.QOS0;
        };
    }
}
