package io.ddd4j.boot.sample.handler;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * MQTT 入站消息处理端点。
 * <p>
 * 通过 Spring Integration 的 {@code @MessageEndpoint} 声明为消息处理组件，
 * 将 {@code mqttInputChannel} 上游投递的消息交由 {@link #handleMessage(Message)} 消费。
 * </p>
 */
@MessageEndpoint
public class MqttMessageHandler {

    /**
     * 构造 MQTT 消息处理端点（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttMessageHandler() {
    }

    /**
     * 处理订阅通道投递过来的消息，打印消息载荷。
     *
     * @param message 待处理的 Spring Messaging 消息
     */
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        System.out.println("Received message: " + message.getPayload());
        // 在这里处理接收到的消息
    }
}
