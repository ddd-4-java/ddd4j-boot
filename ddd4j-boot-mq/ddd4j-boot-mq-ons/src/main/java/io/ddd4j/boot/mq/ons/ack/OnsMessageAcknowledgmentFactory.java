package io.ddd4j.boot.mq.ons.ack;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Message;
import io.ddd4j.mq.message.Message;

import java.util.Objects;
import java.util.Optional;

/**
 * 从 ONS {@link Message} 构建 {@link OnsAcknowledgment}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class OnsAcknowledgmentFactory {

    private OnsAcknowledgmentFactory() {
    }

    /**
     * 从 ONS 原生消息解析确认对象（骨架：需配合消费上下文回调）。
     *
     * @param message ONS 消息
     * @return 确认对象
     */
    public static Optional<OnsAcknowledgment> fromOnsMessage(Message message) {
        if (Objects.isNull(message)) {
            return Optional.empty();
        }
        return Optional.of(new OnsAcknowledgment(
                message.getMsgID(),
                message.getKey(),
                message.getReconsumeTimes(),
                () -> Action.CommitMessage,
                () -> Action.ReconsumeLater));
    }

    /**
     * 从 {@link Message} 解析确认对象。
     *
     * @param message MQ 信封
     * @return 确认对象
     */
    public static Optional<OnsAcknowledgment> from(Message<?> message) {
        Objects.requireNonNull(message, "message");
        Message onsMessage = message.nativeMessage(Message.class);
        if (Objects.nonNull(onsMessage)) {
            return fromOnsMessage(onsMessage);
        }
        Object payload = message.getPayload();
        if (payload instanceof Message nativeMessage) {
            return fromOnsMessage(nativeMessage);
        }
        OnsAcknowledgment ack = message.nativeMessage(OnsAcknowledgment.class);
        return Objects.isNull(ack) ? Optional.empty() : Optional.of(ack);
    }
}
