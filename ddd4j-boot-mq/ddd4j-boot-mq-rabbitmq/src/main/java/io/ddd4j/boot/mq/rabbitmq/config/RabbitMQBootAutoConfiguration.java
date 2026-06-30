package io.ddd4j.boot.mq.rabbitmq.config;

import io.ddd4j.boot.mq.rabbitmq.autoconfigure.Ddd4jRabbitMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(Ddd4jRabbitMQAutoConfiguration.class)
public class RabbitMQBootAutoConfiguration {
}
