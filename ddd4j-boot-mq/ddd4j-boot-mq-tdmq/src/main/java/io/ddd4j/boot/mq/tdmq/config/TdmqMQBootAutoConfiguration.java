package io.ddd4j.boot.mq.tdmq.config;

import io.ddd4j.mq.config.Ddd4jMQProperties;
import io.ddd4j.mq.publish.MQEventPublisher;
import io.ddd4j.mq.serialization.JsonMQMessageSerialization;
import io.ddd4j.mq.serialization.MQEventSerialization;
import io.ddd4j.mq.tdmq.client.TdmqClient;
import io.ddd4j.mq.tdmq.client.TdmqClientPlaceholder;
import io.ddd4j.mq.tdmq.spi.TdmqMQBrokerAdapter;
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
@ConditionalOnClass(TdmqMQBrokerAdapter.class)
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
    public TdmqMQBrokerAdapter tdmqMQBrokerAdapter(
            TdmqClient tdmqClient,
            TdmqMQProperties tdmqProperties,
            Ddd4jMQProperties mqProperties,
            ObjectProvider<MQEventSerialization> serialization) {
        MQEventSerialization eventSerialization = serialization.getIfAvailable(JsonMQMessageSerialization::new);
        return new TdmqMQBrokerAdapter(tdmqClient, tdmqProperties, mqProperties, eventSerialization);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tdmqMQEventPublisher")
    public MQEventPublisher tdmqMQEventPublisher(
            TdmqMQBrokerAdapter tdmqMQBrokerAdapter,
            Ddd4jMQProperties mqProperties) {
        return tdmqMQBrokerAdapter.createPublisher(mqProperties);
    }
}
