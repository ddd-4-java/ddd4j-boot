package io.ddd4j.boot.sample.setup.listener;

import lombok.extern.slf4j.Slf4j;
import org.dromara.mica.mqtt.core.client.MqttClientCreator;
import org.dromara.mica.mqtt.spring.client.event.MqttConnectedEvent;
import org.dromara.mica.mqtt.spring.client.event.MqttDisconnectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * 示例：客户端连接状态监听
 * <p>
 * 监听 mica-mqtt 客户端的连接与断开事件：连接成功时打印事件信息，
 * 断开时刷新客户端标识与鉴权信息，适配类似阿里云 MQTT 以时间戳拼接
 * clientId 的重连场景。
 * </p>
 *
 * @author L.cm
 */
@Service
@Slf4j
public class MqttClientConnectListener {

    /**
     * MQTT 客户端创建器，断线时用于更新重连参数。
     */
    @Autowired
    private MqttClientCreator mqttClientCreator;

    /**
     * 构造连接状态监听服务（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttClientConnectListener() {
    }

    /**
     * 客户端连接成功事件回调。
     *
     * @param event 连接成功事件
     */
    @EventListener
    public void onConnected(MqttConnectedEvent event) {
        log.info("MqttConnectedEvent:{}", event);
    }

    /**
     * 客户端连接断开事件回调，打印事件并刷新重连时使用的连接参数。
     *
     * @param event 连接断开事件
     */
    @EventListener
    public void onDisconnect(MqttDisconnectEvent event) {
        // 离线时更新重连时的密码，适用于类似阿里云 mqtt clientId 连接带时间戳的方式
        log.info("MqttDisconnectEvent:{}", event);
        // 在断线时更新 clientId、username、password
        mqttClientCreator.clientId("newClient" + System.currentTimeMillis())
                .username("newUserName")
                .password("newPassword");
    }

}
