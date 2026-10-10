package io.ddd4j.boot.sample.setup.config;

import org.dromara.mica.mqtt.core.client.MqttClient;
import org.dromara.mica.mqtt.core.client.MqttClientCreator;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 多 MQTT 客户端接入配置。
 * <p>
 * 演示在默认客户端之外再注册一个独立命名的客户端模板
 * {@code mqttClientTemplate1}，用于连接外部 MQTT 服务端。
 * </p>
 */
@Configuration
public class OtherMqttClientConfiguration {

    /**
     * 构造多客户端配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public OtherMqttClientConfiguration() {
    }

    /**
     * 创建第二个 MQTT 客户端模板，携带独立的服务端地址与鉴权信息。
     *
     * @return 以 {@code mqttClientTemplate1} 命名的客户端模板
     */
    @Bean("mqttClientTemplate1")
    public MqttClientTemplate mqttClientTemplate1() {
        MqttClientCreator mqttClientCreator1 = MqttClient.create()
                .ip("mqtt.dreamlu.net")
                .username("mica")
                .password("mica");
        return new MqttClientTemplate(mqttClientCreator1);
    }

}
