package io.ddd4j.boot.mq.ons.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import io.ddd4j.boot.mq.ons.ack.OnsAcknowledgment;
import io.ddd4j.boot.mq.ons.ack.OnsAcknowledgmentFactory;
import io.ddd4j.mq.consume.Acknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.listener.EndpointNaming;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 将 {@code @EventListener} 动态注册为阿里云 ONS {@link Consumer} 端点。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class OnsMQConsumerEndpointRegistrar implements AutoCloseable {

    private final Properties onsConsumerProperties;
    private final MQProperties properties;
    private final List<ListenerDefinition> registeredDefinitions = new CopyOnWriteArrayList<>();
    private final List<Consumer> consumers = new CopyOnWriteArrayList<>();

    /**
     * 注册单个监听器定义。
     */
    public void register(ListenerDefinition definition, ConsumerHandler handler) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");

        String topic = EndpointNaming.physicalTopic(properties, definition);
        String tag = resolveTag(definition.getTags());

        Properties consumerProps = new Properties();
        consumerProps.putAll(onsConsumerProperties);
        consumerProps.setProperty(PropertyKeyConst.GROUP_ID, definition.getGroup());

        Consumer consumer = ONSFactory.createConsumer(consumerProps);
        consumer.subscribe(topic, tag, (message, context) -> onMessage(message, definition, handler));
        consumer.start();
        consumers.add(consumer);
        registeredDefinitions.add(definition);

        log.info("Registered ONS listener: topic={}, tag={}, group={}, ackMode={}",
                topic, tag, definition.getGroup(), properties.getConsumer().getAckMode());
    }

    /**
     * 批量注册监听器。
     */
    public void registerAll(List<ListenerDefinition> definitions, ConsumerHandler handler) {
        if (Objects.isNull(definitions) || definitions.isEmpty()) {
            log.debug("No @EventListener definitions found for ONS");
            return;
        }
        for (ListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("ONS consumer registrar initialized with {} listener(s)", registeredDefinitions.size());
    }

    @Override
    public void close() {
        for (Consumer consumer : consumers) {
            try {
                consumer.shutdown();
            } catch (Exception ex) {
                log.warn("Failed to shutdown ONS consumer", ex);
            }
        }
        consumers.clear();
    }

    /**
     * 返回已登记的监听器定义。
     */
    public List<ListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }

    /**
     * ONS 消息监听回调：将 handler 的 ack 语义映射为 {@link Action}。
     */
    private Action onMessage(Message onsMessage, ListenerDefinition definition, ConsumerHandler handler) {
        AtomicReference<Action> action = new AtomicReference<>(Action.CommitMessage);
        try {
            String payloadText = new String(onsMessage.getBody(), StandardCharsets.UTF_8);
            Map<String, Object> headers = new HashMap<>();
            headers.put("ons.topic", onsMessage.getTopic());
            headers.put("ons.tag", onsMessage.getTag());

            OnsAcknowledgment ack = OnsAcknowledgmentFactory.fromOnsMessage(onsMessage)
                    .orElseThrow(() -> new IllegalStateException("Failed to build ONS acknowledgment"));

            OnsAcknowledgment trackedAck = new OnsAcknowledgment(
                    onsMessage.getMsgID(),
                    onsMessage.getKey(),
                    onsMessage.getReconsumeTimes(),
                    () -> {
                        action.set(Action.CommitMessage);
                        return Action.CommitMessage;
                    },
                    () -> {
                        action.set(Action.ReconsumeLater);
                        return Action.ReconsumeLater;
                    });

            Message<String> mqMessage = Message.of(
                    payloadText,
                    headers,
                    onsMessage.getMsgID(),
                    onsMessage.getKey(),
                    onsMessage);

            Acknowledgment messageAck = trackedAck;
            handler.handle(mqMessage, messageAck);
            if (!properties.getConsumer().isManualAck() && !trackedAck.isAcknowledged()) {
                trackedAck.ack();
            }
            return action.get();
        } catch (Exception ex) {
            log.error("ONS consumer failed: bean={}, method={}",
                    beanLabel(definition), definition.getMethod().getName(), ex);
            return Action.ReconsumeLater;
        }
    }

    private String resolveTag(String tags) {
        String tag = EndpointNaming.resolveTag(tags);
        return Objects.isNull(tag) ? "*" : tag;
    }

    private String beanLabel(ListenerDefinition definition) {
        if (Objects.nonNull(definition.getBean())) {
            return definition.getBean().getClass().getSimpleName();
        }
        if (Objects.nonNull(definition.getBeanName())) {
            return definition.getBeanName();
        }
        return definition.getMethod().getDeclaringClass().getSimpleName();
    }
}
