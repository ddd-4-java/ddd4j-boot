package io.ddd4j.boot.sample.order.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 订单领域事件配置
 */
@Configuration
@EnableAsync
public class OrderDomainEventConfig {
    // 启用异步事件处理
}

