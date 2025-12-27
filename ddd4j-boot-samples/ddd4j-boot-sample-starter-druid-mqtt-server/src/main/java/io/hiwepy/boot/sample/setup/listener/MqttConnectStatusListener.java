package io.hiwepy.boot.sample.setup.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.spring.server.event.MqttClientOfflineEvent;
import org.dromara.mica.mqtt.spring.server.event.MqttClientOnlineEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqttConnectStatusListener {

    @EventListener
    public void online(MqttClientOnlineEvent event) {
        log.info("MqttClientOnlineEvent:{}", event);
    }

    @EventListener
    public void offline(MqttClientOfflineEvent event) {
        log.info("MqttClientOfflineEvent:{}", event);
    }

}