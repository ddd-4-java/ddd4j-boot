package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.mq.config.Ddd4jMQProperties;
import io.ddd4j.mq.publish.MQEventPublisher;
import io.ddd4j.mq.redisstream.RedisStreamMQBrokerAdapter;
import io.ddd4j.mq.redisstream.RedisStreamMQProperties;
import io.ddd4j.mq.serialization.JsonMQMessageSerialization;
import io.ddd4j.mq.serialization.MQEventSerialization;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Redis Stream Spring Boot auto-configuration.
 *
 * <p>The broker implementation stays in {@code ddd4j-mq-redis-stream}; this module
 * only binds Boot properties and exposes Spring beans.
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(RedisStreamMQBrokerAdapter.class)
public class RedisStreamMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.redis-stream")
    public RedisStreamMQProperties redisStreamMQProperties() {
        return new RedisStreamMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RedisStreamMQBrokerAdapter redisStreamMQBrokerAdapter(
            RedisStreamMQProperties redisStreamProperties,
            Ddd4jMQProperties mqProperties,
            ObjectProvider<MQEventSerialization> serialization) {
        MQEventSerialization eventSerialization = serialization.getIfAvailable(JsonMQMessageSerialization::new);
        return new RedisStreamMQBrokerAdapter(redisStreamProperties, mqProperties, eventSerialization, redisStreamProperties.newOperations());
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisStreamMQEventPublisher")
    public MQEventPublisher redisStreamMQEventPublisher(
            RedisStreamMQBrokerAdapter redisStreamMQBrokerAdapter,
            Ddd4jMQProperties mqProperties) {
        return redisStreamMQBrokerAdapter.createPublisher(mqProperties);
    }
}
