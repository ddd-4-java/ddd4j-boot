package io.ddd4j.boot.sample.web.controller;

import com.alibaba.fastjson2.JSON;
import io.ddd4j.boot.sample.setup.TopicConstant;
import io.ddd4j.core.ApiRestResponse;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kafka 消息发送前端控制器，提供普通消息与事务消息发送接口。
 */
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    /**
     * 构造 Kafka 前端控制器实例。
     */
    public KafkaController() {
    }

    /**
     * 普通消息发送模板。
     */
    @Resource(name = "kafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 事务消息发送模板。
     */
    @Resource(name = "kafkaTsTemplate")
    private KafkaTemplate<String, String> kafkaTsTemplate;

    /**
     * 发送普通消息。
     *
     * @return 接口返回对象
     */
    @GetMapping("/send")
    public ApiRestResponse<String> send() {
        kafkaTemplate.send(TopicConstant.DEMO_TOPIC, "字符消息");
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    /**
     * 在事务中发送消息。
     *
     * @return 接口返回对象
     */
    @GetMapping("/sendTs")
    public ApiRestResponse<String> sendTag() {
        kafkaTsTemplate.executeInTransaction(new KafkaOperationsCallback(TopicConstant.DEMO_TOPIC_TS, "带有tag的字符消息"));
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    /**
     * 事务消息发送回调，负责将消息体序列化后发送到指定主题。
     */
    public static class KafkaOperationsCallback implements KafkaOperations.OperationsCallback<String, String, Boolean> {

        /**
         * 目标主题。
         */
        private String topic;

        /**
         * 消息体。
         */
        private Object playload;

        /**
         * 构造事务消息发送回调实例。
         *
         * @param topic    目标主题
         * @param playload 消息体
         */
        public KafkaOperationsCallback(String topic, Object playload) {
            this.topic = topic;
            this.playload = playload;
        }

        /**
         * 在 Kafka 操作中发送消息，字符串原样发送，其他类型序列化为 JSON 后发送。
         *
         * @param kafkaOperations Kafka 操作对象
         * @return 发送成功标识
         */
        @Override
        public @NotNull Boolean doInOperations(KafkaOperations kafkaOperations) {
            kafkaOperations.send(topic, playload instanceof String ? playload.toString() : JSON.toJSONString(playload));
            return Boolean.TRUE;
        }

    }

}
