package io.ddd4j.boot.mq.mqttmica.ack;

import io.ddd4j.mq.consume.ack.NoOpAcknowledgment;
import io.ddd4j.mq.consume.ack.Acknowledgment;
import io.ddd4j.mq.message.Message;
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage;
import org.dromara.mica.mqtt.codec.message.header.MqttPublishVariableHeader;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 从 mica-mqtt 入站消息构建 {@link MicaMqttAcknowledgment}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class MicaMqttAcknowledgmentFactory {

    private MicaMqttAcknowledgmentFactory() {
    }

    /**
     * 根据主题、mica 消息与 headers 解析确认对象。
     *
     * @param topic   MQTT 主题
     * @param message mica 发布消息（可为 null）
     * @param headers MQ 头信息
     * @return 确认对象；QoS 0 时返回 NoOp 包装
     */
    public static AcknowledgmentOrNoOp from(String topic, MqttPublishMessage message, Map<String, Object> headers) {
        int qos = resolveQos(message, headers);
        String messageId = resolveMessageId(message, headers);
        if (qos <= 0) {
            return new AcknowledgmentOrNoOp(new NoOpAcknowledgment(), false);
        }
        return new AcknowledgmentOrNoOp(
                new MicaMqttAcknowledgment(topic, qos, messageId), true);
    }

    /**
     * 从 {@link Message} 头信息解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象
     */
    public static Optional<MicaMqttAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Object topicHeader = message.getHeaders().get(MicaMqttHeaders.TOPIC);
        if (!(topicHeader instanceof String topic)) {
            return Optional.empty();
        }
        Object qosHeader = message.getHeaders().get(MicaMqttHeaders.QOS);
        int qos = qosHeader instanceof Number number ? number.intValue() : 0;
        Object idHeader = message.getHeaders().get(MicaMqttHeaders.MESSAGE_ID);
        String messageId = Objects.isNull(idHeader) ? null : String.valueOf(idHeader);
        if (qos <= 0) {
            return Optional.empty();
        }
        return Optional.of(new MicaMqttAcknowledgment(topic, qos, messageId));
    }

    /**
     * 构建 mica 消息头 Map。
     *
     * @param topic   主题
     * @param message mica 消息
     * @return headers
     */
    public static Map<String, Object> buildHeaders(String topic, MqttPublishMessage message) {
        Map<String, Object> headers = new HashMap<>(4);
        headers.put(MicaMqttHeaders.TOPIC, topic);
        int qos = resolveQos(message, headers);
        headers.put(MicaMqttHeaders.QOS, qos);
        String messageId = resolveMessageId(message, headers);
        if (StringUtils.hasText(messageId)) {
            headers.put(MicaMqttHeaders.MESSAGE_ID, messageId);
        }
        return headers;
    }

    /**
     * 解析 QoS。
     */
    private static int resolveQos(MqttPublishMessage message, Map<String, Object> headers) {
        if (Objects.nonNull(message) && Objects.nonNull(message.fixedHeader()) && Objects.nonNull(message.fixedHeader().qosLevel())) {
            return message.fixedHeader().qosLevel().value();
        }
        Object qosHeader = headers.get(MicaMqttHeaders.QOS);
        if (qosHeader instanceof Number number) {
            return number.intValue();
        }
        return 0;
    }

    /**
     * 解析消息 ID（packet id）。
     */
    private static String resolveMessageId(MqttPublishMessage message, Map<String, Object> headers) {
        if (Objects.nonNull(message)) {
            Object variableHeader = message.variableHeader();
            if (variableHeader instanceof MqttPublishVariableHeader publishHeader) {
                return String.valueOf(publishHeader.packetId());
            }
        }
        Object idHeader = headers.get(MicaMqttHeaders.MESSAGE_ID);
        return Objects.isNull(idHeader) ? null : String.valueOf(idHeader);
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
