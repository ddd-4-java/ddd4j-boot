package io.ddd4j.boot.mq.nats;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.nats.NatsMQClient;
import io.ddd4j.mq.nats.NatsProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link NatsMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 */
@Testcontainers
class NatsMQBootAutoConfigurationTest {

    @Container
    static final GenericContainer<?> NATS = new GenericContainer<>(DockerImageName.parse("nats:2.10.22"))
            .withCommand("-js")
            .withExposedPorts(4222)
            .waitingFor(Wait.forListeningPort());

    private ApplicationContextRunner runner() {
        return new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, NatsMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.enabled=true", "ddd4j.mq.broker=nats",
                        "ddd4j.mq.nats.servers=nats://" + NATS.getHost() + ":" + NATS.getMappedPort(4222));
    }

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner().run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(NatsMQClient.class);
            assertThat(context).hasSingleBean(NatsProperties.class);
            assertThat(context.getBean(NatsProperties.class).getServers())
                    .isEqualTo("nats://" + NATS.getHost() + ":" + NATS.getMappedPort(4222));
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
        runner().withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(NatsMQClient.class))
                        .isSameAs(context.getBean("customNatsMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        NatsMQClient customNatsMQClient(NatsProperties properties) {
            return new NatsMQClient(properties);
        }
    }
}
