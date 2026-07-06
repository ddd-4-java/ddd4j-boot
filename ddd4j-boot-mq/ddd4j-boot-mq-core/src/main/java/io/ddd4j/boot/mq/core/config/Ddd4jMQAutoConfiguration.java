package io.ddd4j.boot.mq.core.config;

import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.consume.ConsumerInterceptor;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.listener.ListenerDefinitionRegistry;
import io.ddd4j.mq.listener.ListenerScanner;
import io.ddd4j.mq.serialization.JsonSerialization;
import io.ddd4j.mq.serialization.EventSerialization;
import io.ddd4j.mq.serialization.MessageSerialization;
import io.ddd4j.mq.spi.BrokerAdapter;
import io.ddd4j.mq.spi.BrokerAdapters;
import io.ddd4j.mq.spring.registry.MQListenerBeanPostProcessor;
import io.ddd4j.mq.spring.registry.MQListenerRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * ddd4j 消息队列自动配置（契约层）：注册属性、发布器与监听器编排。
 */
@Configuration
@EnableConfigurationProperties(MQProperties.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "enabled", havingValue = "true")
public class Ddd4jMQAutoConfiguration {

    /**
     * 默认 JSON 序列化 Bean。
     */
    @Bean
    @ConditionalOnMissingBean(MessageSerialization.class)
    public MessageSerialization mqMessageSerialization() {
        return new JsonSerialization();
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    @ConditionalOnBean(BrokerAdapter.class)
    @ConditionalOnMissingBean(EventPublisher.class)
    public EventPublisher mqEventPublisher(List<BrokerAdapter> adapters, MQProperties props) {
        return BrokerAdapters.createPublisher(adapters, props);
    }

    /**
     * 监听器定义注册表（由 BeanPostProcessor 填充）。
     */
    @Bean
    @ConditionalOnBean(BrokerAdapter.class)
    public ListenerDefinitionRegistry mqListenerDefinitionRegistry() {
        return new ListenerDefinitionRegistry();
    }

    /**
     * 基于 BeanPostProcessor 发现 {@code @EventListener} 方法。
     */
    @Bean
    @ConditionalOnBean(BrokerAdapter.class)
    public MQListenerBeanPostProcessor mqListenerBeanPostProcessor(
            ListenerDefinitionRegistry registry,
            MQProperties props) {
        return new MQListenerBeanPostProcessor(registry, props);
    }

    /**
     * 监听器定义访问门面（读取 Registry）。
     */
    @Bean
    @ConditionalOnBean(BrokerAdapter.class)
    public ListenerScanner mqListenerScanner(ListenerDefinitionRegistry registry) {
        return new ListenerScanner(registry);
    }

    /**
     * 应用就绪后动态注册消费端点到 {@link BrokerAdapter}。
     */
    @Bean
    @ConditionalOnBean(BrokerAdapter.class)
    public MQListenerRegistrar mqListenerRegistrar(
            ListenerScanner scanner,
            List<BrokerAdapter> adapters,
            MQProperties props,
            ObjectProvider<EventSerialization> serializationProvider,
            ObjectProvider<ConsumerInterceptor> interceptorsProvider) {

        EventSerialization serialization = serializationProvider.getIfAvailable(JsonSerialization::new);
        List<ConsumerInterceptor> interceptors = interceptorsProvider.orderedStream().toList();
        return new MQListenerRegistrar(scanner, adapters, props, serialization, interceptors);
    }
}
