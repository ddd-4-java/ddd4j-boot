package io.ddd4j.boot.mq.sqs.config;

import io.ddd4j.boot.mq.sqs.autoconfigure.Ddd4jSqsMQAutoConfiguration;
import io.ddd4j.boot.mq.sqs.spi.SqsMQBrokerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot sqs 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j-boot sqs 的 {@link Ddd4jSqsMQAutoConfiguration}，
 * 提供 sqs 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(SqsMQBrokerAdapter.class)
@Import(Ddd4jSqsMQAutoConfiguration.class)
public class SqsMQBootAutoConfiguration {

}
