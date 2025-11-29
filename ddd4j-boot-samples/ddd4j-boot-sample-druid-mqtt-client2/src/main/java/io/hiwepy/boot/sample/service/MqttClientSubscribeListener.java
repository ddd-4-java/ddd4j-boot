package io.hiwepy.boot.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MqttClientSubscribeListener {

    @MqttClientSubscribe("/test/#")
    public void subQos0(String topic, byte[] payload) {
        log.info("subQos0 topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

    @MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.QOS1)
    public void subQos1(String topic, byte[] payload) {
        log.info("subQos1 topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

    @MqttClientSubscribe("/sys/${productKey}/${deviceName}/thing/sub/register")
    public void thingSubRegister(String topic, byte[] payload) {
        // 1.3.8 开始支持，@MqttClientSubscribe 注解支持 ${} 变量替换，会默认替换成 +
        // 注意：mica-mqtt 会先从 Spring boot 配置中替换参数 ${}，如果存在配置会优先被替换。
        log.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

}
