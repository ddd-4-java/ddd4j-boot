package io.ddd4j.boot.mq.kafka.config;

import io.ddd4j.mq.kafka.KafkaMQClient;
import io.ddd4j.mq.kafka.KafkaMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.apache.kafka.clients.producer.Callback;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot kafka 自动配置（薄适配）。
 *
 * <p>仅做两件事：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.kafka.*} 到 {@link KafkaMQProperties}</li>
 *   <li>注册上游 {@link KafkaMQClient} Bean 并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * <p>MQPublisher / MQListener / Ack / Producer 均由上游 ddd4j-mq-kafka 提供，boot 侧不重复实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 3.4.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "kafka")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class KafkaMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.kafka")
    public KafkaMQProperties kafkaMQProperties() {
        return new KafkaMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public KafkaMQClient kafkaMQClient(KafkaMQProperties properties) {
        return new KafkaMQClient(properties, (Callback) null);
    }
}
