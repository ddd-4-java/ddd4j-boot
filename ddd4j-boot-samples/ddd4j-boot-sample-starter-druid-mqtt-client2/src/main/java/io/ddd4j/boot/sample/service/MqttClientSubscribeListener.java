package io.ddd4j.boot.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * MQTT 客户端订阅监听服务。
 * <p>
 * 借助 mica-mqtt 的 {@code @MqttClientSubscribe} 注解声明式订阅主题，
 * 覆盖 QoS0、QoS1 以及带 {@code ${}} 变量替换的主题三类场景。
 * </p>
 */
@Service
@Slf4j
public class MqttClientSubscribeListener {

    /**
     * 构造订阅监听服务（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttClientSubscribeListener() {
    }

    /**
     * 以 QoS0 订阅 {@code /test/#} 主题并打印报文。
     *
     * @param topic   消息所属的主题
     * @param payload 消息原始字节载荷
     */
    @MqttClientSubscribe("/test/#")
    public void subQos0(String topic, byte[] payload) {
        log.info("subQos0 topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

    /**
     * 以 QoS1 订阅 {@code /qos1/#} 主题并打印报文。
     *
     * @param topic   消息所属的主题
     * @param payload 消息原始字节载荷
     */
    @MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.QOS1)
    public void subQos1(String topic, byte[] payload) {
        log.info("subQos1 topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

    /**
     * 订阅设备注册上报主题并打印报文。
     *
     * @param topic   消息所属的主题
     * @param payload 消息原始字节载荷
     */
    @MqttClientSubscribe("/sys/${productKey}/${deviceName}/thing/sub/register")
    public void thingSubRegister(String topic, byte[] payload) {
        // 1.3.8 开始支持，@MqttClientSubscribe 注解支持 ${} 变量替换，会默认替换成 +
        // 注意：mica-mqtt 会先从 Spring boot 配置中替换参数 ${}，如果存在配置会优先被替换。
        log.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

}
