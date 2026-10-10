package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.mq.redisstream.RedisStreamMQClient;
import io.ddd4j.mq.redisstream.RedisStreamMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot redis-stream 自动配置（薄适配）。
 *
 * <p>仅做两件事：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.redis-stream.*} 到 {@link RedisStreamMQProperties}</li>
 *   <li>注册上游 {@link RedisStreamMQClient} Bean 并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * <p>Publisher / Listener / Ack / Properties / Operations 均由上游 ddd4j-mq-redis-stream 提供，boot 侧不重复实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass(RedisStreamMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "redisStream")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class RedisStreamMQBootAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public RedisStreamMQBootAutoConfiguration() {
    }

    /**
     * 绑定 {@code ddd4j.mq.redis-stream.*} 配置到 {@link RedisStreamMQProperties}。
     *
     * @return 可被用户自定义 Bean 覆盖的 Redis Stream MQ 属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.redis-stream")
    public RedisStreamMQProperties redisStreamMQProperties() {
        return new RedisStreamMQProperties();
    }

    /**
     * 基于属性创建上游 {@link RedisStreamMQClient}，容器销毁时调用 {@code close} 释放连接。
     *
     * @param properties 已绑定的 {@code ddd4j.mq.redis-stream.*} 配置
     * @return Redis Stream MQ 客户端实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RedisStreamMQClient redisStreamMQClient(RedisStreamMQProperties properties) {
        return new RedisStreamMQClient(properties);
    }
}
