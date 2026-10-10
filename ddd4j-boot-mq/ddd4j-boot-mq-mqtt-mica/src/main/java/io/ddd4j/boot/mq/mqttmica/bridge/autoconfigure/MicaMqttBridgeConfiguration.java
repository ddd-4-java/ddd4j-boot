package io.ddd4j.boot.mq.mqttmica.bridge.autoconfigure;

import io.ddd4j.boot.mq.mqttmica.bridge.MicaMqttBridgeMarker;
import io.ddd4j.boot.mq.mqttmica.bridge.consumer.MicaMqttClientSubscribeRegistrar;
import io.ddd4j.boot.mq.mqttmica.bridge.consumer.MicaMqttClientSubscribeScanner;
import io.ddd4j.boot.mq.mqttmica.bridge.registry.MicaMqttClientSubscribeDefinitionRegistry;
import org.dromara.mica.mqtt.spring.client.MqttClientSubscribeDetector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@code @EnableMicaMqttBridge} 导入的配置：注册原生 {@code @MqttClientSubscribe} 扫描与 mica 会话订阅。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class MicaMqttBridgeConfiguration {

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public MicaMqttBridgeConfiguration() {
    }

    /**
     * 桥接启用标记，用于关闭 no-op 守卫并避免重复装配。
     *
     * @return 桥接标记 Bean 实例
     */
    @Bean
    public MicaMqttBridgeMarker micaMqttBridgeMarker() {
        return new MicaMqttBridgeMarker();
    }

    /**
     * 占位 {@link MqttClientSubscribeDetector}，阻止 mica 默认全局扫描（由本模块 scanner 接管）。
     *
     * @param applicationContext Spring 上下文，注入给 mica 检测器基类
     * @return 空操作的订阅检测器占位 Bean
     */
    @Bean
    public MqttClientSubscribeDetector micaMqttBridgeSubscribeDetectorPlaceholder(ApplicationContext applicationContext) {
        return new MqttClientSubscribeDetector(applicationContext) {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) {
                return bean;
            }
        };
    }

    /**
     * 原生订阅定义注册表。
     *
     * @return 订阅定义注册表 Bean 实例
     */
    @Bean
    public MicaMqttClientSubscribeDefinitionRegistry micaMqttClientSubscribeDefinitionRegistry() {
        return new MicaMqttClientSubscribeDefinitionRegistry();
    }

    /**
     * 将扫描结果注册到 mica {@code IMqttClientSession}，容器销毁时清理注册状态。
     *
     * @param applicationContext Spring 上下文，用于定位客户端模板与会话
     * @return 订阅注册器 Bean 实例
     */
    @Bean(destroyMethod = "close")
    public MicaMqttClientSubscribeRegistrar micaMqttClientSubscribeRegistrar(ApplicationContext applicationContext) {
        return new MicaMqttClientSubscribeRegistrar(applicationContext);
    }

    /**
     * 扫描 {@code @MqttClientSubscribe} 并触发注册。
     *
     * @param registry 订阅定义注册表
     * @param registrar 订阅注册器
     * @return 订阅扫描器 Bean 实例
     */
    @Bean
    public MicaMqttClientSubscribeScanner micaMqttClientSubscribeScanner(
            MicaMqttClientSubscribeDefinitionRegistry registry,
            MicaMqttClientSubscribeRegistrar registrar) {
        return new MicaMqttClientSubscribeScanner(registry, registrar);
    }
}
