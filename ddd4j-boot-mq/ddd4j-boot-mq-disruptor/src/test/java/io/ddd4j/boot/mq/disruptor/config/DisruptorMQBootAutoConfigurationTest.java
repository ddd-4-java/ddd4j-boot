package io.ddd4j.boot.mq.disruptor.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.disruptor.DisruptorMQClient;
import io.ddd4j.mq.disruptor.DisruptorMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DisruptorMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class DisruptorMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, DisruptorMQBootAutoConfiguration.class))
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=disruptor")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(DisruptorMQClient.class);
            assertThat(context).hasSingleBean(DisruptorMQProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(DisruptorMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(DisruptorMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(DisruptorMQClient.class))
                .withConfiguration(AutoConfigurations.of(DisruptorMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=disruptor")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(DisruptorMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(DisruptorMQClient.class))
                        .isSameAs(context.getBean("customDisruptorMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        DisruptorMQClient customDisruptorMQClient() {
            return new DisruptorMQClient(new DisruptorMQProperties());
        }
    }
}
