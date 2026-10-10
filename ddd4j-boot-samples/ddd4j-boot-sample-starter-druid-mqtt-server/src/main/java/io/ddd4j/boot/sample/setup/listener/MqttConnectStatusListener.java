package io.ddd4j.boot.sample.setup.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.spring.server.event.MqttClientOfflineEvent;
import org.dromara.mica.mqtt.spring.server.event.MqttClientOnlineEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * MQTT 客户端上下线状态监听器：订阅服务端客户端在线/离线事件并记录日志。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Service
@Slf4j
public class MqttConnectStatusListener {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public MqttConnectStatusListener() {
    }

    /**
     * 客户端上线事件处理。
     *
     * @param event MQTT 客户端上线事件
     */
    @EventListener
    public void online(MqttClientOnlineEvent event) {
        log.info("MqttClientOnlineEvent:{}", event);
    }

    /**
     * 客户端离线事件处理。
     *
     * @param event MQTT 客户端离线事件
     */
    @EventListener
    public void offline(MqttClientOfflineEvent event) {
        log.info("MqttClientOfflineEvent:{}", event);
    }

}