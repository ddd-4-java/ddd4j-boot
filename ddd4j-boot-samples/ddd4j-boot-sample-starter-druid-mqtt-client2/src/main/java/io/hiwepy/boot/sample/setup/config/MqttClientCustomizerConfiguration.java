package io.hiwepy.boot.sample.setup.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.core.client.MqttClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class MqttClientCustomizerConfiguration {

    @Bean
    public MqttClientCustomizer mqttClientCustomizer() {
        return creator -> {
            // 此处可自定义配置 creator，会覆盖 yml 中的配置
            log.info("----------------MqttServerCustomizer-----------------");
        };
    }

}
