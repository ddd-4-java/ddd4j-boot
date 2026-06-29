package io.ddd4j.boot.mq.mqttmica.config;

import io.ddd4j.mq.mqtt.mica.autoconfigure.Ddd4jMicaMqttMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot mqtt mica 自动配置。
 *
 * <p>通过 {@link Import} 导入 ddd4j 库的 {@link MicaMqttMQ}，
 * 提供 mqtt mica 消息队列的 Spring Boot 自动配置支持。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@AutoConfiguration
@ConditionalOnClass(MicaMqttMQ.class)
@Import(MicaMqttMQ.class)
public class MicaMqttMQBootAutoConfiguration {

}
