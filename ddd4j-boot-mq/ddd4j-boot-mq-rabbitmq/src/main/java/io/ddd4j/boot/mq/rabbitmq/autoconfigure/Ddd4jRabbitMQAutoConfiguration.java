package io.ddd4j.boot.mq.rabbitmq.autoconfigure;

import io.ddd4j.boot.mq.rabbitmq.publisher.RabbitMQEventPublisher;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.event.MQEventPublisher;
import io.ddd4j.mq.rabbitmq.consumer.RabbitMQConsumerEndpointRegistrar;
import io.ddd4j.mq.rabbitmq.spi.RabbitBrokerAdapter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=rabbit 时生效。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jRabbitMQAutoConfiguration {

    /**
     * 注册 Rabbit 消费端点编排器。
     */
    @Bean
    public RabbitMQConsumerEndpointRegistrar rabbitMQConsumerEndpointRegistrar(
            ApplicationContext applicationContext,
            RabbitListenerEndpointRegistry endpointRegistry,
            MQProperties properties) {
        return new RabbitMQConsumerEndpointRegistrar(applicationContext, endpointRegistry, properties);
    }

    /**
     * 注册 RabbitMQ Broker 适配器。
     */
    @Bean
    public RabbitBrokerAdapter rabbitBrokerAdapter(
            MQProperties properties,
            RabbitMQConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new RabbitBrokerAdapter(properties, consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public MQEventPublisher rabbitEventPublisher(
            RabbitTemplate rabbitTemplate,
            MQProperties properties) {
        return new RabbitMQEventPublisher(rabbitTemplate, properties);
    }
}
