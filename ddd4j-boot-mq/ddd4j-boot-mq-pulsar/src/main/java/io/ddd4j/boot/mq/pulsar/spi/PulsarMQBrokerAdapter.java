package io.ddd4j.boot.mq.pulsar.spi;

import io.ddd4j.boot.mq.ack.Acknowledgment;
import io.ddd4j.boot.mq.config.MQProperties;
import io.ddd4j.boot.mq.consume.ConsumerHandler;
import io.ddd4j.boot.mq.contract.Message;
import io.ddd4j.boot.mq.pulsar.ack.PulsarAcknowledgment;
import io.ddd4j.boot.mq.pulsar.ack.PulsarAcknowledgmentFactory;
import io.ddd4j.boot.mq.pulsar.consumer.PulsarConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.registry.BrokerType;
import io.ddd4j.boot.mq.registry.ListenerDefinition;
import io.ddd4j.boot.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;

/**
 * Pulsar Broker 适配器（纯 Java，零 Spring 依赖）。
 * <p>Publisher 由 ddd4j-boot-mq-pulsar 的 AutoConfiguration 直接创建 Bean。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class PulsarBrokerAdapter implements BrokerAdapter {

    private final MQProperties properties;
    private final PulsarConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.PULSAR;
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        PulsarAcknowledgment pulsarAck = message.nativeMessage(PulsarAcknowledgment.class);
        if (pulsarAck != null) {
            return pulsarAck;
        }
        return PulsarAcknowledgmentFactory.from(message).orElse(null);
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.PULSAR == configured;
    }

    public MQProperties properties() {
        return properties;
    }
}
