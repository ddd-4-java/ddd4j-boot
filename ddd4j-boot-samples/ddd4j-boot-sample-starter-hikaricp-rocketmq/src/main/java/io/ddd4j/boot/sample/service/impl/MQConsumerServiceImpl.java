package io.ddd4j.boot.sample.service.impl;

import com.alibaba.fastjson2.JSON;
import io.ddd4j.boot.sample.web.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * RocketMQ 消息消费者实现，内置三个监听器分别演示按 tag 过滤、全量监听与通配符接收。
 */
@Slf4j
@Component
public class MQConsumerServiceImpl {

    /**
     * 构造消息消费者实现类实例。
     */
    public MQConsumerServiceImpl() {
    }

    // topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
    // selectorExpression的意思指的就是tag，默认为“*”，不设置的话会监听所有消息
    /**
     * 按 tag1 过滤的消息监听器，消费 {@link MessageDTO} 类型消息。
     */
    @Service
    @RocketMQMessageListener(topic = "RLT_TEST_TOPIC", selectorExpression = "tag1", consumerGroup = "Con_Group_One")
    public class ConsumerSend implements RocketMQListener<MessageDTO> {

        /**
         * 构造 tag1 消息监听器实例。
         */
        public ConsumerSend() {
        }

        // 监听到消息就会执行此方法
        /**
         * 处理监听到的消息，转为 JSON 后打印日志。
         *
         * @param message 消息数据传输对象
         */
        @Override
        public void onMessage(MessageDTO message) {
            log.info("监听到消息：message={}", JSON.toJSONString(message));
        }
    }

    // 注意：这个ConsumerSend2和上面ConsumerSend在没有添加tag做区分时，不能共存，
    // 不然生产者发送一条消息，这两个都会去消费，如果类型不同会有一个报错，所以实际运用中最好加上tag，写这只是让你看知道就行
    /**
     * 不区分 tag 的消息监听器，消费 String 类型消息。
     */
    @Service
    @RocketMQMessageListener(topic = "RLT_TEST_TOPIC", consumerGroup = "Con_Group_Two")
    public class ConsumerSend2 implements RocketMQListener<String> {

        /**
         * 构造无 tag 过滤消息监听器实例。
         */
        public ConsumerSend2() {
        }

        /**
         * 处理监听到的消息，打印日志。
         *
         * @param str 消息体字符串
         */
        @Override
        public void onMessage(String str) {
            log.info("监听到消息：str={}", str);
        }
    }

    // MessageExt：是一个消息接收通配符，不管发送的是String还是对象，都可接收，当然也可以像上面明确指定类型（我建议还是指定类型较方便）
    /**
     * 按 tag2 过滤、以 {@link MessageExt} 通配接收的消息监听器。
     */
    @Service
    @RocketMQMessageListener(topic = "RLT_TEST_TOPIC", selectorExpression = "tag2", consumerGroup = "Con_Group_Three")
    public class Consumer implements RocketMQListener<MessageExt> {

        /**
         * 构造 tag2 通配消息监听器实例。
         */
        public Consumer() {
        }

        /**
         * 处理监听到的消息，取出消息体字节数组转字符串后打印日志。
         *
         * @param messageExt 消息扩展对象
         */
        @Override
        public void onMessage(MessageExt messageExt) {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            log.info("监听到消息：msg={}", msg);
        }
    }

}
