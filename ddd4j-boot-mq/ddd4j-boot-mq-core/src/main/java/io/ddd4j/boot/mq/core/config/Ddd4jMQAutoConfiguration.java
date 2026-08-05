package io.ddd4j.boot.mq.core.config;

import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.event.MQEventSerialization;
import io.ddd4j.mq.serialization.JsonMQEventSerialization;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * ddd4j 消息队列 Spring Boot 自动配置。
 *
 * <p>本类承担 MQ 的统一开关与属性绑定：
 * <ul>
 *   <li>以 {@code ddd4j.mq.enabled=true} 作为整组装配开关</li>
 *   <li>用 {@link Binder} 将 {@code ddd4j.mq.*} 绑定到 {@link MQProperties}（上游 {@link MQProperties}
 *       是零 Spring 依赖纯 POJO，不标注 {@code @ConfigurationProperties}，因此既不能用
 *       {@code @EnableConfigurationProperties}（启动期抛 "No ConfigurationProperties annotation found"），
 *       也不导入上游 {@code Ddd4jMQPropertiesConfiguration}（其 {@code mqEventStorer} 工厂方法
 *       {@code ObjectProvider<MQEventStorer<?>>.getIfAvailable()} 自引用正在创建的自身 Bean，
 *       存在循环依赖，MQ 一经启用必然启动失败）。字段默认值由 POJO 自身初始化保证。</li>
 *   <li>注册默认 {@link MQEventSerialization} Bean，用户可通过 {@code @ConditionalOnMissingBean} 覆盖</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @since 2.0.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MQProperties.class, MQEventSerialization.class})
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "enabled", havingValue = "true")
public class Ddd4jMQAutoConfiguration {

    @Bean
    @Primary
    @ConditionalOnMissingBean(MQProperties.class)
    public MQProperties ddd4jMQProperties(Environment environment) {
        MQProperties properties = new MQProperties();
        Binder.get(environment).bind("ddd4j.mq", Bindable.ofInstance(properties));
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean(MQEventSerialization.class)
    public MQEventSerialization mqEventSerialization() {
        return new JsonMQEventSerialization();
    }

}
