package io.ddd4j.boot.mq.sqs;

import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import io.ddd4j.mq.sqs.SqsMQClient;
import io.ddd4j.mq.sqs.SqsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@ConditionalOnClass(SqsMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "sqs")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class SqsMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.sqs")
    public SqsProperties sqsProperties() {
        return new SqsProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqsMQClient sqsMQClient(SqsProperties properties) {
        return new SqsMQClient(properties);
    }
}