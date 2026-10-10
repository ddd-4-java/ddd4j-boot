package io.ddd4j.boot.mq.mqtt;

import io.ddd4j.mq.mqtt.MqttMQClient;
import io.ddd4j.mq.mqtt.MqttMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * MQTT 消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅当 {@code ddd4j.mq.broker=mqtt} 且 {@link MqttMQClient} 类存在时生效，
 * 并通过 {@link Ddd4jMQRegistrarConfiguration} 导入 MQ 通用注册配置。
 */
@AutoConfiguration
@ConditionalOnClass(MqttMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "mqtt")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class MqttMQBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public MqttMQBootAutoConfiguration() {
    }

    /**
     * 装配 MQTT 配置属性，绑定 {@code ddd4j.mq.mqtt.*}。
     *
     * @return MQTT 配置属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.mqtt")
    public MqttMQProperties mqttMQProperties() {
        return new MqttMQProperties();
    }

    /**
     * 装配 MQTT 消息客户端。
     *
     * @param properties MQTT 配置属性
     * @return MQTT 客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MqttMQClient mqttMQClient(MqttMQProperties properties) {
        return new MqttMQClient(properties);
    }
}