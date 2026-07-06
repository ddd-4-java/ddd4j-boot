package io.ddd4j.boot.mq.pulsar.autoconfigure;

import io.ddd4j.boot.mq.pulsar.publisher.PulsarMQEventPublisher;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.event.MQEventPublisher;
import io.ddd4j.mq.pulsar.consumer.PulsarConsumerEndpointRegistrar;
import io.ddd4j.mq.pulsar.spi.PulsarBrokerAdapter;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Pulsar 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=pulsar 时生效。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jPulsarMQAutoConfiguration {

    /**
     * 注册 Pulsar 消费端点编排器（依赖 Spring Boot 提供的 {@link PulsarClient}）。
     */
    @Bean
    public PulsarConsumerEndpointRegistrar pulsarConsumerEndpointRegistrar(
            ApplicationContext applicationContext,
            MQProperties properties) {
        return new PulsarConsumerEndpointRegistrar(applicationContext, properties);
    }

    /**
     * 注册 Pulsar Broker 适配器。
     */
    @Bean
    public PulsarBrokerAdapter pulsarBrokerAdapter(
            MQProperties properties,
            PulsarConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new PulsarBrokerAdapter(properties, consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public MQEventPublisher pulsarEventPublisher(
            MQProperties properties) {
        return new PulsarMQEventPublisher(pulsarTemplate, properties);
    }
}
