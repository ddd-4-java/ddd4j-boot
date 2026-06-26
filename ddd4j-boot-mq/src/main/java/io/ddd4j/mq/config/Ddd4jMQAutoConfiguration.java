package io.ddd4j.mq.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Ddd4j MQ 自动配置入口。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-mq / Ddd4jMQAutoConfiguration。
 * 本配置只发布 Port/契约层 Bean；具体 broker 由 ddd4j-boot-extensions 下的 mq-* 子模块按需引入。
 */
@Configuration
@EnableConfigurationProperties(Ddd4jMQProperties.class)
public class Ddd4jMQAutoConfiguration {

}
