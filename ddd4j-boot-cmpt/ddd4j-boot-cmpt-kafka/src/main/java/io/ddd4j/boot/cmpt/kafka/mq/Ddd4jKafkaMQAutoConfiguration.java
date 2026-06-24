package io.ddd4j.boot.cmpt.kafka.mq;

import io.ddd4j.boot.mq.config.Ddd4jMQProperties;
import io.ddd4j.boot.mq.core.MQEventSerialization;
import io.ddd4j.boot.mq.spi.MQBrokerAdapter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * ddd4j Kafka MQ 适配层自动配置。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({KafkaTemplate.class, MQBrokerAdapter.class})
@ConditionalOnExpression("${ddd4j.mq.enabled:false} == true && '${ddd4j.mq.broker:none}'.equals('kafka')")
@EnableConfigurationProperties(Ddd4jMQProperties.class)
@AutoConfigureAfter(KafkaAutoConfiguration.class)
public class Ddd4jKafkaMQAutoConfiguration {

    /**
     * 注册 Kafka {@link MQBrokerAdapter} Bean。
     *
     * @param kafkaTemplate     Kafka 模板
     * @param consumerFactory   消费者工厂
     * @param serialization     事件序列化器
     * @return Kafka Broker 适配器
     */
    @Bean
    @ConditionalOnMissingBean(name = "kafkaMQBrokerAdapter")
    public MQBrokerAdapter kafkaMQBrokerAdapter(
            ObjectProvider<KafkaTemplate<String, String>> kafkaTemplate,
            ObjectProvider<ConsumerFactory<String, String>> consumerFactory,
            ObjectProvider<MQEventSerialization> serialization) {
        return new KafkaMQBrokerAdapter(
                kafkaTemplate.getIfAvailable(),
                consumerFactory.getIfAvailable(),
                serialization.getIfAvailable());
    }
}
