package io.ddd4j.boot.sample.setup.config;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    //客户端ID
    private String clientId = "mqtt_client";

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        //连接设置
        MqttConnectOptions options = new MqttConnectOptions();
        //是否清空session，设置false表示服务器会保留客户端的连接记录（订阅主题，qos）,客户端重连之后能获取到服务器在客户端断开连接期间推送的消息
        //设置为true表示每次连接服务器都是以新的身份
        options.setCleanSession(false);

        options.setServerURIs(new String[]{"tcp://localhost:1883"});
        //设置连接用户名
        options.setUserName("mqtt_user");
        //设置连接密码
        options.setPassword("mqtt_password".toCharArray());
        //设置超时时间，单位为秒
        options.setConnectionTimeout(60);
        //设置心跳时间 单位为秒，表示服务器每隔 1.5*10秒的时间向客户端发送心跳判断客户端是否在线
        options.setKeepAliveInterval(20);
        // 开启自动重连
        options.setAutomaticReconnect(true);
        // 设置最大重连时间间隔 (可选)，单位是毫秒，设置为 5000 表示最多等待 5 秒再尝试重连
        options.setMaxReconnectDelay(5000);
        //设置遗嘱消息的话题，若客户端和服务器之间的连接意外断开，服务器将发布客户端的遗嘱信息
        options.setWill("willTopic", (clientId + "与服务器断开连接").getBytes(), 0, false);

        factory.setConnectionOptions(options);

        MqttClientPersistence persistence = new MemoryPersistence();

        factory.setPersistence(persistence);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "gateway-${random.uuid}", mqttClientFactory(), "/sensor/data");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("gateway-${random.uuid}", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("/sensor/data");
        return messageHandler;
    }
}