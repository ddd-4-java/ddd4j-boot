package io.ddd4j.boot.mq.sqs.config;

import io.ddd4j.mq.sqs.autoconfigure.Ddd4jSqsMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot sqs 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link SqsMQ}，
 * 提供 sqs 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(SqsMQ.class)
@Import(SqsMQ.class)
public class SqsMQBootAutoConfiguration {

}
