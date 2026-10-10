package io.ddd4j.boot.sample.setup.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.codec.message.MqttPublishMessage;
import org.dromara.mica.mqtt.core.annotation.MqttClientSubscribe;
import org.dromara.mica.mqtt.core.deserialize.MqttJsonDeserializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * mica-mqtt 声明式订阅监听器。
 * <p>
 * 通过 {@code @MqttClientSubscribe} 注解直接绑定主题，覆盖 QoS0、QoS1、
 * 带 {@code ${}} 变量替换的主题以及自定义反序列化器四类订阅方式。
 * </p>
 */
@Service
@Slf4j
public class MqttClientSubscribeListener {

    /**
     * 构造声明式订阅监听器（显式无参构造器，与编译器生成的默认构造器等价）。
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
        log.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
    }

    /**
     * 以 QoS1 订阅 {@code /qos1/#} 主题并打印报文。
     *
     * @param topic   消息所属的主题
     * @param payload 消息原始字节载荷
     */
    @MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.QOS1)
    public void subQos1(String topic, byte[] payload) {
        log.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
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

    /**
     * 订阅 {@code /test/json} 主题，使用 JSON 反序列化器把报文转换为 {@link TestJsonBean}。
     *
     * @param topic   消息所属的主题（参数名与类型映射到主题）
     * @param message 原始 MQTT 发布消息，可读取 MQTT5 的 props 参数
     * @param data    反序列化后的 JSON 数据对象
     */
    @MqttClientSubscribe(
            value = "/test/json", deserialize = MqttJsonDeserializer.class // 2.4.5 开始支持 自定义序列化，默认 json 序列化
    )
    public void testJson(String topic, MqttPublishMessage message, TestJsonBean data) {
        // 2.4.5 开始支持，支持 2 到 3 个参数，字段类型映射规则如下
        // String 字符串会默认映射到 topic，
        // MqttPublishMessage 会默认映射到 原始的消息，可以拿到 mqtt5 的 props 参数
        // byte[] 会映射到 mqtt 消息内容 payload
        // ByteBuffer 会映射到 mqtt 消息内容 payload
        // 其他类型会走序列化，确保消息能够序列化，默认为 json 序列化
        log.info("topic:{} json data:{}", topic, data);
    }

}
