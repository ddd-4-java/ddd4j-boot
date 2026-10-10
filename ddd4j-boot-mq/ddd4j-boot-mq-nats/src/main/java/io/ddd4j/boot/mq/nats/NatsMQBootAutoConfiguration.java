package io.ddd4j.boot.mq.nats;

import io.ddd4j.mq.nats.NatsMQClient;
import io.ddd4j.mq.nats.NatsProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * NATS 消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅当 {@code ddd4j.mq.broker=nats} 且 {@link NatsMQClient} 类存在时生效，
 * 并通过 {@link Ddd4jMQRegistrarConfiguration} 导入 MQ 通用注册配置。
 */
@AutoConfiguration
@ConditionalOnClass(NatsMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "nats")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class NatsMQBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public NatsMQBootAutoConfiguration() {
    }

    /**
     * 装配 NATS 配置属性，绑定 {@code ddd4j.mq.nats.*}。
     *
     * @return 配置属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.nats")
    public NatsProperties natsProperties() {
        return new NatsProperties();
    }

    /**
     * 装配 NATS 消息客户端。
     *
     * @param properties NATS 配置属性
     * @return 消息客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public NatsMQClient natsMQClient(NatsProperties properties) {
        return new NatsMQClient(properties);
    }
}