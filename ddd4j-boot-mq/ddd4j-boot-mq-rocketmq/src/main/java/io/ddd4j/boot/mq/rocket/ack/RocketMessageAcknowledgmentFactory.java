package io.ddd4j.boot.mq.rocket.ack;

import io.ddd4j.boot.mq.contract.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 从纯 Java {@link Message} 构建 {@link RocketAcknowledgment}。
 *
 * <p>2.0.x 重构：彻底移除对 {@code org.springframework.messaging.Message} 的依赖，
 * 直接基于 ddd4j-mq-core 的纯 Java {@link Message} 工作。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class RocketAcknowledgmentFactory {

    private RocketAcknowledgmentFactory() {
    }

    /**
     * 从 {@link Message} 头信息解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象；缺少必要头时返回 empty
     */
    @SuppressWarnings("unchecked")
    public static Optional<RocketAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Map<String, Object> headers = message.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return Optional.empty();
        }

        // 逻辑块：从 header 或 payload 解析 RocketMQ MessageExt
        MessageExt messageExt = resolveMessageExt(headers, message);
        if (messageExt == null) {
            return Optional.empty();
        }

        Object callbackHeader = headers.get(RocketAcknowledgment.HEADER_ROCKET_ACK_CALLBACK);
        Consumer<Boolean> ackCallback = callbackHeader instanceof Consumer<?> consumer
                ? (Consumer<Boolean>) consumer
                : null;
        return Optional.of(new RocketAcknowledgment(messageExt, ackCallback));
    }

    /**
     * 从 headers 或 payload 解析 MessageExt。
     */
    private static MessageExt resolveMessageExt(Map<String, Object> headers, Message<?> message) {
        Object ext = headers.get(RocketAcknowledgment.HEADER_ROCKET_MESSAGE);
        if (ext instanceof MessageExt messageExt) {
            return messageExt;
        }
        Object payload = message.getPayload();
        if (payload instanceof MessageExt messageExt) {
            return messageExt;
        }
        return null;
    }
}
