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

/**
 * 构造OrderPostgresInfrastructureConfiguration对象（默认无参构造，字段由调用方逐个设置）。
 */
public OrderPostgresInfrastructureConfiguration() {
}

    private static final Logger log = LoggerFactory.getLogger(OrderPostgresInfrastructureConfiguration.class);

    /**
     * 注册基于 JDBC 的订单事务端口。
     *
     * @param dataSource 数据源
     * @return JDBC 订单事务端口
     */
    @Bean
    public JdbcOrderTransactionPort orderTransactionPort(DataSource dataSource) {
        return new JdbcOrderTransactionPort(dataSource);
    }

    /**
     * 注册基于 JDBC 的订单仓储。
     *
     * @param transaction JDBC 事务端口
     * @return 订单仓储
     */
    @Bean
    public OrderRepository orderRepository(JdbcOrderTransactionPort transaction) {
        return new JdbcOrderRepository(transaction);
    }

    /**
     * 注册基于 JDBC 的事务 Outbox 端口。
     *
     * @param transaction  JDBC 事务端口
     * @param objectMapper JSON 序列化器
     * @return Outbox 端口
     */
    @Bean
    public OutboxPort orderOutboxPort(JdbcOrderTransactionPort transaction, ObjectMapper objectMapper) {
        return new JdbcOutboxPort(transaction, objectMapper);
    }

    /**
     * 注册基于 JDBC 的订单读模型端口。
     *
     * @param transaction JDBC 事务端口
     * @return 订单读模型端口
     */
    @Bean
    public OrderReadModelPort orderReadModelPort(JdbcOrderTransactionPort transaction) {
        return new JdbcOrderReadModelPort(transaction);
    }

    /**
     * 注册 Redis 客户端（JedisPooled），Bean 销毁时自动 close。
     *
     * @param properties 示例配置属性
     * @return Redis 客户端
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(UnifiedJedis.class)
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "redis-enabled", havingValue = "true",
            matchIfMissing = true)
    public UnifiedJedis orderRedisClient(OrderSampleProperties properties) {
        return new JedisPooled(properties.getRedisHost(), properties.getRedisPort());
    }

    /**
     * 注册幂等端口：Redis 可用时使用 Redis 幂等实现，否则降级到本地 CacheKit 兜底。
     *
     * @param redisClientProvider Redis 客户端提供者（可能缺失）
     * @return 幂等端口
     */
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

    /**
     * 注册 Kafka 生产者（acks=all、开启幂等），Bean 销毁时自动 close。
     *
     * @param properties 示例配置属性
     * @return Kafka 生产者
     */
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

    /**
     * 注册基于 Kafka 的集成事件发布器。
     *
     * @param orderKafkaProducer Kafka 生产者
     * @param objectMapper       JSON 序列化器
     * @param properties         示例配置属性
     * @return 集成事件发布器
     */
    @Bean
    @ConditionalOnMissingBean(IntegrationEventPublisher.class)
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "kafka-enabled", havingValue = "true",
            matchIfMissing = true)
    public IntegrationEventPublisher orderIntegrationEventPublisher(Producer<String, String> orderKafkaProducer,
                                                                     ObjectMapper objectMapper,
                                                                     OrderSampleProperties properties) {
        return new KafkaIntegrationEventPublisher(orderKafkaProducer, objectMapper, properties.getKafkaTopic());
    }

    /**
     * 注册 Outbox 发布器：Kafka 禁用时降级为不可用发布器，消息保留待重试。
     *
     * @param orderOutboxPort   Outbox 端口
     * @param publisherProvider 集成事件发布器提供者（可能缺失）
     * @return Outbox 发布器
     */
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

    /**
     * 注册事务 Outbox 发布器（随本地事务提交后发布）。
     *
     * @param transaction          JDBC 事务端口
     * @param orderOutboxPublisher Outbox 发布器
     * @return 事务 Outbox 发布器
     */
    @Bean
    public TransactionalOutboxPublisher transactionalOutboxPublisher(JdbcOrderTransactionPort transaction,
                                                                      OutboxPublisher orderOutboxPublisher) {
        return new TransactionalOutboxPublisher(transaction, orderOutboxPublisher);
    }

    /**
     * 注册 Outbox 定时调度器（周期发布待发送事件）。
     *
     * @param transactionalOutboxPublisher 事务 Outbox 发布器
     * @param properties                   示例配置属性
     * @return Outbox 调度器
     */
    @Bean
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "outbox-scheduler-enabled", havingValue = "true",
            matchIfMissing = true)
    public OrderOutboxScheduler orderOutboxScheduler(TransactionalOutboxPublisher transactionalOutboxPublisher,
                                                      OrderSampleProperties properties) {
        return new OrderOutboxScheduler(transactionalOutboxPublisher, properties);
    }

    /**
     * 组装订单应用服务（仓储、Outbox、读模型、幂等、事务端口）。
     *
     * @param orderRepository      订单仓储
     * @param orderOutboxPort      Outbox 端口
     * @param orderReadModelPort   订单读模型端口
     * @param orderIdempotencyPort 幂等端口
     * @param orderTransactionPort 事务端口
     * @return 订单应用服务
     */
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

        /** 幂等缓存名称 */
        private static final String CACHE_NAME = "ddd4j-sample-order-idempotency";
        /** 幂等状态机守卫 */
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
