package io.ddd4j.boot.mq.rabbit.spi;

import io.ddd4j.boot.mq.ack.Acknowledgment;
import io.ddd4j.boot.mq.config.MQProperties;
import io.ddd4j.boot.mq.consume.ConsumerHandler;
import io.ddd4j.boot.mq.contract.Message;
import io.ddd4j.boot.mq.rabbit.ack.AmqpAcknowledgmentFactory;
import io.ddd4j.boot.mq.rabbit.consumer.RabbitMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.registry.BrokerType;
import io.ddd4j.boot.mq.registry.ListenerDefinition;
import io.ddd4j.boot.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;

/**
 * RabbitMQ Broker 适配器，桥接 ddd4j MQ SPI 与 Spring AMQP。
 * <p>2.0.x 重构：Publisher 由 ddd4j-boot-mq-rabbitmq 的 AutoConfiguration 直接创建 Bean，
 * 本类不再引用 Spring 客户端库。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class RabbitBrokerAdapter implements BrokerAdapter {

    private final MQProperties properties;
    private final RabbitMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.RABBIT;
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        // 2.0.x：直接基于纯 Java Message 解析（headers 中携带 Channel/deliveryTag）
        return AmqpAcknowledgmentFactory.from(message).orElse(null);
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.RABBIT == configured;
    }

    /**
     * 返回当前 MQ 配置。
     */
    public MQProperties properties() {
        return properties;
    }
}
