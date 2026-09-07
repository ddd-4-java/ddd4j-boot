package io.ddd4j.boot.mq.activemq;

import io.ddd4j.mq.activemq.ActiveMQClient;
import io.ddd4j.mq.spring.config.Ddd4jMQRegistrarConfiguration;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ActiveMQClient.class, ActiveMQConnectionFactory.class})
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "activemq")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class ActiveMQBootAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ActiveMQClient activeMQClient(ActiveMQConnectionFactory connectionFactory) {
        return new ActiveMQClient(connectionFactory);
    }
}
