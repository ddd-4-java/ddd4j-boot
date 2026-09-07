package io.ddd4j.boot.mq.mqtt;

import io.ddd4j.mq.mqtt.MqttMQClient;
import io.ddd4j.mq.mqtt.MqttMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(MqttMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "mqtt")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class MqttMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.mqtt")
    public MqttMQProperties mqttMQProperties() {
        return new MqttMQProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public MqttMQClient mqttMQClient(MqttMQProperties properties) {
        return new MqttMQClient(properties);
    }
}