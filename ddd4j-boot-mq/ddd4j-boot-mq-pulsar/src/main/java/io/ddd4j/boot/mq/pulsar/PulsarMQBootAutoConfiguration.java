package io.ddd4j.boot.mq.pulsar;

import io.ddd4j.mq.pulsar.PulsarMQClient;
import io.ddd4j.mq.pulsar.PulsarProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Pulsar 消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅当 {@code ddd4j.mq.broker=pulsar} 且 {@link PulsarMQClient} 类存在时生效，
 * 并通过 {@link Ddd4jMQRegistrarConfiguration} 导入 MQ 通用注册配置。
 */
@AutoConfiguration
@ConditionalOnClass(PulsarMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "pulsar")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class PulsarMQBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public PulsarMQBootAutoConfiguration() {
    }

    /**
     * 装配 Pulsar 配置属性，绑定 {@code ddd4j.mq.pulsar.*}。
     *
     * @return 配置属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.pulsar")
    public PulsarProperties pulsarProperties() {
        return new PulsarProperties();
    }

    /**
     * 装配 Pulsar 消息客户端。
     *
     * @param properties Pulsar 配置属性
     * @return 消息客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public PulsarMQClient pulsarMQClient(PulsarProperties properties) {
        return new PulsarMQClient(properties);
    }
}