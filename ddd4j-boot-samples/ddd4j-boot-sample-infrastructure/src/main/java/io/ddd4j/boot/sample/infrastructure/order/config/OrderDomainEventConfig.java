package io.ddd4j.boot.sample.infrastructure.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 订单领域事件配置
 *
 * <p>为订单限界上下文开启 Spring 异步事件处理（{@code @EnableAsync}），
 * 使 {@code @EventListener} + {@code @Async} 的领域事件监听器在独立线程池中执行，
 * 避免通知、库存等副作用阻塞交易主链路。</p>
 */
@Configuration
@EnableAsync
public class OrderDomainEventConfig {

    /**
     * 构造订单领域事件配置类，仅承载 {@code @EnableAsync} 注解，无成员。
     */
    public OrderDomainEventConfig() {
    }
    // 启用异步事件处理
}

