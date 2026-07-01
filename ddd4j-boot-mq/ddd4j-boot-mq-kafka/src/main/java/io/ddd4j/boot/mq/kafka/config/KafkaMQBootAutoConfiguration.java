package io.ddd4j.boot.mq.kafka.config;

import io.ddd4j.boot.mq.kafka.mq.Ddd4jKafkaMQAutoConfiguration;
import io.ddd4j.boot.mq.kafka.mq.KafkaMQBrokerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot kafka 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j-boot kafka 的 {@link Ddd4jKafkaMQAutoConfiguration}，
 * 提供 kafka 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(KafkaMQBrokerAdapter.class)
@Import(Ddd4jKafkaMQAutoConfiguration.class)
public class KafkaMQBootAutoConfiguration {

}
