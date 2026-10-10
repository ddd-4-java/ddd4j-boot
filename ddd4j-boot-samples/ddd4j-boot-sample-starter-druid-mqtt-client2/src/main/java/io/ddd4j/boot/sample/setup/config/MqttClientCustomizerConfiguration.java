package io.ddd4j.boot.sample.setup.config;

import org.dromara.mica.mqtt.core.client.MqttClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mica-mqtt 客户端自定义配置。
 * <p>
 * 声明 {@link MqttClientCustomizer} Bean，在客户端创建阶段覆盖 yml 中的默认配置，
 * 同时以 {@code proxyBeanMethods = false} 关闭配置类方法互调，减少容器内方法级代理。
 * </p>
 */
@Configuration(proxyBeanMethods = false)
public class MqttClientCustomizerConfiguration {

    /**
     * 构造客户端自定义配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttClientCustomizerConfiguration() {
    }

    /**
     * 注册客户端创建回调，供业务侧按需调整 {@code creator} 上的连接参数。
     *
     * @return 执行自定义逻辑的 MQTT 客户端定制器
     */
    @Bean
    public MqttClientCustomizer mqttClientCustomizer() {
        return creator -> {
            // 此处可自定义配置 creator，会覆盖 yml 中的配置
            System.out.println("----------------MqttServerCustomizer-----------------");
        };
    }

}
