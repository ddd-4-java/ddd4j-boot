package io.ddd4j.boot.mq.mqttmica.config;

import io.ddd4j.mq.mqttmica.MicaMqttMQClient;
import io.ddd4j.mq.mqttmica.MicaMqttProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot mqtt mica 自动配置（薄适配）。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(MicaMqttMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "mqtt-mica")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class MicaMqttMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.mqtt-mica")
    public MicaMqttProperties micaMqttProperties() {
        return new MicaMqttProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public MicaMqttMQClient micaMqttMQClient(MicaMqttProperties properties) {
        return new MicaMqttMQClient(properties);
    }
}
