package io.ddd4j.boot.mq.tdmq.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot tdmq 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link TdmqMQ}，
 * 提供 tdmq 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(TdmqMQ.class)
@Import(TdmqMQ.class)
public class TdmqMQBootAutoConfiguration {

}
