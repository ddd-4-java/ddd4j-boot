package io.ddd4j.boot.sample.service;

import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * MQTT 服务端消息发送服务：封装 {@code MqttServerTemplate} 向订阅方广播报文。
 *
 * @author wsq
 */
@Service
public class ServerService {

    /**
     * 无参构造，交由 Spring 注入依赖。
     */
    public ServerService() {
    }

    @Autowired
    private MqttServerTemplate server;

    /**
     * 向固定主题 {@code /test/123} 广播文本报文。
     *
     * @param body 待发送的文本内容，按 UTF-8 编码
     * @return 是否发送成功，当前固定返回 {@code true}
     */
    public boolean publish(String body) {
        server.publishAll("/test/123", body.getBytes(StandardCharsets.UTF_8));
        return true;
    }

}