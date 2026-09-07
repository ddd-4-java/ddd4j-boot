package io.ddd4j.boot.sample.order;

import tools.jackson.databind.ObjectMapper;
import io.ddd4j.core.cache.CacheConfig;
import io.ddd4j.cache.CacheKit;
import io.ddd4j.cache.local.CaffeineCache;
import io.ddd4j.sample.order.application.IdempotencyPort;
import io.ddd4j.sample.order.application.IntegrationEventPublisher;
import io.ddd4j.sample.order.application.OrderApplicationService;
import io.ddd4j.sample.order.application.OrderReadModelPort;
import io.ddd4j.sample.order.application.OrderTransactionPort;
import io.ddd4j.sample.order.application.OutboxPort;
import io.ddd4j.sample.order.application.OutboxPublisher;
import io.ddd4j.sample.order.application.ResilientIdempotencyPort;
import io.ddd4j.sample.order.application.UnavailableIntegrationEventPublisher;
import io.ddd4j.sample.order.domain.OrderRepository;
import io.ddd4j.sample.order.jdbc.JdbcOrderReadModelPort;
import io.ddd4j.sample.order.jdbc.JdbcOrderRepository;
import io.ddd4j.sample.order.jdbc.JdbcOrderTransactionPort;
import io.ddd4j.sample.order.jdbc.JdbcOutboxPort;
import io.ddd4j.sample.order.jdbc.TransactionalOutboxPublisher;
import io.ddd4j.sample.order.kafka.KafkaIntegrationEventPublisher;
import io.ddd4j.sample.order.redis.RedisIdempotencyPort;
import io.ddd4j.web.core.idempotency.CacheIdempotencyGuard;
import io.ddd4j.web.core.idempotency.IdempotencyGuard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * Order 生产基础设施示例：PostgreSQL 事务写侧、Redis 幂等和 Kafka Outbox 发布。
 */
@Configuration(proxyBeanMethods = false)
@EnableScheduling
@EnableConfigurationProperties(OrderSampleProperties.class)
@ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "infrastructure", havingValue = "postgres")
public class OrderPostgresInfrastructureConfiguration {

    private static final Logger log = LoggerFactory.getLogger(OrderPostgresInfrastructureConfiguration.class);

    @Bean
    public JdbcOrderTransactionPort orderTransactionPort(DataSource dataSource) {
        return new JdbcOrderTransactionPort(dataSource);
    }

    @Bean
    public OrderRepository orderRepository(JdbcOrderTransactionPort transaction) {
        return new JdbcOrderRepository(transaction);
    }

    @Bean
    public OutboxPort orderOutboxPort(JdbcOrderTransactionPort transaction, ObjectMapper objectMapper) {
        return new JdbcOutboxPort(transaction, objectMapper);
    }

    @Bean
    public OrderReadModelPort orderReadModelPort(JdbcOrderTransactionPort transaction) {
        return new JdbcOrderReadModelPort(transaction);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(UnifiedJedis.class)
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "redis-enabled", havingValue = "true",
            matchIfMissing = true)
    public UnifiedJedis orderRedisClient(OrderSampleProperties properties) {
        return new JedisPooled(properties.getRedisHost(), properties.getRedisPort());
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyPort.class)
    public IdempotencyPort orderIdempotencyPort(ObjectProvider<UnifiedJedis> redisClientProvider) {
        IdempotencyPort fallback = new CacheKitOrderIdempotencyPort();
        UnifiedJedis redisClient = redisClientProvider.getIfAvailable();
        if (Objects.isNull(redisClient)) {
            log.warn("Redis idempotency is disabled; using local CacheKit fallback for this sample instance");
            return fallback;
        }
        return new ResilientIdempotencyPort(new RedisIdempotencyPort(redisClient), fallback);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(Producer.class)
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "kafka-enabled", havingValue = "true",
            matchIfMissing = true)
    public Producer<String, String> orderKafkaProducer(OrderSampleProperties properties) {
        return new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getKafkaBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName(),
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true
        ));
    }

    @Bean
    @ConditionalOnMissingBean(IntegrationEventPublisher.class)
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "kafka-enabled", havingValue = "true",
            matchIfMissing = true)
    public IntegrationEventPublisher orderIntegrationEventPublisher(Producer<String, String> orderKafkaProducer,
                                                                     ObjectMapper objectMapper,
                                                                     OrderSampleProperties properties) {
        return new KafkaIntegrationEventPublisher(orderKafkaProducer, objectMapper, properties.getKafkaTopic());
    }

    @Bean
    public OutboxPublisher orderOutboxPublisher(OutboxPort orderOutboxPort,
                                                ObjectProvider<IntegrationEventPublisher> publisherProvider) {
        IntegrationEventPublisher publisher = publisherProvider.getIfAvailable();
        if (Objects.isNull(publisher)) {
            log.warn("Kafka publisher is disabled; Order Outbox messages will remain pending for retry");
            publisher = new UnavailableIntegrationEventPublisher(
                    "Order Kafka publisher is disabled; Outbox message remains pending");
        }
        return new OutboxPublisher(orderOutboxPort, publisher);
    }

    @Bean
    public TransactionalOutboxPublisher transactionalOutboxPublisher(JdbcOrderTransactionPort transaction,
                                                                      OutboxPublisher orderOutboxPublisher) {
        return new TransactionalOutboxPublisher(transaction, orderOutboxPublisher);
    }

    @Bean
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "outbox-scheduler-enabled", havingValue = "true",
            matchIfMissing = true)
    public OrderOutboxScheduler orderOutboxScheduler(TransactionalOutboxPublisher transactionalOutboxPublisher,
                                                      OrderSampleProperties properties) {
        return new OrderOutboxScheduler(transactionalOutboxPublisher, properties);
    }

    @Bean
    public OrderApplicationService orderApplicationService(OrderRepository orderRepository,
                                                           OutboxPort orderOutboxPort,
                                                           OrderReadModelPort orderReadModelPort,
                                                           IdempotencyPort orderIdempotencyPort,
                                                           OrderTransactionPort orderTransactionPort) {
        return new OrderApplicationService(orderRepository, orderOutboxPort, orderReadModelPort,
                orderIdempotencyPort, orderTransactionPort);
    }

    /**
     * 将 Web 层已验证的 CacheKit 原子幂等状态机适配为订单应用端口，仅用于 Redis 不可用时的单实例降级。
     */
    private static final class CacheKitOrderIdempotencyPort implements IdempotencyPort {

        private static final String CACHE_NAME = "ddd4j-sample-order-idempotency";
        private final IdempotencyGuard guard;

        CacheKitOrderIdempotencyPort() {
            // CacheIdempotencyGuard 要求 CAS 缓存已显式注册到 CacheKit，此处按需注册本地 Caffeine 实现
            if (Objects.isNull(CacheKit.getCache(CACHE_NAME))) {
                CacheKit.register(CACHE_NAME, CaffeineCache.create(CacheConfig.builder(CACHE_NAME).build()));
            }
            this.guard = new CacheIdempotencyGuard(CACHE_NAME);
        }

        @Override
        public boolean acquire(String key, Duration ttl) {
            return guard.acquire(key, ttl);
        }

        @Override
        public void complete(String key, Object result) {
            guard.complete(key);
        }

        @Override
        public void release(String key) {
            guard.release(key);
        }
    }
}
