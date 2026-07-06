package io.ddd4j.boot.mq.rocketmq.autoconfigure;

import io.ddd4j.boot.mq.rocketmq.publisher.RocketEventPublisher;
import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.rocketmq.consumer.RocketMQConsumerEndpointRegistrar;
import io.ddd4j.mq.rocketmq.spi.RocketBrokerAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=rocket 时生效。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jRocketMQAutoConfiguration {

    /**
     * 注册 RocketMQ 消费端点编排器。
     */
    @Bean
    public RocketMQConsumerEndpointRegistrar rocketMQConsumerEndpointRegistrar(
            ApplicationContext applicationContext,
            MQProperties properties) {
        return new RocketMQConsumerEndpointRegistrar(applicationContext, properties);
    }

    /**
     * 注册 RocketMQ Broker 适配器。
     */
    @Bean
    public RocketBrokerAdapter rocketBrokerAdapter(
            MQProperties properties,
            RocketMQConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new RocketBrokerAdapter(properties, consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public EventPublisher rocketEventPublisher(
            MQProperties properties) {
        return new RocketEventPublisher(rocketMQTemplate, properties);
    }
}
