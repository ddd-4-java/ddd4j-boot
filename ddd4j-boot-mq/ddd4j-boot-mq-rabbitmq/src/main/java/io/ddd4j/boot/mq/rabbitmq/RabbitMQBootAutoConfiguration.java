package io.ddd4j.boot.mq.rabbitmq;

import io.ddd4j.mq.rabbitmq.RabbitMQClient;
import io.ddd4j.mq.rabbitmq.RabbitMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot RabbitMQ 自动配置（薄适配）。
 *
 * <p>仅做两件事：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.rabbitmq.*} 到 {@link RabbitMQProperties}</li>
 *   <li>注册上游 {@link RabbitMQClient} Bean 并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * <p>Publisher / Listener / Ack / Consumer 均由上游 ddd4j-mq-rabbitmq 提供，boot 侧不重复实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 3.4.x
 */
@AutoConfiguration
@ConditionalOnClass(RabbitMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "rabbit")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class RabbitMQBootAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public RabbitMQBootAutoConfiguration() {
    }

    /**
     * 绑定 {@code ddd4j.mq.rabbitmq.*} 配置到 {@link RabbitMQProperties}。
     *
     * @return 可被用户自定义 Bean 覆盖的 RabbitMQ 属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.rabbitmq")
    public RabbitMQProperties rabbitMQProperties() {
        return new RabbitMQProperties();
    }

    /**
     * 基于属性创建上游 {@link RabbitMQClient}，容器销毁时调用 {@code close} 释放连接。
     *
     * @param properties 已绑定的 {@code ddd4j.mq.rabbitmq.*} 配置
     * @return RabbitMQ 客户端实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RabbitMQClient rabbitMQClient(RabbitMQProperties properties) {
        return new RabbitMQClient(properties);
    }
}