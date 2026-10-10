package io.ddd4j.boot.sample.setup;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Paho MQTT 客户端回调实现。
 * <p>
 * 以 {@code @Component} 形式注册到容器，处理连接断开、消息到达与投递完成三类回调，
 * 客户端标识取自配置项 {@code spring.mqtt.client.id}，用于日志中区分当前客户端实例。
 * </p>
 */
@Slf4j
@Component
public class MqttClientCallBack implements MqttCallback {

    /**
     * 当前 MQTT 客户端标识。
     */
    @Value("${spring.mqtt.client.id}")
    private String clientId;

    /**
     * 构造 MQTT 客户端回调（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public MqttClientCallBack() {
    }

    /**
     * 客户端断开连接的回调
     *
     * @param throwable 导致连接中断的异常原因
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error(clientId + "与服务器断开连接！！" + throwable.getMessage());
    }

    /**
     * 消息到达的回调
     *
     * @param topic   消息所属的主题
     * @param message 收到的 MQTT 消息体
     * @throws Exception 消息解析或处理过程中发生的异常
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.printf("接收消息主题 : %s%n", topic);
        System.out.printf("接收消息Qos : %d%n", message.getQos());
        System.out.printf("接收消息内容 : %s%n", new String(message.getPayload()));
        System.out.printf("接收消息retained : %b%n", message.isRetained());
    }

    /**
     * 消息发布成功的回调
     *
     * @param token 本次投递对应的交付令牌
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        IMqttAsyncClient client = token.getClient();
        System.out.println(client.getClientId() + "发布消息成功！");
    }

}
