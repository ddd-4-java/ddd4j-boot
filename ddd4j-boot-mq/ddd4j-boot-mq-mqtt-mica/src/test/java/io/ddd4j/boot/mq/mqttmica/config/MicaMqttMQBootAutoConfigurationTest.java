package io.ddd4j.boot.mq.mqttmica.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.mqttmica.MicaMqttMQClient;
import io.ddd4j.mq.mqttmica.MicaMqttProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MicaMqttMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class MicaMqttMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, MicaMqttMQBootAutoConfiguration.class))
            .withUserConfiguration(DisabledMQInitializationConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=mqtt-mica")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(MicaMqttMQClient.class);
            assertThat(context).hasSingleBean(MicaMqttProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MicaMqttMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MicaMqttMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(MicaMqttMQClient.class))
                .withConfiguration(AutoConfigurations.of(MicaMqttMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=mqtt-mica")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MicaMqttMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(MicaMqttMQClient.class))
                        .isSameAs(context.getBean("customMicaMqttMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        MicaMqttMQClient customMicaMqttMQClient() {
            return new MicaMqttMQClient(new MicaMqttProperties());
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
