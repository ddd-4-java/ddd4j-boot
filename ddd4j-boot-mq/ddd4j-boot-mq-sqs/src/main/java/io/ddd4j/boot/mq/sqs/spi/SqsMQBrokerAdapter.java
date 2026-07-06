package io.ddd4j.boot.mq.sqs.spi;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import io.ddd4j.boot.mq.sqs.ack.SqsAcknowledgment;
import io.ddd4j.boot.mq.sqs.ack.SqsAcknowledgmentFactory;
import io.ddd4j.boot.mq.sqs.consumer.SqsMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.sqs.publisher.SqsMQEventPublisher;
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
 * AWS SQS Broker 适配器，桥接 ddd4j MQ SPI 与 aws-java-sdk-sqs。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@RequiredArgsConstructor
public class SqsBrokerAdapter implements BrokerAdapter {

    private final AmazonSQS amazonSqs;
    private final String defaultQueueUrl;
    private final MQProperties properties;
    private final SqsMQConsumerEndpointRegistrar consumerEndpointRegistrar;

    @Override
    public BrokerType brokerType() {
        return BrokerType.SQS;
    }

    @Override
    public MQEventPublisher createPublisher(MQProperties props) {
        return new SqsMQEventPublisher(amazonSqs, defaultQueueUrl, props);
    }

    @Override
    public void registerConsumer(ListenerDefinition definition, ConsumerHandler handler) {
        consumerEndpointRegistrar.register(definition, handler);
    }

    @Override
    public Acknowledgment resolveAcknowledgment(Message<?> message) {
        // 逻辑块：优先从 SQS 原生 Message 解析确认
        Message sqsMessage = message.nativeMessage(Message.class);
        if (Objects.nonNull(sqsMessage)) {
            return SqsAcknowledgmentFactory.from(message)
                    .map(ack -> (Acknowledgment) ack)
                    .orElse(null);
        }
        SqsAcknowledgment sqsAck = message.nativeMessage(SqsAcknowledgment.class);
        return sqsAck;
    }

    @Override
    public boolean supports(BrokerType configured) {
        return BrokerType.SQS == configured;
    }

    /**
     * 返回当前 MQ 配置。
     */
    public MQProperties properties() {
        return properties;
    }
}
