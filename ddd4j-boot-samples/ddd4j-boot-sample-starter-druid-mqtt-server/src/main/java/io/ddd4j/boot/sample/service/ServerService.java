package io.ddd4j.boot.sample.service;

import org.dromara.mica.mqtt.spring.server.MqttServerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Service
public class ServerService {

    @Autowired
    private MqttServerTemplate server;

    public boolean publish(String body) {
        server.publishAll("/test/123", body.getBytes(StandardCharsets.UTF_8));
        return true;
    }

}