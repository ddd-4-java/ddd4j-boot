package io.ddd4j.boot.mq.activemq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.activemq.ActiveMQClient;
import io.ddd4j.mq.activemq.ActiveMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ActiveMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class ActiveMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, ActiveMQBootAutoConfiguration.class))
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=activemq")

            .withUserConfiguration(ConnectionFactoryConfiguration.class);

    @Test
    void defaultAssemblyShouldCreateClient() {
        // ActiveMQ 薄适配只注册 client Bean（ConnectionFactory 由 Spring Boot 装配），
        // 不额外注册 ActiveMQProperties Bean，故仅断言 client。
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ActiveMQClient.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ActiveMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(ActiveMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(ActiveMQClient.class))
                .withConfiguration(AutoConfigurations.of(ActiveMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=activemq")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(ActiveMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(ActiveMQClient.class))
                        .isSameAs(context.getBean("customActiveMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class ConnectionFactoryConfiguration {

        @Bean
        org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory activeMQConnectionFactory() {
            return new org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory();
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        ActiveMQClient customActiveMQClient() {
            return new ActiveMQClient(new ActiveMQProperties());
        }
    }
}
