package io.ddd4j.boot.sample.order;

import io.ddd4j.sample.order.application.IdempotencyPort;
import io.ddd4j.sample.order.application.OrderReadModel;
import io.ddd4j.sample.order.application.OrderReadModelPort;
import io.ddd4j.sample.order.application.OrderTransactionPort;
import io.ddd4j.sample.order.application.OutboxMessage;
import io.ddd4j.sample.order.application.OutboxPort;
import io.ddd4j.sample.order.domain.Order;
import io.ddd4j.sample.order.domain.OrderQuery;
import io.ddd4j.sample.order.domain.OrderRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Boot 示例的本地适配器。
 *
 * <p>它只用于快速启动；生产配置应将这些端口分别替换为 PostgreSQL、Redis 和 Kafka Outbox 适配器。
 */
public final class InMemoryOrderAdapters implements OrderRepository, OutboxPort, OrderReadModelPort, IdempotencyPort,
        OrderTransactionPort {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final Map<String, OrderReadModel> readModels = new ConcurrentHashMap<>();
    private final Map<String, OutboxMessage> pendingMessages = new ConcurrentHashMap<>();
    private final Set<String> idempotencyKeys = ConcurrentHashMap.newKeySet();
    private final Object transactionMonitor = new Object();

    @Override
    public void execute(Runnable operation) {
        Objects.requireNonNull(operation, "operation must not be null");
        synchronized (transactionMonitor) {
            operation.run();
        }
    }

    @Override
    public void save(Order order) {
        orders.put(order.id(), order);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orders.values().stream().filter(order -> order.orderNo().equals(orderNo)).findFirst();
    }

    @Override
    public List<Order> findAll(int offset, int limit) {
        return orders.values().stream().skip(offset).limit(limit).toList();
    }

    @Override
    public long count() {
        return orders.size();
    }

    @Override
    public void append(List<OutboxMessage> messages) {
        messages.forEach(message -> pendingMessages.put(message.id(), message));
    }

    @Override
    public List<OutboxMessage> pending(int limit) {
        return new ArrayList<>(pendingMessages.values()).stream().limit(limit).toList();
    }

    @Override
    public void markPublished(String messageId) {
        pendingMessages.remove(messageId);
    }

    @Override
    public void markFailed(String messageId, String reason) {
        // 保留待发布消息，让后台发布器在下一轮重试。
    }

    @Override
    public void project(OrderReadModel order) {
        readModels.put(order.id(), order);
    }

    @Override
    public Optional<OrderReadModel> findProjectionById(String orderId) {
        return Optional.ofNullable(readModels.get(orderId));
    }

    @Override
    public List<OrderReadModel> query(OrderQuery query) {
        return readModels.values().stream()
                .filter(order -> Objects.isNull(query.buyerId()) || query.buyerId().equals(order.buyerId()))
                .filter(order -> Objects.isNull(query.status()) || query.status() == order.status())
                .skip((long) (query.page() - 1) * query.size())
                .limit(query.size())
                .toList();
    }

    @Override
    public boolean acquire(String key, Duration ttl) {
        return idempotencyKeys.add(key);
    }

    @Override
    public void complete(String key, Object result) {
        idempotencyKeys.add(key);
    }

    @Override
    public void release(String key) {
        idempotencyKeys.remove(key);
    }
}
