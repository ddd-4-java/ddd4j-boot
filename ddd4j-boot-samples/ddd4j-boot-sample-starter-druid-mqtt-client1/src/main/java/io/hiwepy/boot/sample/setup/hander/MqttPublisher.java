package io.hiwepy.boot.sample.setup.hander;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {

    @Autowired
    private MessageChannel mqttInputChannel;

    public void publishMessage(String topic, String message) {
        mqttInputChannel.send(MessageBuilder.withPayload(message).setHeader("mqtt_topic", topic).build());
    }

}