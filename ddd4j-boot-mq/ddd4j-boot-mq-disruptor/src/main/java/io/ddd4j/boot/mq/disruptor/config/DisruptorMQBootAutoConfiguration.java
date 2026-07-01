package io.ddd4j.boot.mq.disruptor.config;

import io.ddd4j.boot.mq.disruptor.autoconfigure.Ddd4jDisruptorMQAutoConfiguration;
import io.ddd4j.mq.disruptor.spi.DisruptorMQBrokerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot disruptor 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j-boot disruptor 的 {@link Ddd4jDisruptorMQAutoConfiguration}，
 * 提供 disruptor 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(DisruptorMQBrokerAdapter.class)
@Import(Ddd4jDisruptorMQAutoConfiguration.class)
public class DisruptorMQBootAutoConfiguration {

}
