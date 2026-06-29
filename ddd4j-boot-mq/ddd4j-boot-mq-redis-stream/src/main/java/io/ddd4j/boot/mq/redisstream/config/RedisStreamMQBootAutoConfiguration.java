package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.mq.redisstream.autoconfigure.Ddd4jRedisStreamMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot redis stream 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link RedisStreamMQ}，
 * 提供 redis stream 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(RedisStreamMQ.class)
@Import(RedisStreamMQ.class)
public class RedisStreamMQBootAutoConfiguration {

}
