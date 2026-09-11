package io.ddd4j.boot.mq.rocketmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.rocketmq.RocketMQClient;
import io.ddd4j.mq.rocketmq.RocketMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link RocketMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class RocketMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, RocketMQBootAutoConfiguration.class))
            .withUserConfiguration(DisabledMQInitializationConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=rocket")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(RocketMQClient.class);
            assertThat(context).hasSingleBean(RocketMQProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RocketMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(RocketMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(RocketMQClient.class))
                .withConfiguration(AutoConfigurations.of(RocketMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=rocket")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(RocketMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(RocketMQClient.class))
                        .isSameAs(context.getBean("customRocketMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        RocketMQClient customRocketMQClient() {
            return new RocketMQClient(new RocketMQProperties());
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
