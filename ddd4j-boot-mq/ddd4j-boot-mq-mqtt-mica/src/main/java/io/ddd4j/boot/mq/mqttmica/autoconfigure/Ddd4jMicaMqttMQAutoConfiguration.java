package io.ddd4j.boot.mq.mqttmica.autoconfigure;

import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.boot.mq.mqttmica.config.Ddd4jMicaMqttProperties;
import io.ddd4j.boot.mq.mqttmica.consumer.MicaMqttMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.mqttmica.publisher.MicaMqttEventPublisher;
import io.ddd4j.boot.mq.mqttmica.spi.MicaMqttBrokerAdapter;
import io.ddd4j.mq.publish.EventPublisher;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mica-mqtt 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=mqtt-mica 时生效。
 * <p>
 * 主路径：mica-mqtt client（与 sample mqtt-client2 一致），连接参数走 {@code mqtt.client.*}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jMicaMqttMQAutoConfiguration {

    /**
     * 注册 mica-mqtt 消费端点编排器。
     */
    @Bean(destroyMethod = "close")
    public MicaMqttMQConsumerEndpointRegistrar micaMqttMQConsumerEndpointRegistrar(
            MqttClientTemplate mqttClientTemplate,
            MQProperties mqProperties,
            Ddd4jMicaMqttProperties micaMqttProperties) {
        return new MicaMqttMQConsumerEndpointRegistrar(mqttClientTemplate, mqProperties, micaMqttProperties);
    }

    /**
     * 注册 mica-mqtt Broker 适配器。
     */
    @Bean
    public MicaMqttBrokerAdapter micaMqttBrokerAdapter(
            MqttClientTemplate mqttClientTemplate,
            MQProperties mqProperties,
            Ddd4jMicaMqttProperties micaMqttProperties,
            MicaMqttMQConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new MicaMqttBrokerAdapter(
                mqttClientTemplate, mqProperties, micaMqttProperties.getQos(), consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public EventPublisher micaMqttEventPublisher(
            MqttClientTemplate mqttClientTemplate,
            MQProperties mqProperties,
            Ddd4jMicaMqttProperties micaMqttProperties) {
        return new MicaMqttEventPublisher(mqttClientTemplate, mqProperties, micaMqttProperties.getQos());
    }
}
