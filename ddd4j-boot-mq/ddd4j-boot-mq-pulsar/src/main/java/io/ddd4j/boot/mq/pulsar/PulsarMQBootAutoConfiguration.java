package io.ddd4j.boot.mq.pulsar;

import io.ddd4j.mq.pulsar.PulsarMQClient;
import io.ddd4j.mq.pulsar.PulsarProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PulsarMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "pulsar")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class PulsarMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.pulsar")
    public PulsarProperties pulsarProperties() {
        return new PulsarProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public PulsarMQClient pulsarMQClient(PulsarProperties properties) {
        return new PulsarMQClient(properties);
    }
}