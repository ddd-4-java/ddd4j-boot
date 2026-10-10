package io.ddd4j.boot.sample.consumer;

import com.alibaba.fastjson2.JSON;
import io.ddd4j.boot.sample.setup.TopicConstant;
import io.ddd4j.boot.sample.web.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * RocketMQ 消费者示例容器：以内部类形式演示强类型、通配类型与 tag 过滤三种监听方式。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Slf4j
@Component
public class RocketMQConsumer {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public RocketMQConsumer() {
    }

    // topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
    // selectorExpression的意思指的就是tag，默认为“*”，不设置的话会监听所有消息
    /**
     * 按 {@code tag1} 过滤的强类型消费者：消息体反序列化为 {@link MessageDTO}。
     */
    @Service
    @RocketMQMessageListener(topic = TopicConstant.DEMO_TOPIC, selectorExpression = "tag1", consumerGroup = "Con-Group-One")
    public class ConsumerSend implements RocketMQListener<MessageDTO> {

        /**
         * 无参构造，保持 Spring Bean 默认实例化语义。
         */
        public ConsumerSend() {
        }

        // 监听到消息就会执行此方法
        /**
         * 收到消息后的回调，按 JSON 打印报文。
         *
         * @param message 反序列化后的消息体
         */
        @Override
        public void onMessage(MessageDTO message) {
            log.info("监听到消息：message={}", JSON.toJSONString(message));
        }
    }

    // 注意：这个ConsumerSend2和上面ConsumerSend在没有添加tag做区分时，不能共存，
    // 不然生产者发送一条消息，这两个都会去消费，如果类型不同会有一个报错，所以实际运用中最好加上tag，写这只是让你看知道就行
    /**
     * 不带 tag 过滤的字符串消费者（与 {@link ConsumerSend} 同 topic 时需靠 tag 区分）。
     */
    @Service
    @RocketMQMessageListener(topic = TopicConstant.DEMO_TOPIC, consumerGroup = "Con-Group-Two")
    public class ConsumerSend2 implements RocketMQListener<String> {

        /**
         * 无参构造，保持 Spring Bean 默认实例化语义。
         */
        public ConsumerSend2() {
        }
        /**
         * 收到消息后的回调，直接打印字符串报文。
         *
         * @param str 字符串报文
         */
        @Override
        public void onMessage(String str) {
            log.info("监听到消息：str={}", str);
        }
    }

    // MessageExt：是一个消息接收通配符，不管发送的是String还是对象，都可接收，当然也可以像上面明确指定类型（我建议还是指定类型较方便）
    /**
     * 按 {@code tag2} 过滤、以 {@link MessageExt} 通配接收的消费者。
     */
    @Service
    @RocketMQMessageListener(topic = TopicConstant.DEMO_TOPIC, selectorExpression = "tag2", consumerGroup = "Con-Group-Three")
    public class Consumer implements RocketMQListener<MessageExt> {

        /**
         * 无参构造，保持 Spring Bean 默认实例化语义。
         */
        public Consumer() {
        }
        /**
         * 收到消息后的回调，取出原始字节体并按字符串打印。
         *
         * @param messageExt 原始消息对象
         */
        @Override
        public void onMessage(MessageExt messageExt) {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            log.info("监听到消息：msg={}", msg);
        }
    }

}
