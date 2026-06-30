package io.ddd4j.boot.mq.rocketmq.config;

import io.ddd4j.boot.mq.rocketmq.autoconfigure.Ddd4jRocketMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(Ddd4jRocketMQAutoConfiguration.class)
public class RocketMQBootAutoConfiguration {
}
