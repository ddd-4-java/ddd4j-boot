package io.ddd4j.boot.mq.nats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.nats.NatsMQClient;
import io.ddd4j.mq.nats.NatsProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link NatsMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class NatsMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, NatsMQBootAutoConfiguration.class))
            .withUserConfiguration(DisabledMQInitializationConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=nats")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(NatsMQClient.class);
            assertThat(context).hasSingleBean(NatsProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(NatsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(NatsMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(NatsMQClient.class))
                .withConfiguration(AutoConfigurations.of(NatsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=nats")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(NatsMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(NatsMQClient.class))
                        .isSameAs(context.getBean("customNatsMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        NatsMQClient customNatsMQClient() {
            return new NatsMQClient(new NatsProperties());
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class DisabledMQInitializationConfiguration {


        @Bean
        @Primary
        MQProperties disabledMQProperties() {
            MQProperties properties = new MQProperties();
            properties.setEnabled(false);
            return properties;
        }
    }
}
