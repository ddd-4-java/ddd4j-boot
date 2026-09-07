package io.ddd4j.boot.mq.nats;

import io.ddd4j.mq.nats.NatsMQClient;
import io.ddd4j.mq.nats.NatsProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NatsMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "nats")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class NatsMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.nats")
    public NatsProperties natsProperties() {
        return new NatsProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public NatsMQClient natsMQClient(NatsProperties properties) {
        return new NatsMQClient(properties);
    }
}