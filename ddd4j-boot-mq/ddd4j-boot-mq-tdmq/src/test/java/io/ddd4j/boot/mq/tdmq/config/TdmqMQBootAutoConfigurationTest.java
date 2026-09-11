package io.ddd4j.boot.mq.tdmq.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.tdmq.TdmqMQClient;
import io.ddd4j.mq.tdmq.TdmqProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link TdmqMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class TdmqMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, TdmqMQBootAutoConfiguration.class))
            .withUserConfiguration(DisabledMQInitializationConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=tdmq")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(TdmqMQClient.class);
            assertThat(context).hasSingleBean(TdmqProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(TdmqMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(TdmqMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(TdmqMQClient.class))
                .withConfiguration(AutoConfigurations.of(TdmqMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=tdmq")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(TdmqMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(TdmqMQClient.class))
                        .isSameAs(context.getBean("customTdmqMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        TdmqMQClient customTdmqMQClient() {
            return new TdmqMQClient(new TdmqProperties());
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
