package io.ddd4j.boot.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.codec.message.builder.MqttPublishBuilder;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * MQTT 客户端核心业务服务。
 * <p>
 * 通过 mica-mqtt 提供的 {@link MqttClientTemplate} 演示消息发布与通配符订阅两类典型用法，
 * 发布使用 QoS0，订阅使用 {@code /test/#} 主题过滤器。
 * </p>
 *
 * @author wsq
 */
@Service
@Slf4j
public class MainService {

    /**
     * mica-mqtt 客户端模板。
     */
    @Autowired
    private MqttClientTemplate client;

    /**
     * 构造 MQTT 核心业务服务（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MainService() {
    }

    /**
     * 向 {@code /test/client,mica最牛皮} 主题发布一条 QoS0 消息。
     *
     * @return 发布请求提交成功时返回 {@code true}
     */
    public boolean publish() {
        client.publish(new MqttPublishBuilder()
                .topicName("/test/client,mica最牛皮")
                .payload("mica最牛皮".getBytes(StandardCharsets.UTF_8))
                .qos(MqttQoS.QOS0));
        return true;
    }

    /**
     * 以 QoS0 订阅 {@code /test/#} 主题，并在回调中打印主题与报文内容。
     *
     * @return 订阅请求提交成功时返回 {@code true}
     */
    public boolean sub() {
        client.subQos0("/test/#", (context, topic, message, payload) -> {
            log.info(topic + '\t' + new String(payload, StandardCharsets.UTF_8));
        });
        return true;
    }

}
