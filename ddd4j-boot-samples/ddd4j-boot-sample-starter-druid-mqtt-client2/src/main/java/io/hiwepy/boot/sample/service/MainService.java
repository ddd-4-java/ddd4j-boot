package io.hiwepy.boot.sample.service;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.codec.message.builder.MqttPublishBuilder;
import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Service
@Slf4j
public class MainService {

    @Autowired
    private MqttClientTemplate client;

    public boolean publish() {
        client.publish(new MqttPublishBuilder()
                .topicName("/test/client,mica最牛皮")
                .payload("mica最牛皮".getBytes(StandardCharsets.UTF_8))
                .qos(MqttQoS.QOS0));
        return true;
    }

    public boolean sub() {
        client.subQos0("/test/#", (context, topic, message, payload) -> {
            log.info(topic + '\t' + new String(payload, StandardCharsets.UTF_8));
        });
        return true;
    }

}