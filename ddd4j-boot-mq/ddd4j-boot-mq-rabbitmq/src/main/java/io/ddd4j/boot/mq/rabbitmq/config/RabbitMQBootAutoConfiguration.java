package io.ddd4j.boot.mq.rabbitmq.config;

import io.ddd4j.mq.rabbit.autoconfigure.Ddd4jRabbitMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot raUbbitmq 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link RabbitMQ}，
 * 提供 raUbbitmq 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(RabbitMQ.class)
@Import(RabbitMQ.class)
public class RabbitMQBootAutoConfiguration {

}
