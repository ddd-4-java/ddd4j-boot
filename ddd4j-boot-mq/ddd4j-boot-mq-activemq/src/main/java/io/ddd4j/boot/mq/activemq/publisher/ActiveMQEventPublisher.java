package io.ddd4j.boot.mq.activemq.publisher;

import io.ddd4j.mq.event.MQEvent;
import io.ddd4j.kit.lang.JsonKit;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.message.Destination;
import io.ddd4j.mq.event.MQEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * 基于 {@link JmsTemplate}（ActiveMQ Artemis）的领域事件发布实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class ActiveMQEventPublisher implements MQEventPublisher {

    private final JmsTemplate jmsTemplate;
    private final MQProperties properties;

    @Override
    public <T extends MQEvent> void publish(T event, Destination destination) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(destination, "destination");

        // 逻辑块：补齐事件元数据
        if (!StringUtils.hasText(event.getTopic())) {
            event.setTopic(properties.getDefaultTopic());
        }
        if (!StringUtils.hasText(event.getNamespace())) {
            event.setNamespace(properties.getNamespace());
        }
        if (event.getMsgId() == null) {
            event.setMsgId(String.valueOf(System.currentTimeMillis()));
        }

        // 逻辑块：序列化并发送到 JMS destination
        String payload = JsonKit.toJson(event);
        String jmsDestination = buildJmsDestination(destination, event.getTag());
        jmsTemplate.convertAndSend(jmsDestination, payload);
        log.debug("Published ActiveMQ event, destination={}, msgId={}", jmsDestination, event.getMsgId());
    }

    /**
     * 根据目的地与 tag 生成 JMS destination 名称。
     */
    private String buildJmsDestination(Destination destination, String eventTag) {
        String namespace = StringUtils.hasText(destination.getNamespace())
                ? destination.getNamespace()
                : properties.getNamespace();
        String topic = StringUtils.hasText(destination.getTopic())
                ? destination.getTopic()
                : properties.getDefaultTopic();
        String tag = StringUtils.hasText(destination.getTag()) ? destination.getTag() : eventTag;
        String base = StringUtils.hasText(namespace) ? namespace + "." + topic : topic;
        if (!StringUtils.hasText(tag)) {
            return base;
        }
        return base + "." + tag;
    }
}
