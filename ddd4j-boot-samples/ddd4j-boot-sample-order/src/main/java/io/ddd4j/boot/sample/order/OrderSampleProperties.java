package io.ddd4j.boot.sample.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 共享 Order 示例的运行配置。
 *
 * <p>默认使用内存适配器；生产演示通过 {@code infrastructure=postgres} 同时启用 PostgreSQL、Redis、Kafka
 * 和事务 Outbox。
 */
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

    public String getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    public void setRedisEnabled(boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
    }

    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }

    public void setKafkaEnabled(boolean kafkaEnabled) {
        this.kafkaEnabled = kafkaEnabled;
    }

    public boolean isOutboxSchedulerEnabled() {
        return outboxSchedulerEnabled;
    }

    public void setOutboxSchedulerEnabled(boolean outboxSchedulerEnabled) {
        this.outboxSchedulerEnabled = outboxSchedulerEnabled;
    }

    public int getOutboxBatchSize() {
        return outboxBatchSize;
    }

    public void setOutboxBatchSize(int outboxBatchSize) {
        this.outboxBatchSize = outboxBatchSize;
    }

    public long getOutboxDelayMillis() {
        return outboxDelayMillis;
    }

    public void setOutboxDelayMillis(long outboxDelayMillis) {
        this.outboxDelayMillis = outboxDelayMillis;
    }
}
