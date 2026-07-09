package io.ddd4j.boot.mq.core.config;

import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.event.MQEventSerialization;
import io.ddd4j.mq.serialization.JsonMQEventSerialization;
import io.ddd4j.mq.spring.config.Ddd4jMQPropertiesConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * ddd4j 消息队列 Spring Boot 自动配置。
 *
 * <p>导入 ddd4j-mq-spring 的 {@link Ddd4jMQPropertiesConfiguration}（属性绑定 + 序列化器），
 * 本类仅负责：
 * <ul>
 *   <li>绑定 {@link MQProperties} 配置属性</li>
 *   <li>注册默认 {@link MQEventSerialization} Bean</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 2.0.x
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MQProperties.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "enabled", havingValue = "true")
@Import(Ddd4jMQPropertiesConfiguration.class)
public class Ddd4jMQAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MQEventSerialization.class)
    public MQEventSerialization mqEventSerialization() {
        return new JsonMQEventSerialization();
    }

}
