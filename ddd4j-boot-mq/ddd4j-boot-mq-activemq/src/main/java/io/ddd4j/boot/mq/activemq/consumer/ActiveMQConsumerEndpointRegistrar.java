package io.ddd4j.boot.mq.activemq.consumer;

import io.ddd4j.mq.consume.ack.Acknowledgment;
import io.ddd4j.mq.consume.ack.NoOpAcknowledgment;
import io.ddd4j.mq.activemq.ack.ActiveMQAcknowledgment;
import io.ddd4j.mq.activemq.ack.ActiveMQAcknowledgmentFactory;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.listener.EndpointNaming;
import jakarta.jms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @EventListener} 动态注册为 ActiveMQ Artemis JMS 消费端点。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class ActiveMQConsumerEndpointRegistrar implements AutoCloseable {

    private final ApplicationContext applicationContext;
    private final JmsListenerEndpointRegistry endpointRegistry;
    private final MQProperties properties;
    private final List<ListenerDefinition> registeredDefinitions = new CopyOnWriteArrayList<>();
    private final List<String> endpointIds = new CopyOnWriteArrayList<>();

    /**
     * 从 JMS 消息提取文本载荷。
     */
    private static String extractPayload(Message jmsMessage) throws JMSException {
        if (jmsMessage instanceof TextMessage textMessage) {
            return textMessage.getText();
        }
        if (jmsMessage instanceof BytesMessage bytesMessage) {
            byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return jmsMessage.toString();
    }

    /**
     * 安全读取 JMSMessageID。
     */
    private static String safeMessageId(Message jmsMessage) {
        try {
            return jmsMessage.getJMSMessageID();
        } catch (JMSException ex) {
            return null;
        }
    }

    /**
     * 安全读取 JMSCorrelationID。
     */
    private static String safeCorrelationId(Message jmsMessage) {
        try {
            return jmsMessage.getJMSCorrelationID();
        } catch (JMSException ex) {
            return null;
        }
    }

    /**
     * 注册单个监听器定义。
     *
     * @param definition 监听器定义
     * @param handler    消费处理函数
     */
    public void register(ListenerDefinition definition, ConsumerHandler handler) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");

        String endpointId = EndpointNaming.endpointId("activemq", definition);
        String queueName = EndpointNaming.queueName(definition);

        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId(endpointId);
        endpoint.setDestination(queueName);
        endpoint.setMessageListener(new SessionAwareJmsMessageListener(definition, handler));

        JmsListenerContainerFactory<?> containerFactory = resolveContainerFactory();
        endpointRegistry.registerListenerContainer(endpoint, containerFactory, true);
        endpointIds.add(endpointId);
        registeredDefinitions.add(definition);

        log.info("Registered ActiveMQ listener endpoint: id={}, queue={}, ackMode={}",
                endpointId, queueName, properties.getConsumer().getAckMode());
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     *
     * @param definitions 监听器定义列表
     * @param handler     统一消费处理函数
     */
    public void registerAll(List<ListenerDefinition> definitions, ConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @EventListener definitions found for ActiveMQ");
            return;
        }
        for (ListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("ActiveMQ consumer registrar initialized with {} listener(s), ackMode={}",
                registeredDefinitions.size(), properties.getConsumer().getAckMode());
    }

    @Override
    public void close() {
        for (String endpointId : endpointIds) {
            try {
                if (endpointRegistry.getListenerContainer(endpointId) instanceof DefaultMessageListenerContainer container) {
                    container.stop();
                }
            } catch (Exception ex) {
                log.warn("Failed to stop ActiveMQ listener container: id={}", endpointId, ex);
            }
        }
        endpointIds.clear();
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<ListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }

    /**
     * 解析 JmsListenerContainerFactory，并按 ack-mode 配置 Session 确认模式。
     */
    @SuppressWarnings("rawtypes")
    private JmsListenerContainerFactory resolveContainerFactory() {
        ConnectionFactory connectionFactory = applicationContext.getBean(ConnectionFactory.class);
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setSessionAcknowledgeMode(
                properties.getConsumer().isManualAck()
                        ? Session.CLIENT_ACKNOWLEDGE
                        : Session.AUTO_ACKNOWLEDGE);
        return factory;
    }

    /**
     * 同时实现 {@link MessageListener} 与 {@link SessionAwareMessageListener}，
     * 以便 {@link SimpleJmsListenerEndpoint} 注册后容器仍能注入 JMS Session。
     */
    private final class SessionAwareJmsMessageListener
            implements MessageListener, SessionAwareMessageListener<Message> {

        private final ListenerDefinition definition;
        private final ConsumerHandler handler;

        private SessionAwareJmsMessageListener(ListenerDefinition definition, ConsumerHandler handler) {
            this.definition = definition;
            this.handler = handler;
        }

        @Override
        public void onMessage(Message message) {
            throw new UnsupportedOperationException("Use session-aware onMessage(Message, Session)");
        }

        @Override
        public void onMessage(Message jmsMessage, Session session) throws JMSException {
            try {
                String payloadText = extractPayload(jmsMessage);

                // 2.0.x：直接构造纯 Java Message，jakarta.jms.Message 通过 nativeMessage 逃生口传入
                Map<String, Object> headers = new HashMap<>();
                headers.put(ActiveMQAcknowledgment.HEADER_JMS_MESSAGE, jmsMessage);
                headers.put(ActiveMQAcknowledgment.HEADER_JMS_SESSION, session);

                Message<String> mqMessage = Message.of(
                        payloadText,
                        headers,
                        safeMessageId(jmsMessage),
                        safeCorrelationId(jmsMessage),
                        jmsMessage);

                Acknowledgment ack = ActiveMQAcknowledgmentFactory.from(mqMessage)
                        .map(a -> (Acknowledgment) a)
                        .orElseGet(NoOpAcknowledgment::new);

                handler.handle(mqMessage, ack);
            } catch (Exception ex) {
                log.error("ActiveMQ consumer failed: bean={}, method={}",
                        definition.getBean().getClass().getSimpleName(),
                        definition.getMethod().getName(),
                        ex);
                if (properties.getConsumer().isManualAck() && session != null) {
                    session.recover();
                }
            }
        }
    }
}
