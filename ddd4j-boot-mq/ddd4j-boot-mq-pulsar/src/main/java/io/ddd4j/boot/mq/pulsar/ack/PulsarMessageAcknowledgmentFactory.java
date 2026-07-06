package io.ddd4j.boot.mq.pulsar.ack;

import io.ddd4j.boot.mq.contract.Message;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 从纯 Java {@link Message} 头信息构建 {@link PulsarAcknowledgment}。
 *
 * <p>2.0.x 重构：彻底移除对 {@code org.springframework.messaging.Message} 的依赖，
 * 直接基于 ddd4j-mq-core 的纯 Java {@link Message} 工作。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class PulsarAcknowledgmentFactory {

    private PulsarAcknowledgmentFactory() {
    }

    /**
     * 从 {@link Message} 头信息解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象；缺少必要头时返回 empty
     */
    public static Optional<PulsarAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Map<String, Object> headers = message.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return Optional.empty();
        }

        Object consumerHeader = headers.get(PulsarAcknowledgment.HEADER_PULSAR_CONSUMER);
        Object messageHeader = headers.get(PulsarAcknowledgment.HEADER_PULSAR_MESSAGE);
        if (!(consumerHeader instanceof Consumer<?> consumer) || !(messageHeader instanceof Message<?> pulsarMessage)) {
            return Optional.empty();
        }
        return Optional.of(new PulsarAcknowledgment(consumer, pulsarMessage));
    }
}
