package io.ddd4j.boot.mq.sqs.autoconfigure;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.ddd4j.boot.mq.sqs.consumer.SqsMQConsumerEndpointRegistrar;
import io.ddd4j.boot.mq.sqs.publisher.SqsMQEventPublisher;
import io.ddd4j.boot.mq.sqs.spi.SqsBrokerAdapter;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.event.MQEventPublisher;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * AWS SQS 组件自动配置，在 {@code ddd4j.mq.enabled=true} 且 broker=sqs 时生效。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */

public class Ddd4jSqsMQAutoConfiguration {

    /**
     * 注册 AmazonSQS 客户端（骨架 Bean，可通过自定义 AmazonSQS 覆盖）。
     *
     * @param region AWS 区域
     * @return SQS 客户端
     */
    @Bean
    public AmazonSQS amazonSqs(
            @Value("${ddd4j.mq.sqs.region:us-east-1}") String region) {
        return AmazonSQSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    /**
     * 注册 SQS 消费端点编排器。
     */
    @Bean(destroyMethod = "close")
    public SqsMQConsumerEndpointRegistrar sqsMQConsumerEndpointRegistrar(
            ObjectProvider<AmazonSQS> amazonSqsProvider,
            @Value("${ddd4j.mq.sqs.queue-url:}") String queueUrl,
            MQProperties properties) {
        return new SqsMQConsumerEndpointRegistrar(amazonSqsProvider.getIfAvailable(), queueUrl, properties);
    }

    /**
     * 注册 SQS Broker 适配器。
     */
    @Bean
    public SqsBrokerAdapter sqsBrokerAdapter(
            ObjectProvider<AmazonSQS> amazonSqsProvider,
            @Value("${ddd4j.mq.sqs.queue-url:}") String queueUrl,
            MQProperties properties,
            SqsMQConsumerEndpointRegistrar consumerEndpointRegistrar) {
        return new SqsBrokerAdapter(amazonSqsProvider.getIfAvailable(), queueUrl, properties, consumerEndpointRegistrar);
    }

    /**
     * 注册领域事件发布 Bean。
     */
    @Bean
    public MQEventPublisher sqsEventPublisher(
            ObjectProvider<AmazonSQS> amazonSqsProvider,
            @Value("${ddd4j.mq.sqs.queue-url:}") String queueUrl,
            MQProperties properties) {
        return new SqsMQEventPublisher(amazonSqsProvider.getIfAvailable(), queueUrl, properties);
    }
}
