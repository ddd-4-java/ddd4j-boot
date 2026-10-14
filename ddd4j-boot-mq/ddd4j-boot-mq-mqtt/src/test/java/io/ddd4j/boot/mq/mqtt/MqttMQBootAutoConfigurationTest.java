package io.ddd4j.boot.mq.mqtt;

import java.net.ConnectException;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.lifecycle.MQStartupState;
import io.ddd4j.mq.mqtt.MqttMQClient;
import io.ddd4j.mq.mqtt.MqttMQProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MQTT 自动装配和必需生产者连接契约；真实 broker 由测试容器提供。
 */
@Testcontainers(disabledWithoutDocker = false)
class MqttMQBootAutoConfigurationTest {

    @Container
    static final GenericContainer<?> MOSQUITTO = new GenericContainer<>(
            DockerImageName.parse("eclipse-mosquitto:2.0.22"))
            .withCopyToContainer(Transferable.of(
                    "listener 1883 0.0.0.0\nallow_anonymous true\npersistence false\n", 0644),
                    "/mosquitto/config/mosquitto.conf")
            .withExposedPorts(1883)
            .waitingFor(Wait.forListeningPort())
            .withStartupTimeout(Duration.ofMinutes(2));

    private static String brokerUri() {
        return "tcp://" + MOSQUITTO.getHost() + ":" + MOSQUITTO.getMappedPort(1883);
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class,
                    Ddd4jMQAutoConfiguration.class, MqttMQBootAutoConfiguration.class))
            .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=mqtt",
                    "ddd4j.mq.mqtt.server-uri=" + brokerUri());

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(MqttMQClient.class);
            assertThat(context).hasSingleBean(MqttMQProperties.class);
            assertThat(context.getBean(MqttMQProperties.class).getServerUri()).isEqualTo(brokerUri());
            assertThat(context.getBean(MqttMQClient.class).startupStatus().snapshot().state())
                    .isEqualTo(MQStartupState.READY);
        });
    }

    @Test
    void requiredProducerConnectionFailureShouldAbortStartup() {
        runner.withPropertyValues("ddd4j.mq.mqtt.server-uri=tcp://127.0.0.1:1")
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context.getStartupFailure()).hasRootCauseInstanceOf(ConnectException.class);
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
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context.getBean(MqttMQClient.class))
                            .isSameAs(context.getBean("customMqttMQClient"));
                    assertThat(context.getBean(MqttMQClient.class).startupStatus().snapshot().state())
                            .isEqualTo(MQStartupState.READY);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        MqttMQClient customMqttMQClient(MqttMQProperties properties) {
            return new MqttMQClient(properties);
        }
    }
}
