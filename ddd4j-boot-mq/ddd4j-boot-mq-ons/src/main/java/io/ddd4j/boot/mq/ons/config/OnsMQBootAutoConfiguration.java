package io.ddd4j.boot.mq.ons.config;

import io.ddd4j.boot.mq.ons.autoconfigure.Ddd4jOnsMQAutoConfiguration;
import io.ddd4j.boot.mq.ons.spi.OnsMQBrokerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot ons 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j-boot ons 的 {@link Ddd4jOnsMQAutoConfiguration}，
 * 提供 ons 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(OnsMQBrokerAdapter.class)
@Import(Ddd4jOnsMQAutoConfiguration.class)
public class OnsMQBootAutoConfiguration {

}
