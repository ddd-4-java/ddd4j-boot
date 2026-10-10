package io.ddd4j.boot.mq.tdmq.config;

import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import io.ddd4j.mq.tdmq.TdmqMQClient;
import io.ddd4j.mq.tdmq.TdmqProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot tdmq 自动配置（薄适配）。
 *
 * <p>仅做两件事：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.tdmq.*} 到 {@link TdmqProperties}</li>
 *   <li>注册上游 {@link TdmqMQClient} Bean 并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * <p>Publisher / Subscriber / Ack / Properties 均由上游 ddd4j-mq-tdmq 提供，boot 侧不重复实现。
 * 业务侧可通过 {@link TdmqMQClient#setBrokerPublisher} / {@link TdmqMQClient#setBrokerSubscriber}
 * 注入真实腾讯云 SDK 适配；未注入时回落到内存总线（仅测试）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(TdmqMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "tdmq")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class TdmqMQBootAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public TdmqMQBootAutoConfiguration() {
    }

    /**
     * 绑定 {@code ddd4j.mq.tdmq.*} 配置到 {@link TdmqProperties}。
     *
     * @return 可被用户自定义 Bean 覆盖的 TDMQ 属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.tdmq")
    public TdmqProperties tdmqProperties() {
        return new TdmqProperties();
    }

    /**
     * 基于属性创建上游 {@link TdmqMQClient}，容器销毁时调用 {@code close} 释放连接。
     *
     * @param properties 已绑定的 {@code ddd4j.mq.tdmq.*} 配置
     * @return TDMQ MQ 客户端实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public TdmqMQClient tdmqMQClient(TdmqProperties properties) {
        return new TdmqMQClient(properties);
    }
}
