package io.ddd4j.boot.sample.order;

import io.ddd4j.cache.subject.InMemorySubject;
import io.ddd4j.cache.subject.InMemorySubjectProvider;
import io.ddd4j.core.subject.SubjectProvider;
import io.ddd4j.sample.order.application.OrderApplicationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 仅负责将共享业务内核连接到 Boot 示例的本地适配器。
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OrderSampleProperties.class)
public class OrderSampleConfiguration {

/**
 * 构造OrderSampleConfiguration对象（默认无参构造，字段由调用方逐个设置）。
 */
public OrderSampleConfiguration() {
}

    /**
     * 注册内存版订单适配器（仓储、读模型、事务、Outbox、幂等端口一体）。
     *
     * @return 内存版订单适配器
     */
    @Bean
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "infrastructure", havingValue = "in-memory",
            matchIfMissing = true)
    public InMemoryOrderAdapters orderAdapters() {
        return new InMemoryOrderAdapters();
    }

    /**
     * 以内存适配器组装订单应用服务（同实例承担仓储/读模型/事务/Outbox/幂等）。
     *
     * @param adapters 内存版订单适配器
     * @return 订单应用服务
     */
    @Bean
    @ConditionalOnProperty(prefix = "ddd4j.sample.order", name = "infrastructure", havingValue = "in-memory",
            matchIfMissing = true)
    public OrderApplicationService orderApplicationService(InMemoryOrderAdapters adapters) {
        return new OrderApplicationService(adapters, adapters, adapters, adapters, adapters);
    }

    /**
     * 本地示例的 Bearer Subject。生产应用应由 Sa-Token、Spring Security 或企业认证实现替换。
     *
     * @return 主题提供者
     */
    @Bean
    public SubjectProvider subjectProvider() {
        return new InMemorySubjectProvider(new InMemorySubject(event -> {
            // 示例仅验证 Subject SPI 与 Bearer 桥接，不额外持久化认证事件。
        }));
    }
}
