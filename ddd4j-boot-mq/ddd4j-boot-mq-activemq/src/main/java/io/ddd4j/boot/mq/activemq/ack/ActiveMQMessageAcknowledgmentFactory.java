package io.ddd4j.boot.mq.activemq.ack;

import io.ddd4j.boot.mq.contract.Message;
import jakarta.jms.Message;
import jakarta.jms.Session;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 从纯 Java {@link Message} 头信息构建 {@link ActiveMQAcknowledgment}。
 *
 * <p>2.0.x 重构：彻底移除对 {@code org.springframework.messaging.Message} 的依赖，
 * 直接基于 ddd4j-mq-core 的纯 Java {@link Message} 工作。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class ActiveMQAcknowledgmentFactory {

    private ActiveMQAcknowledgmentFactory() {
    }

    /**
     * 从 {@link Message} 头信息解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象
     */
    public static Optional<ActiveMQAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Map<String, Object> headers = message.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return Optional.empty();
        }

        Object messageHeader = headers.get(ActiveMQAcknowledgment.HEADER_JMS_MESSAGE);
        Object sessionHeader = headers.get(ActiveMQAcknowledgment.HEADER_JMS_SESSION);

        Message jmsMessage = null;
        if (messageHeader instanceof Message headerMessage) {
            jmsMessage = headerMessage;
        } else if (message.getPayload() instanceof Message payloadMessage) {
            jmsMessage = payloadMessage;
        } else if (message.getNativeMessage() instanceof Message nativeMessage) {
            jmsMessage = nativeMessage;
        }
        if (jmsMessage == null) {
            return Optional.empty();
        }
        Session session = sessionHeader instanceof Session jmsSession ? jmsSession : null;
        return Optional.of(new ActiveMQAcknowledgment(jmsMessage, session));
    }
}
