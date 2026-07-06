package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.redisstream.RedisStreamBrokerAdapter;
import io.ddd4j.mq.redisstream.RedisStreamMQProperties;
import io.ddd4j.mq.serialization.JsonSerialization;
import io.ddd4j.mq.serialization.EventSerialization;
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
@ConditionalOnClass(RedisStreamBrokerAdapter.class)
public class RedisStreamMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.redis-stream")
    public RedisStreamMQProperties redisStreamMQProperties() {
        return new RedisStreamMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RedisStreamBrokerAdapter redisStreamBrokerAdapter(
            RedisStreamMQProperties redisStreamProperties,
            MQProperties mqProperties,
            ObjectProvider<EventSerialization> serialization) {
        EventSerialization eventSerialization = serialization.getIfAvailable(JsonSerialization::new);
        return new RedisStreamBrokerAdapter(redisStreamProperties, mqProperties, eventSerialization, redisStreamProperties.newOperations());
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisStreamEventPublisher")
    public EventPublisher redisStreamEventPublisher(
            RedisStreamBrokerAdapter redisStreamBrokerAdapter,
            MQProperties mqProperties) {
        return redisStreamBrokerAdapter.createPublisher(mqProperties);
    }
}
