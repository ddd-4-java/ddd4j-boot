package io.ddd4j.boot.mq.rocketmq;

import io.ddd4j.mq.rocketmq.RocketMQClient;
import io.ddd4j.mq.rocketmq.RocketMQProperties;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-boot RocketMQ 自动配置（薄适配）。
 *
 * <p>仅做两件事：
 * <ol>
 *   <li>绑定 {@code ddd4j.mq.rocketmq.*} 到 {@link RocketMQProperties}</li>
 *   <li>注册上游 {@link RocketMQClient} Bean 并导入 {@link Ddd4jMQRegistrarConfiguration}
 *       驱动 {@code @MQEventListener} 扫描与装配</li>
 * </ol>
 *
 * <p>Publisher / Listener / Ack / Producer / Consumer 均由上游 ddd4j-mq-rocketmq 提供，boot 侧不重复实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 3.4.x
 */
@AutoConfiguration
@ConditionalOnClass(RocketMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "rocket")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class RocketMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "ddd4j.mq.rocketmq")
    public RocketMQProperties rocketMQProperties() {
        return new RocketMQProperties();
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public RocketMQClient rocketMQClient(RocketMQProperties properties) {
        return new RocketMQClient(properties);
    }
}