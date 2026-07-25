package io.ddd4j.boot.mq.ons;

import io.ddd4j.mq.ons.OnsMQClient;
import io.ddd4j.mq.ons.OnsProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(OnsMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "ons")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class OnsMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.ons")
    public OnsProperties onsProperties() {
        return new OnsProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public OnsMQClient onsMQClient(OnsProperties properties) {
        return new OnsMQClient(properties);
    }
}