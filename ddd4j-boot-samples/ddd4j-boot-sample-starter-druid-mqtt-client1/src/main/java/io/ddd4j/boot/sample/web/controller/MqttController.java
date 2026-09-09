package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.service.MqttPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    private MqttPublisher mqttPublisher;

    @GetMapping("/send")
    public void send(String message) {
        mqttPublisher.publishMessage("/sensor/data", message);
    }

}