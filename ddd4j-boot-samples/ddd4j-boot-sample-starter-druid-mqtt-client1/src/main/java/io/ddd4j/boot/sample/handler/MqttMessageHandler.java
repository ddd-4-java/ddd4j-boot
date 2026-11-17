package io.ddd4j.boot.sample.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@MessageEndpoint
@Slf4j
public class MqttMessageHandler {

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        log.info("Received message: {}", message.getPayload());
        // 在这里处理接收到的消息
    }
}
