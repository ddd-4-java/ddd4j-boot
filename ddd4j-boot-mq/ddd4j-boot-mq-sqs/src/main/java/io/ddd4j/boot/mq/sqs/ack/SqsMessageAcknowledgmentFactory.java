package io.ddd4j.boot.mq.sqs.ack;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import io.ddd4j.mq.message.Message;

import java.util.Objects;
import java.util.Optional;

/**
 * 从 SQS {@link Message} 构建 {@link SqsAcknowledgment}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class SqsAcknowledgmentFactory {

    public static final String HEADER_QUEUE_URL = "sqsQueueUrl";
    public static final String HEADER_AMAZON_SQS = "amazonSqs";

    private SqsAcknowledgmentFactory() {
    }

    /**
     * 从 SQS 原生消息解析确认对象。
     *
     * @param amazonSqs SQS 客户端
     * @param queueUrl  队列 URL
     * @param message   SQS 消息
     * @return 确认对象
     */
    public static Optional<SqsAcknowledgment> fromSqsMessage(
            AmazonSQS amazonSqs, String queueUrl, Message message) {
        if (Objects.isNull(amazonSqs) || Objects.isNull(queueUrl) || Objects.isNull(message)) {
            return Optional.empty();
        }
        return Optional.of(new SqsAcknowledgment(amazonSqs, queueUrl, message));
    }

    /**
     * 从 {@link Message} 解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象
     */
    public static Optional<SqsAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Message sqsMessage = message.nativeMessage(Message.class);
        if (Objects.nonNull(sqsMessage)) {
            AmazonSQS amazonSqs = resolveAmazonSqs(message);
            String queueUrl = resolveQueueUrl(message);
            return fromSqsMessage(amazonSqs, queueUrl, sqsMessage);
        }
        SqsAcknowledgment ack = message.nativeMessage(SqsAcknowledgment.class);
        return Objects.isNull(ack) ? Optional.empty() : Optional.of(ack);
    }

    /**
     * 从 MQ 头信息解析 SQS 客户端。
     */
    private static AmazonSQS resolveAmazonSqs(Message<?> message) {
        Object client = message.getHeaders().get(HEADER_AMAZON_SQS);
        return client instanceof AmazonSQS amazonSqs ? amazonSqs : null;
    }

    /**
     * 从 MQ 头信息解析队列 URL。
     */
    private static String resolveQueueUrl(Message<?> message) {
        Object queueUrl = message.getHeaders().get(HEADER_QUEUE_URL);
        return Objects.isNull(queueUrl) ? null : String.valueOf(queueUrl);
    }
}
