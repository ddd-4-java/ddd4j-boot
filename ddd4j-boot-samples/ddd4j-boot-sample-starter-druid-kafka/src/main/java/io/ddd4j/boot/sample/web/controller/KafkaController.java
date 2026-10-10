package io.ddd4j.boot.sample.web.controller;

import com.alibaba.fastjson2.JSON;
import io.ddd4j.core.ApiRestResponse;
import io.ddd4j.boot.sample.setup.TopicConstant;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kafka 消息发送演示控制器。
 * <p>
 * 提供普通发送与事务型发送两个演示端点，分别使用容器中的
 * {@code kafkaTemplate} 与 {@code kafkaTsTemplate} 两个模板 Bean。
 * </p>
 */
@RestController
@RequestMapping("/kafka")
public class KafkaController {

    @Resource(name = "kafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource(name = "kafkaTsTemplate")
    private KafkaTemplate<String, String> kafkaTsTemplate;

    /**
     * 构造 Kafka 演示控制器（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public KafkaController() {
    }

    /**
     * 向常规演示主题发送一条字符消息。
     *
     * @return 发送结果的统一响应对象
     */
    @GetMapping("/send")
    public ApiRestResponse<String> send() {
        kafkaTemplate.send(TopicConstant.DEMO_TOPIC, "字符消息");
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    /**
     * 在事务上下文中向事务型演示主题发送一条带标签的字符消息。
     *
     * @return 发送结果的统一响应对象
     */
    @GetMapping("/sendTs")
    public ApiRestResponse<String> sendTag() {
        kafkaTsTemplate.executeInTransaction(new KafkaOperationsCallback(TopicConstant.DEMO_TOPIC_TS, "带有tag的字符消息"));
        return ApiRestResponse.success("kafka.msg.send.success");
    }

    /**
     * 事务发送回调：在模板管理的事务上下文中完成实际发送。
     */
    public static class KafkaOperationsCallback implements KafkaOperations.OperationsCallback<String, String, Boolean> {

        private String topic;
        private Object playload;

        /**
         * 构造事务发送回调。
         *
         * @param topic    目标主题名称
         * @param playload 待发送的消息载荷
         */
        public KafkaOperationsCallback(String topic, Object playload) {
            this.topic = topic;
            this.playload = playload;
        }

        /**
         * 执行消息发送；字符串载荷原样发送，其余载荷序列化为 JSON 后发送。
         *
         * @param kafkaOperations 事务上下文中的 Kafka 操作对象
         * @return 恒为 {@code true}，表示发送逻辑已执行
         */
        @Override
        public @NotNull Boolean doInOperations(KafkaOperations kafkaOperations) {
            kafkaOperations.send(topic, playload instanceof String ? playload.toString() : JSON.toJSONString(playload));
            return Boolean.TRUE;
        }

    }

}