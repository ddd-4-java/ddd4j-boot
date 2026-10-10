package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.service.MqttPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MQTT 消息发送演示控制器。
 * <p>
 * 暴露 {@code /mqtt/send} 接口，接收字符串报文并委托
 * {@link MqttPublisher} 发布到固定的传感器数据主题，用于联调验证发布链路。
 * </p>
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {

    /**
     * MQTT 消息发布服务。
     */
    @Autowired
    private MqttPublisher mqttPublisher;

    /**
     * 构造 MQTT 演示控制器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttController() {
    }

    /**
     * 将入参报文发布到 {@code /sensor/data} 主题。
     *
     * @param message 待发送的消息内容
     */
    @GetMapping("/send")
    public void send(String message) {
        mqttPublisher.publishMessage("/sensor/data", message);
    }

}
