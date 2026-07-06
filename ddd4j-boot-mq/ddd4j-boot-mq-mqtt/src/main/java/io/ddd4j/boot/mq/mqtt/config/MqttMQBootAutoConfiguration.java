package io.ddd4j.boot.mq.mqtt.config;

import io.ddd4j.boot.mq.mqtt.autoconfigure.Ddd4jMqttMQAutoConfiguration;
import io.ddd4j.boot.mq.mqtt.spi.MqttBrokerAdapter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot mqtt 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j-boot mqtt 的 {@link Ddd4jMqttMQAutoConfiguration}，
 * 提供 mqtt 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(MqttBrokerAdapter.class)
@Import(Ddd4jMqttMQAutoConfiguration.class)
public class MqttMQBootAutoConfiguration {

}
