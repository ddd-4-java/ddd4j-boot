package io.ddd4j.boot.mq.pulsar.config;

import io.ddd4j.mq.pulsar.autoconfigure.Ddd4jPulsarMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot pulsar 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link PulsarMQ}，
 * 提供 pulsar 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(PulsarMQ.class)
@Import(PulsarMQ.class)
public class PulsarMQBootAutoConfiguration {

}
