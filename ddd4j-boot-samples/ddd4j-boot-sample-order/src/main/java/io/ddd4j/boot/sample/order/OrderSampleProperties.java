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

/**
 * 构造OrderSampleProperties对象（默认无参构造，字段由调用方逐个设置）。
 */
public OrderSampleProperties() {
}

    /** 基础设施模式（in-memory / postgres） */
    private String infrastructure = "in-memory";

    /** Kafka 引导服务器地址 */
    private String kafkaBootstrapServers = "localhost:9092";

    /** Kafka 事件主题名 */
    private String kafkaTopic = "ddd4j.sample.order.events";

    /** Redis 主机地址 */
    private String redisHost = "localhost";

    /** Redis 端口 */
    private int redisPort = 6379;

    /** 是否启用 Redis */
    private boolean redisEnabled = true;

    /** 是否启用 Kafka */
    private boolean kafkaEnabled = true;

    /** 是否启用 Outbox 定时调度器 */
    private boolean outboxSchedulerEnabled = true;

    /** Outbox 单次批量发布条数 */
    private int outboxBatchSize = 100;

    /** Outbox 调度延迟（毫秒） */
    private long outboxDelayMillis = 5000L;

    /** 获取基础设施模式（in-memory / postgres）。
     * @return 基础设施模式（in-memory / postgres） */
    public String getInfrastructure() {
        return infrastructure;
    }

    /** 设置基础设施模式（in-memory / postgres）。
     * @param infrastructure 基础设施模式（in-memory / postgres） */
    public void setInfrastructure(String infrastructure) {
        this.infrastructure = infrastructure;
    }

    /** 获取Kafka 引导服务器地址。
     * @return Kafka 引导服务器地址 */
    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    /** 设置Kafka 引导服务器地址。
     * @param kafkaBootstrapServers Kafka 引导服务器地址 */
    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }

    /** 获取Kafka 事件主题名。
     * @return Kafka 事件主题名 */
    public String getKafkaTopic() {
        return kafkaTopic;
    }

    /** 设置Kafka 事件主题名。
     * @param kafkaTopic Kafka 事件主题名 */
    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }

    /** 获取Redis 主机地址。
     * @return Redis 主机地址 */
    public String getRedisHost() {
        return redisHost;
    }

    /** 设置Redis 主机地址。
     * @param redisHost Redis 主机地址 */
    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    /** 获取Redis 端口。
     * @return Redis 端口 */
    public int getRedisPort() {
        return redisPort;
    }

    /** 设置Redis 端口。
     * @param redisPort Redis 端口 */
    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    /** 获取是否启用 Redis。
     * @return 是否启用 Redis */
    public boolean isRedisEnabled() {
        return redisEnabled;
    }

    /** 设置是否启用 Redis。
     * @param redisEnabled 是否启用 Redis */
    public void setRedisEnabled(boolean redisEnabled) {
        this.redisEnabled = redisEnabled;
    }

    /** 获取是否启用 Kafka。
     * @return 是否启用 Kafka */
    public boolean isKafkaEnabled() {
        return kafkaEnabled;
    }

    /** 设置是否启用 Kafka。
     * @param kafkaEnabled 是否启用 Kafka */
    public void setKafkaEnabled(boolean kafkaEnabled) {
        this.kafkaEnabled = kafkaEnabled;
    }

    /** 获取是否启用 Outbox 定时调度器。
     * @return 是否启用 Outbox 定时调度器 */
    public boolean isOutboxSchedulerEnabled() {
        return outboxSchedulerEnabled;
    }

    /** 设置是否启用 Outbox 定时调度器。
     * @param outboxSchedulerEnabled 是否启用 Outbox 定时调度器 */
    public void setOutboxSchedulerEnabled(boolean outboxSchedulerEnabled) {
        this.outboxSchedulerEnabled = outboxSchedulerEnabled;
    }

    /** 获取Outbox 单次批量发布条数。
     * @return Outbox 单次批量发布条数 */
    public int getOutboxBatchSize() {
        return outboxBatchSize;
    }

    /** 设置Outbox 单次批量发布条数。
     * @param outboxBatchSize Outbox 单次批量发布条数 */
    public void setOutboxBatchSize(int outboxBatchSize) {
        this.outboxBatchSize = outboxBatchSize;
    }

    /** 获取Outbox 调度延迟（毫秒）。
     * @return Outbox 调度延迟（毫秒） */
    public long getOutboxDelayMillis() {
        return outboxDelayMillis;
    }

    /** 设置Outbox 调度延迟（毫秒）。
     * @param outboxDelayMillis Outbox 调度延迟（毫秒） */
    public void setOutboxDelayMillis(long outboxDelayMillis) {
        this.outboxDelayMillis = outboxDelayMillis;
    }
}
