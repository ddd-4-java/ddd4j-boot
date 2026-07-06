package io.ddd4j.boot.mq.rocket.spi;

import io.ddd4j.boot.mq.ack.Acknowledgment;
import io.ddd4j.boot.mq.config.MQProperties;
import io.ddd4j.boot.mq.consume.ConsumerHandler;
import io.ddd4j.boot.mq.contract.Message;
import io.ddd4j.boot.mq.registry.BrokerType;
import io.ddd4j.boot.mq.registry.ListenerDefinition;
import io.ddd4j.boot.mq.rocket.ack.RocketAcknowledgment;
import io.ddd4j.boot.mq.rocket.ack.RocketAcknowledgmentFactory;
import io.ddd4j.boot.mq.rocket.consumer.RocketMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;

/**
 * RocketMQ Broker 适配器（纯 Java，零 Spring 依赖）。
 * <p>Publisher 由 ddd4j-boot-mq-rocketmq 的 AutoConfiguration 直接创建 Bean。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class RocketBrokerAdapter implements BrokerAdapter {

    private final MQProperties properties;
    private final RocketMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.ROCKET;
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        RocketAcknowledgment rocketAck = message.nativeMessage(RocketAcknowledgment.class);
        if (rocketAck != null) {
            return rocketAck;
        }
        return RocketAcknowledgmentFactory.from(message).orElse(null);
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.ROCKET == configured;
    }

    public MQProperties properties() {
        return properties;
    }
}
