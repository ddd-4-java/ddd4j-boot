package io.hiwepy.boot.sample.setup.consumer;

import io.hiwepy.boot.sample.setup.TopicConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KafkaConsumer implements InitializingBean {

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
