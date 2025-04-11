package io.hiwepy.boot.sample.setup.config;

import org.dromara.mica.mqtt.core.client.MqttClient;
import org.dromara.mica.mqtt.core.client.MqttClientCreator;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtherMqttClientConfiguration {

    @Bean("mqttClientTemplate1")
    public MqttClientTemplate mqttClientTemplate1() {
        MqttClientCreator mqttClientCreator1 = MqttClient.create()
                .ip("mqtt.dreamlu.net")
                .username("mica")
                .password("mica");
        return new MqttClientTemplate(mqttClientCreator1);
    }

}