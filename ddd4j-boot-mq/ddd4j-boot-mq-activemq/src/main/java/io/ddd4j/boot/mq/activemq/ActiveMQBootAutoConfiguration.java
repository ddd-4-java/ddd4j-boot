package io.ddd4j.boot.mq.activemq;

import io.ddd4j.mq.activemq.ActiveMQClient;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ActiveMQ（Artemis）消息客户端的 Spring Boot 自动装配。
 *
 * <p>仅当 {@code ddd4j.mq.broker=activemq} 且 Artemis 客户端类存在时生效，
 * 并通过 {@link Ddd4jMQRegistrarConfiguration} 导入 MQ 通用注册配置。
 */
@AutoConfiguration
@ConditionalOnClass({ActiveMQClient.class, ActiveMQConnectionFactory.class})
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "activemq")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class ActiveMQBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public ActiveMQBootAutoConfiguration() {
    }

    /**
     * 装配 ActiveMQ 客户端，复用容器中已有的连接工厂。
     *
     * @param connectionFactory Artemis 连接工厂
     * @return ActiveMQ 客户端实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ActiveMQClient activeMQClient(ActiveMQConnectionFactory connectionFactory) {
        return new ActiveMQClient(connectionFactory);
    }
}
