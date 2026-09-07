package io.ddd4j.boot.mq.disruptor.config;

import io.ddd4j.mq.disruptor.DisruptorMQClient;
import io.ddd4j.mq.disruptor.DisruptorMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DisruptorMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "disruptor")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class DisruptorMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.disruptor")
    public DisruptorMQProperties disruptorMQProperties() {
        return new DisruptorMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public DisruptorMQClient disruptorMQClient(DisruptorMQProperties properties) {
        return new DisruptorMQClient(properties);
    }
}
