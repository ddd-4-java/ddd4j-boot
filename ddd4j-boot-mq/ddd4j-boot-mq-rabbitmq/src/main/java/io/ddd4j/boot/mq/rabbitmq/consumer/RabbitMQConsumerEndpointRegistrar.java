package io.ddd4j.boot.mq.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import io.ddd4j.mq.consume.Acknowledgment;
import io.ddd4j.mq.consume.NoOpAcknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.message.Message;
import io.ddd4j.mq.rabbit.ack.AmqpAcknowledgmentFactory;
import io.ddd4j.mq.listener.ListenerDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 将 {@code @EventListener} 动态注册为 RabbitMQ 消费端点。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConsumerEndpointRegistrar {

    private final ApplicationContext applicationContext;
    private final RabbitListenerEndpointRegistry endpointRegistry;
    private final MQProperties properties;
    private final List<ListenerDefinition> registeredDefinitions = new CopyOnWriteArrayList<>();

    /**
     * 注册单个监听器定义。
     *
     * @param definition 监听器定义
     * @param handler    消费处理函数
     */
    public void register(ListenerDefinition definition, ConsumerHandler handler) {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(handler, "handler");

        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        RabbitAdmin rabbitAdmin = resolveRabbitAdmin(rabbitTemplate);
        RabbitListenerContainerFactory<?> containerFactory = resolveContainerFactory();

        String concat = resolveConcat(definition);
        String queueName = buildQueueName(definition, concat);
        String exchangeName = rabbitTemplate.getExchange();
        String routingKey = buildRoutingKey(definition, concat);

        declareTopology(rabbitAdmin, queueName, exchangeName, routingKey);

        SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId(buildEndpointId(definition));
        endpoint.setQueueNames(queueName);
        endpoint.setAckMode(properties.getConsumer().isManualAck()
                ? AcknowledgeMode.MANUAL
                : AcknowledgeMode.AUTO);
        endpoint.setMessageListener(createMessageListener(definition, handler));

        endpointRegistry.registerListenerContainer(endpoint, containerFactory, true);
        registeredDefinitions.add(definition);

        log.info("Registered RabbitMQ listener endpoint: id={}, queue={}, routingKey={}, ackMode={}",
                endpoint.getId(), queueName, routingKey, properties.getConsumer().getAckMode());
    }

    /**
     * 批量注册监听器（启动阶段调用）。
     */
    public void registerAll(List<ListenerDefinition> definitions, ConsumerHandler handler) {
        if (definitions == null || definitions.isEmpty()) {
            log.debug("No @EventListener definitions found for RabbitMQ");
            return;
        }
        for (ListenerDefinition definition : definitions) {
            register(definition, handler);
        }
        log.info("RabbitMQ consumer registrar initialized with {} listener(s)", registeredDefinitions.size());
    }

    /**
     * 返回已登记的监听器定义（只读视图）。
     */
    public List<ListenerDefinition> registeredDefinitions() {
        return List.copyOf(registeredDefinitions);
    }

    /**
     * 创建 ChannelAware 消息监听器，转换 AMQP 消息并委托 {@link ConsumerHandler}。
     */
    private ChannelAwareMessageListener createMessageListener(
            ListenerDefinition definition,
            ConsumerHandler handler) {

        return (Message amqpMessage, Channel channel) -> {
            try {
                String payloadText = new String(amqpMessage.getBody(), StandardCharsets.UTF_8);

                // 2.0.x：直接构造纯 Java Message，Channel/deliveryTag 通过 headers 传递（与 AmqpAcknowledgmentFactory.from(Message) 配套）
                Map<String, Object> headers = new HashMap<>();
                Map<String, Object> propsHeaders = amqpMessage.getMessageProperties().getHeaders();
                if (propsHeaders != null) {
                    headers.putAll(propsHeaders);
                }
                headers.put(AmqpHeaders.CHANNEL, channel);
                headers.put(AmqpHeaders.DELIVERY_TAG, amqpMessage.getMessageProperties().getDeliveryTag());

                Message<String> mqMessage = Message.of(
                        payloadText,
                        headers,
                        amqpMessage.getMessageProperties().getMessageId(),
                        amqpMessage.getMessageProperties().getCorrelationId(),
                        amqpMessage);

                Acknowledgment ack = AmqpAcknowledgmentFactory.from(mqMessage)
                        .map(a -> (Acknowledgment) a)
                        .orElseGet(NoOpAcknowledgment::new);

                handler.handle(mqMessage, ack);
            } catch (Exception ex) {
                log.error("RabbitMQ consumer failed: bean={}, method={}",
                        definition.getBean().getClass().getSimpleName(),
                        definition.getMethod().getName(),
                        ex);
                if (properties.getConsumer().isManualAck() && channel != null && channel.isOpen()) {
                    try {
                        channel.basicNack(amqpMessage.getMessageProperties().getDeliveryTag(), false, true);
                    } catch (Exception nackEx) {
                        log.warn("Failed to nack RabbitMQ message after consumer error", nackEx);
                    }
                }
            }
        };
    }

    /**
     * 声明队列、交换机与绑定关系。
     */
    private void declareTopology(RabbitAdmin rabbitAdmin, String queueName, String exchangeName, String routingKey) {
        Queue queue = new Queue(queueName, true);
        DirectExchange exchange = new DirectExchange(exchangeName, true, false);
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(routingKey);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);
    }

    /**
     * 构建队列名：group.namespace.className.methodName。
     */
    private String buildQueueName(ListenerDefinition definition, String concat) {
        String group = definition.getGroup();
        String namespace = definition.getNamespace();
        String className = definition.getMethod().getDeclaringClass().getSimpleName();
        String methodName = definition.getMethod().getName();
        return group + concat + namespace + concat + className + concat + methodName;
    }

    /**
     * 构建路由键：namespace.topic[.tag]。
     */
    private String buildRoutingKey(ListenerDefinition definition, String concat) {
        String namespace = definition.getNamespace();
        String topic = definition.getTopic();
        String tags = definition.getTags();
        if (StringUtils.hasText(tags) && !"*".equals(tags.trim())) {
            String tag = tags.contains("||") ? tags.substring(0, tags.indexOf("||")).trim() : tags.trim();
            return namespace + concat + topic + concat + tag;
        }
        return namespace + concat + topic;
    }

    /**
     * 解析连接符，默认 {@code .}。
     */
    private String resolveConcat(ListenerDefinition definition) {
        if (StringUtils.hasText(definition.getConcat())) {
            return definition.getConcat();
        }
        return ".";
    }

    /**
     * 构建端点 ID，保证在 registry 内唯一。
     */
    private String buildEndpointId(ListenerDefinition definition) {
        return "ddd4j-" + definition.bindingName() + "-"
                + definition.getMethod().getDeclaringClass().getSimpleName() + "-"
                + definition.getMethod().getName();
    }

    /**
     * 解析 RabbitAdmin，不存在时基于 RabbitTemplate 创建。
     */
    private RabbitAdmin resolveRabbitAdmin(RabbitTemplate rabbitTemplate) {
        if (applicationContext.containsBean("rabbitAdmin")) {
            return applicationContext.getBean(RabbitAdmin.class);
        }
        return new RabbitAdmin(rabbitTemplate);
    }

    /**
     * 解析 RabbitListenerContainerFactory Bean。
     */
    @SuppressWarnings("rawtypes")
    private RabbitListenerContainerFactory resolveContainerFactory() {
        if (applicationContext.containsBean("rabbitListenerContainerFactory")) {
            return applicationContext.getBean("rabbitListenerContainerFactory", RabbitListenerContainerFactory.class);
        }
        return applicationContext.getBean(RabbitListenerContainerFactory.class);
    }
}
