package io.ddd4j.boot.mq.activemq.spi;

import io.ddd4j.boot.mq.ack.Acknowledgment;
import io.ddd4j.boot.mq.activemq.ack.ActiveMQAcknowledgment;
import io.ddd4j.boot.mq.activemq.ack.ActiveMQAcknowledgmentFactory;
import io.ddd4j.boot.mq.activemq.consumer.ActiveMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.config.MQProperties;
import io.ddd4j.boot.mq.consume.ConsumerHandler;
import io.ddd4j.boot.mq.contract.Message;
import io.ddd4j.boot.mq.registry.BrokerType;
import io.ddd4j.boot.mq.registry.ListenerDefinition;
import io.ddd4j.boot.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;

/**
 * ActiveMQ Artemis Broker 适配器（纯 Java，零 Spring 依赖）。
 * <p>Publisher 由 ddd4j-boot-mq-activemq 的 AutoConfiguration 直接创建 Bean。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class ActiveBrokerAdapter implements BrokerAdapter {

    private final MQProperties properties;
    private final ActiveMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.ACTIVEMQ;
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        ActiveMQAcknowledgment activeMqAck = message.nativeMessage(ActiveMQAcknowledgment.class);
        if (activeMqAck != null) {
            return activeMqAck;
        }
        return ActiveMQAcknowledgmentFactory.from(message).orElse(null);
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.ACTIVEMQ == configured;
    }

    public MQProperties properties() {
        return properties;
    }
}
