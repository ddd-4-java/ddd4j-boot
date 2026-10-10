package io.ddd4j.boot.mq.disruptor.config;

import io.ddd4j.mq.disruptor.DisruptorMQClient;
import io.ddd4j.mq.disruptor.DisruptorMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Disruptor 进程内消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅在 {@code ddd4j.mq.broker=disruptor} 且 classpath 存在 {@link DisruptorMQClient} 时生效：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.disruptor.*} 到 {@link DisruptorMQProperties}</li>
 *   <li>注册 {@link DisruptorMQClient} Bean，并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(DisruptorMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "disruptor")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class DisruptorMQBootAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public DisruptorMQBootAutoConfiguration() {
    }

    /**
     * 绑定 {@code ddd4j.mq.disruptor.*} 配置到 {@link DisruptorMQProperties}。
     *
     * @return 可被用户自定义 Bean 覆盖的 Disruptor MQ 属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.disruptor")
    public DisruptorMQProperties disruptorMQProperties() {
        return new DisruptorMQProperties();
    }

    /**
     * 基于属性创建 {@link DisruptorMQClient}，容器销毁时调用 {@code close} 释放资源。
     *
     * @param properties 已绑定的 {@code ddd4j.mq.disruptor.*} 配置
     * @return Disruptor MQ 客户端实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public DisruptorMQClient disruptorMQClient(DisruptorMQProperties properties) {
        return new DisruptorMQClient(properties);
    }
}
