package io.ddd4j.boot.core;

import io.ddd4j.core.context.Contexts;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import io.ddd4j.spring.config.SpringCoreConfig;
import io.ddd4j.spring.context.SpringContext;
import io.ddd4j.spring.context.SpringContextBridge;
import io.ddd4j.spring.event.SpringDomainEventPublisher;

/**
 * ddd4j 核心 SPI 生命周期自动配置。
 *
 * <p>导入 Spring Runtime 已验证的 {@link SpringContextBridge}，将 Spring 容器中的 SPI Bean
 * 注册到 ddd4j 上下文，并在关闭时恢复前序全局状态。Boot 只负责自动装配，不重复维护另一套静态注册逻辑。
 *
 * <p>导入上游 {@link SpringCoreConfig} 获取基础 Bean，本类负责 SPI 注册。
 *
 * @author wandl
 * @since 3.4.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Contexts.class, SpringContext.class})
@Import({SpringCoreConfig.class, SpringDomainEventPublisher.class, SpringContextBridge.class})
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class Ddd4jCoreAutoConfiguration {
}
