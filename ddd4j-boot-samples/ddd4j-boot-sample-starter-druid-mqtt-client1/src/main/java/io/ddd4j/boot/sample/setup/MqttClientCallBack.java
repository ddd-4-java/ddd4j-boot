package io.ddd4j.boot.sample.setup;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttClientCallBack implements MqttCallback {

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    /**
     * 客户端断开连接的回调
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.error(clientId + "与服务器断开连接！！" + throwable.getMessage());
    }

    /**
     * 消息到达的回调
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
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        IMqttAsyncClient client = token.getClient();
        System.out.println(client.getClientId() + "发布消息成功！");
    }

}
