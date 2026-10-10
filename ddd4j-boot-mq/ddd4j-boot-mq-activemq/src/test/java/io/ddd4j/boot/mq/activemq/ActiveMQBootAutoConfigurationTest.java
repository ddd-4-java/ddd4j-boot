package io.ddd4j.boot.mq.activemq;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
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
import io.ddd4j.mq.activemq.ActiveMQClient;
import io.ddd4j.mq.activemq.ActiveMQProperties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ActiveMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>装配契约上下文同样触发真实建连：{@code ActiveMQBootAutoConfiguration} 经
 * {@code @Import(Ddd4jMQRegistrarConfiguration)} 在上下文刷新期执行
 * {@code MQClient#init}（producer 为必选初始化项，fail-fast），
 * 因此默认装配与用户 Bean 覆盖两例与 {@link ActiveMQClientIntegrationTest} 共用
 * Testcontainers Artemis 单节点镜像；无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class ActiveMQBootAutoConfigurationTest {

    private static final String USER = "artemis";
    private static final String PASSWORD = "artemis";

    @Container
    static final GenericContainer<?> ARTEMIS = new GenericContainer<>(
            // 测试镜像来源: https://testcontainers.com/modules/artemis/
            DockerImageName.parse("apache/activemq-artemis:2.33.0-alpine"))
            .withEnv("ARTEMIS_USER", USER)
            .withEnv("ARTEMIS_PASSWORD", PASSWORD)
            .withEnv("ANONYMOUS_LOGIN", "false")
            .withExposedPorts(61616)
            .waitingFor(Wait.forListeningPort());

    /**
     * 返回容器映射后的 Artemis 连接地址。
     *
     * @return {@code tcp://host:port} 形式的 broker URL
     */
    private static String brokerUrl() {
        return "tcp://" + ARTEMIS.getHost() + ":" + ARTEMIS.getMappedPort(61616);
    }

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
        ActiveMQConnectionFactory activeMQConnectionFactory() {
            return new ActiveMQConnectionFactory(brokerUrl(), USER, PASSWORD);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        ActiveMQClient customActiveMQClient() {
            // 用户自定义 client 同样要经 registrar 初始化，配置指向容器地址才能完成必选 producer 预热
            ActiveMQProperties properties = new ActiveMQProperties();
            properties.setBrokerUrl(brokerUrl());
            properties.setUsername(USER);
            properties.setPassword(PASSWORD);
            return new ActiveMQClient(properties);
        }
    }
}
