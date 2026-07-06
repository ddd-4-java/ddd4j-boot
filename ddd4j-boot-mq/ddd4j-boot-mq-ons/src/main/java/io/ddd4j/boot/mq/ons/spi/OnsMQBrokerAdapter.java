package io.ddd4j.boot.mq.ons.spi;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import io.ddd4j.boot.mq.ons.ack.OnsAcknowledgment;
import io.ddd4j.boot.mq.ons.ack.OnsAcknowledgmentFactory;
import io.ddd4j.boot.mq.ons.consumer.OnsMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.ons.publisher.OnsMQEventPublisher;
import io.ddd4j.mq.consume.ack.Acknowledgment;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerHandler;
import io.ddd4j.mq.event.MQEventPublisher;
import io.ddd4j.mq.listener.BrokerType;
import io.ddd4j.mq.listener.ListenerDefinition;
import io.ddd4j.mq.spi.BrokerAdapter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * 阿里云 ONS Broker 适配器，桥接 ddd4j MQ SPI 与 ons-client（Rocket 兼容）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class OnsBrokerAdapter implements BrokerAdapter {

    private final Producer producer;
    private final MQProperties properties;
    private final OnsMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.ONS;
    }

    @Override
    public MQEventPublisher createPublisher(MQProperties props) {
        return new OnsMQEventPublisher(producer, props);
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        // 逻辑块：优先从 ONS 原生 Message 解析确认
        Message onsMessage = message.nativeMessage(Message.class);
        if (Objects.nonNull(onsMessage)) {
            return OnsAcknowledgmentFactory.fromOnsMessage(onsMessage)
                    .map(ack -> (Acknowledgment) ack)
                    .orElse(null);
        }
        OnsAcknowledgment onsAck = message.nativeMessage(OnsAcknowledgment.class);
        return onsAck;
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.ONS == configured;
    }

    /**
     * 返回当前 MQ 配置。
     */
    public MQProperties properties() {
        return properties;
    }
}
