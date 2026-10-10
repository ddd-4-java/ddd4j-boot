package io.ddd4j.boot.sample.setup.consumer;

import io.ddd4j.boot.sample.setup.TopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kafka 演示消息消费者。
 * <p>
 * 通过 {@code @KafkaListener} 分别订阅常规主题与事务型主题：
 * 常规主题按批打印消息内容，事务型主题额外手动提交消费位点。
 * </p>
 */
@Slf4j
@Component
public class KafkaConsumer implements InitializingBean {

    /**
     * 构造 Kafka 消费者（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public KafkaConsumer() {
    }

    /**
     * Bean 属性初始化完成后的回调，当前未定义额外初始化逻辑。
     *
     * @throws Exception 初始化过程中发生的任何异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * 普通消息监听器
     *
     * @param records 消息记录
     */
    @KafkaListener(topics = TopicConstant.DEMO_TOPIC, containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(List<ConsumerRecord<String, String>> records) {
        for (ConsumerRecord<String, String> record : records) {
            log.info("接收普通消息>>topic={},value={},size={}", record.topic(), record.value(), records.size());
        }
    }

    /**
     * 事务消息监听器
     *
     * @param records 消息记录
     * @param ack     Acknowledgment 对象
     */
    @KafkaListener(topics = TopicConstant.DEMO_TOPIC_TS, containerFactory = "kafkaTsListenerContainerFactory")
    public void onTsMessage(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        for (ConsumerRecord<String, String> record : records) {
            log.info("接收事务消息>>topic={},value={},size={}", record.topic(), record.value(), records.size());
        }
        // 手动提交偏移量
        ack.acknowledge();
    }

}
