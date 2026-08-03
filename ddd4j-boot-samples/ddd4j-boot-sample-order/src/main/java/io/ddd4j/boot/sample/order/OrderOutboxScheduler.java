package io.ddd4j.boot.sample.order;

import io.ddd4j.sample.order.application.OutboxDispatchResult;
import io.ddd4j.sample.order.jdbc.TransactionalOutboxPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

/**
 * 周期性发送事务 Outbox 中仍待发布的订单集成事件。
 */
@Slf4j
public final class OrderOutboxScheduler {

    private final TransactionalOutboxPublisher publisher;
    private final OrderSampleProperties properties;

    public OrderOutboxScheduler(TransactionalOutboxPublisher publisher, OrderSampleProperties properties) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    @Scheduled(fixedDelayString = "${ddd4j.sample.order.outbox-delay-millis:5000}")
    public void publishPending() {
        OutboxDispatchResult result = publisher.publishPending(properties.getOutboxBatchSize());
        if (result.failed() > 0) {
            log.warn("Order Outbox dispatch retained {} failed messages for retry", result.failed());
        }
        if (result.published() > 0) {
            log.info("Order Outbox published {}/{} messages", result.published(), result.attempted());
        }
    }
}
