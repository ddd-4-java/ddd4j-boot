package io.ddd4j.boot.mq.kafka.mq;

import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.serialization.MessageSerialization;
import io.ddd4j.mq.spi.BrokerAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * ddd4j Kafka MQ 适配层自动配置。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jKafkaMQAutoConfiguration {

    /**
     * 注册 Kafka {@link BrokerAdapter} Bean。
     */
    @Bean
    public BrokerAdapter kafkaBrokerAdapter(
            ObjectProvider<KafkaTemplate<String, String>> kafkaTemplate,
            ObjectProvider<ConsumerFactory<String, String>> consumerFactory,
            ObjectProvider<MessageSerialization> serialization) {
        return new KafkaBrokerAdapter(
                kafkaTemplate.getIfAvailable(),
                consumerFactory.getIfAvailable(),
                serialization.getIfAvailable());
    }

    /**
     * 注册领域事件发布 Bean（与 Rabbit 等 cmpt 模块对齐）。
     */
    @Bean
    public EventPublisher kafkaEventPublisher(
            KafkaTemplate<String, String> kafkaTemplate,
            MessageSerialization serialization,
            MQProperties properties) {
        return new KafkaEventPublisher(kafkaTemplate, serialization, properties);
    }
}
