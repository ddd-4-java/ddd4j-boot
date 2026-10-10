package io.ddd4j.boot.sample.order;

import io.ddd4j.sample.order.application.OutboxDispatchResult;
import io.ddd4j.sample.order.jdbc.TransactionalOutboxPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Objects;

/**
 * 周期性发送事务 Outbox 中仍待发布的订单集成事件。
 */
public final class OrderOutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderOutboxScheduler.class);

    /** 事务 Outbox 发布器 */
    private final TransactionalOutboxPublisher publisher;
    /** 示例配置属性 */
    private final OrderSampleProperties properties;

    /** 构造 OrderOutboxScheduler 对象。
     * @param publisher 事务 Outbox 发布器
     * @param properties 示例配置属性 */
    public OrderOutboxScheduler(TransactionalOutboxPublisher publisher, OrderSampleProperties properties) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
        this.properties = Objects.requireNonNull(properties, "properties must not be null");
    }

    /**
     * 发布 Outbox 中待发送的订单集成事件（固定延迟调度，间隔可配置）。
     */
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
