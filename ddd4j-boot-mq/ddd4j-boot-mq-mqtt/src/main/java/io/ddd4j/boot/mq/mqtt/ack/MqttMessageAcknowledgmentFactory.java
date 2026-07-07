package io.ddd4j.boot.mq.mqtt.ack;

import io.ddd4j.mq.consume.ack.NoOpAcknowledgment;
import io.ddd4j.mq.message.Acknowledgment;
import io.ddd4j.mq.message.Message;
import org.springframework.integration.mqtt.support.MqttHeaders;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 从纯 Java {@link Message} 头信息构建 {@link MqttAcknowledgment}。
 *
 * <p>2.0.x 重构：彻底移除对 {@code org.springframework.messaging.Message} 的依赖，
 * 直接基于 ddd4j-mq-core 的纯 Java {@link Message} 工作。
 *
 * <p>注：{@code MqttHeaders} 来自 spring-integration-mqtt，属于 MQTT 客户端设计约束。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class MqttAcknowledgmentFactory {

    private MqttAcknowledgmentFactory() {
    }

    /**
     * 从 {@link Message} 头信息解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象；QoS 0 时返回 empty
     */
    public static Optional<MqttAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Map<String, Object> headers = message.getHeaders();
        if (Objects.isNull(headers) || headers.isEmpty()) {
            return Optional.empty();
        }

        Object topicHeader = headers.get(MqttHeaders.RECEIVED_TOPIC);
        if (Objects.isNull(topicHeader)) {
            topicHeader = headers.get(MqttHeaders.TOPIC);
        }
        Object qosHeader = headers.get(MqttHeaders.RECEIVED_QOS);
        if (!(topicHeader instanceof String topic)) {
            return Optional.empty();
        }
        int qos = qosHeader instanceof Number number ? number.intValue() : 0;
        Object idHeader = headers.get(MqttHeaders.ID);
        String messageId = Objects.isNull(idHeader) ? null : String.valueOf(idHeader);
        if (qos <= 0) {
            return Optional.empty();
        }
        return Optional.of(new MqttAcknowledgment(topic, qos, messageId));
    }

    /**
     * 从 {@link Message} 头信息解析确认对象，QoS 0 时返回 NoOp 包装。
     *
     * @param message MQ 信封
     * @return 确认对象与是否为 QoS 确认的包装
     */
    public static AcknowledgmentOrNoOp resolve(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Optional<MqttAcknowledgment> ack = from(message);
        if (ack.isPresent()) {
            return new AcknowledgmentOrNoOp(ack.get(), true);
        }
        return new AcknowledgmentOrNoOp(new NoOpAcknowledgment(), false);
    }

    /**
     * 确认对象与是否为 QoS 确认的简单包装。
     *
     * @param acknowledgment 确认实现
     * @param qosAck         是否为 QoS 级确认
     */
    public record AcknowledgmentOrNoOp(
            Acknowledgment acknowledgment,
            boolean qosAck) {
    }
}
