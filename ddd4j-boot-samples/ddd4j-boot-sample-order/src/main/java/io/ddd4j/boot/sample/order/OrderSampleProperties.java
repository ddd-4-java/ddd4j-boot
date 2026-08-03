package io.ddd4j.boot.sample.order;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 共享 Order 示例的运行配置。
 *
 * <p>默认使用内存适配器；生产演示通过 {@code infrastructure=postgres} 同时启用 PostgreSQL、Redis、Kafka
 * 和事务 Outbox。
 */
@Data
@ConfigurationProperties(prefix = "ddd4j.sample.order")
public class OrderSampleProperties {

    private String infrastructure = "in-memory";

    private String kafkaBootstrapServers = "localhost:9092";

    private String kafkaTopic = "ddd4j.sample.order.events";

    private String redisHost = "localhost";

    private int redisPort = 6379;

    private boolean redisEnabled = true;

    private boolean kafkaEnabled = true;

    private boolean outboxSchedulerEnabled = true;

    private int outboxBatchSize = 100;

    private long outboxDelayMillis = 5000L;
}
