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

/**
 * ONS（阿里云） 消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅当 {@code ddd4j.mq.broker=ons} 且 {@link OnsMQClient} 类存在时生效，
 * 并通过 {@link Ddd4jMQRegistrarConfiguration} 导入 MQ 通用注册配置。
 */
@AutoConfiguration
@ConditionalOnClass(OnsMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "ons")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class OnsMQBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public OnsMQBootAutoConfiguration() {
    }

    /**
     * 装配 ONS（阿里云） 配置属性，绑定 {@code ddd4j.mq.ons.*}。
     *
     * @return 配置属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties("ddd4j.mq.ons")
    public OnsProperties onsProperties() {
        return new OnsProperties();
    }

    /**
     * 装配 ONS（阿里云） 消息客户端。
     *
     * @param properties ONS（阿里云） 配置属性
     * @return 消息客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OnsMQClient onsMQClient(OnsProperties properties) {
        return new OnsMQClient(properties);
    }
}