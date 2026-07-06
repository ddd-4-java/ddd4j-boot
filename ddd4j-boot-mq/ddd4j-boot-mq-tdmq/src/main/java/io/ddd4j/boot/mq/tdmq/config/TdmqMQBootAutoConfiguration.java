package io.ddd4j.boot.mq.tdmq.config;

import io.ddd4j.mq.config.MQProperties;
import io.ddd4j.mq.publish.EventPublisher;
import io.ddd4j.mq.serialization.JsonSerialization;
import io.ddd4j.mq.serialization.EventSerialization;
import io.ddd4j.mq.tdmq.client.TdmqClient;
import io.ddd4j.mq.tdmq.client.TdmqClientPlaceholder;
import io.ddd4j.mq.tdmq.spi.TdmqBrokerAdapter;
import io.ddd4j.mq.tdmq.spi.TdmqMQProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * TDMQ Spring Boot auto-configuration.
 *
 * <p>Broker 实现位于 {@code ddd4j-mq-tdmq}；本模块只负责属性绑定与 Spring Bean 暴露。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(TdmqBrokerAdapter.class)
public class TdmqMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.tdmq")
    public TdmqMQProperties tdmqMQProperties() {
        return new TdmqMQProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public TdmqClient tdmqClient() {
        return new TdmqClientPlaceholder();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public TdmqBrokerAdapter tdmqBrokerAdapter(
            TdmqClient tdmqClient,
            TdmqMQProperties tdmqProperties,
            MQProperties mqProperties,
            ObjectProvider<EventSerialization> serialization) {
        EventSerialization eventSerialization = serialization.getIfAvailable(JsonSerialization::new);
        return new TdmqBrokerAdapter(tdmqClient, tdmqProperties, mqProperties, eventSerialization);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tdmqEventPublisher")
    public EventPublisher tdmqEventPublisher(
            TdmqBrokerAdapter tdmqBrokerAdapter,
            MQProperties mqProperties) {
        return tdmqBrokerAdapter.createPublisher(mqProperties);
    }
}
