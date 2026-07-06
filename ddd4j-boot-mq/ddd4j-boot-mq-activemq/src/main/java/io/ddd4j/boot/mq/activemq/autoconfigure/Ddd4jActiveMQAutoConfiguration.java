package io.ddd4j.boot.mq.activemq.autoconfigure;

import io.ddd4j.boot.mq.activemq.publisher.ActiveEventPublisher;
import io.ddd4j.mq.activemq.consumer.ActiveMQConsumerEndpointRegistrar;
import io.ddd4j.mq.activemq.spi.ActiveBrokerAdapter;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.publish.EventPublisher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsTemplate;

/**
 * ActiveMQ Artemis 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=activemq 时生效。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jActiveMQAutoConfiguration {

    /**
     * 注册 ActiveMQ 消费端点编排器。
     */
    @Bean
    public ActiveMQConsumerEndpointRegistrar activeMQConsumerEndpointRegistrar(
            ApplicationContext applicationContext,
            JmsListenerEndpointRegistry endpointRegistry,
            MQProperties properties) {
        return new ActiveMQConsumerEndpointRegistrar(applicationContext, endpointRegistry, properties);
    }

    /**
     * 注册 ActiveMQ Broker 适配器。
     */
    @Bean
    public ActiveBrokerAdapter activeBrokerAdapter(
            MQProperties properties,
            ActiveMQConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new ActiveBrokerAdapter(properties, consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public EventPublisher activeEventPublisher(
            JmsTemplate jmsTemplate,
            MQProperties properties) {
        return new ActiveEventPublisher(jmsTemplate, properties);
    }
}
