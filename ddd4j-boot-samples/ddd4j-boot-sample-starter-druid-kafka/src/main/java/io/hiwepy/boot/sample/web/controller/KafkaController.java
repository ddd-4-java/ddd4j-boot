package io.hiwepy.boot.sample.web.controller;

import com.alibaba.fastjson2.JSON;
import io.ddd4j.boot.core.ApiRestResponse;
import io.hiwepy.boot.sample.setup.TopicConstant;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource(name = "kafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource(name = "kafkaTsTemplate")
    private KafkaTemplate<String, String> kafkaTsTemplate;

    @GetMapping("/send")
    public ApiRestResponse<String> send() {
        kafkaTemplate.send(TopicConstant.DEMO_TOPIC, "字符消息");
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    @GetMapping("/sendTs")
    public ApiRestResponse<String> sendTag() {
        kafkaTsTemplate.executeInTransaction(new KafkaOperationsCallback(TopicConstant.DEMO_TOPIC_TS, "带有tag的字符消息"));
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    public static class KafkaOperationsCallback implements KafkaOperations.OperationsCallback<String, String, Boolean> {

        private String topic;
        private Object playload;

        public KafkaOperationsCallback(String topic, Object playload) {
            this.topic = topic;
            this.playload = playload;
        }

        @Override
        public @NotNull Boolean doInOperations(KafkaOperations kafkaOperations) {
            kafkaOperations.send(topic, playload instanceof String ? playload.toString() : JSON.toJSONString(playload));
            return Boolean.TRUE;
        }

    }

}