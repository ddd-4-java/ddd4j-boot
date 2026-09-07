package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.mq.redisstream.RedisStreamMQClient;
import io.ddd4j.mq.redisstream.RedisStreamMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.context.annotation.Configuration;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisStreamMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "redisStream")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class RedisStreamMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.redis-stream")
    public RedisStreamMQProperties redisStreamMQProperties() {
        return new RedisStreamMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RedisStreamMQClient redisStreamMQClient(RedisStreamMQProperties properties) {
        return new RedisStreamMQClient(properties);
    }
}
