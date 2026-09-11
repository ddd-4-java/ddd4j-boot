package io.ddd4j.boot.mq.mqtt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.MQProperties;
import io.ddd4j.mq.mqtt.MqttMQClient;
import io.ddd4j.mq.mqtt.MqttMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MqttMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class MqttMQBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMQAutoConfiguration.class, MqttMQBootAutoConfiguration.class))
            .withUserConfiguration(DisabledMQInitializationConfiguration.class)
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=mqtt")
;

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(MqttMQClient.class);
            assertThat(context).hasSingleBean(MqttMQProperties.class);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MqttMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MqttMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(MqttMQClient.class))
                .withConfiguration(AutoConfigurations.of(MqttMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=mqtt")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MqttMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(MqttMQClient.class))
                        .isSameAs(context.getBean("customMqttMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        MqttMQClient customMqttMQClient() {
            return new MqttMQClient(new MqttMQProperties());
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
