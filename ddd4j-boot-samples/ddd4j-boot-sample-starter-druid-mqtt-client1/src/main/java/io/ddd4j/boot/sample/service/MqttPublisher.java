package io.ddd4j.boot.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * MQTT 消息发布服务。
 * <p>
 * 以 {@code @Service} 形式对外提供统一的发布入口，把业务侧的“主题 + 报文”
 * 两个参数封装为 Spring Messaging 消息并投递到 {@code mqttInputChannel}，
 * 由后续的出站适配器转换为 MQTT 报文。
 * </p>
 */
@Service
public class MqttPublisher {

    /**
     * MQTT 出站消息通道。
     */
    @Autowired
    private MessageChannel mqttInputChannel;

    /**
     * 构造 MQTT 发布服务（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttPublisher() {
    }

    /**
     * 向指定主题发布一条消息。
     *
     * @param topic   目标 MQTT 主题
     * @param message 消息内容（文本载荷）
     */
    public void publishMessage(String topic, String message) {
        mqttInputChannel.send(MessageBuilder.withPayload(message).setHeader("mqtt_topic", topic).build());
    }
}
