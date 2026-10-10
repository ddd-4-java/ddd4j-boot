package io.ddd4j.boot.mq.nats;

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
import io.ddd4j.mq.nats.NatsMQClient;
import io.ddd4j.mq.nats.NatsProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link NatsMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>装配契约上下文同样触发真实建连：{@code NatsMQBootAutoConfiguration} 经
 * {@code @Import(Ddd4jMQRegistrarConfiguration)} 在上下文刷新期执行
 * {@code MQClient#init}（producer 为必选初始化项，fail-fast），
 * {@code NatsMQClient#initProducer} 立即打开 NATS 连接（缺 broker 即
 * {@code Open NATS connection failed}），因此默认装配与用户 Bean 覆盖两例与
 * {@link NatsMQClientIntegrationTest} 共用 Testcontainers NATS 单节点镜像
 * （JetStream 已启用，属性 {@code ddd4j.mq.nats.servers} 指向容器映射端口）；
 * 无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class NatsMQBootAutoConfigurationTest {

    @Container
    // 测试镜像来源: https://testcontainers.com/modules/nats/
    static final GenericContainer<?> NATS = new GenericContainer<>(DockerImageName.parse("nats:2.10.22"))
            .withCommand("-js")
            .withExposedPorts(4222)
            .waitingFor(Wait.forListeningPort());

    /**
     * 返回容器映射后的 NATS 服务器地址。
     *
     * @return {@code nats://host:port} 形式的 servers 连接串
     */
    private static String servers() {
        return "nats://" + NATS.getHost() + ":" + NATS.getMappedPort(4222);
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                    Ddd4jMQAutoConfiguration.class, NatsMQBootAutoConfiguration.class))
            .withPropertyValues(
                    "ddd4j.mq.enabled=true",
                    "ddd4j.mq.broker=nats",
                    // 急切建连契约：servers 必须指向容器，否则 initProducer 阶段即 fail-fast
                    "ddd4j.mq.nats.servers=" + servers());

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(NatsMQClient.class);
            assertThat(context).hasSingleBean(NatsProperties.class);
            // 属性绑定契约：容器连接参数必须已绑定到 NatsProperties
            assertThat(context.getBean(NatsProperties.class).getServers()).isEqualTo(servers());
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        // 回退路径不建连、不需要 broker，保持原有断言语义
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
        // 缺类回退不建连、不需要 broker，保持原有断言语义
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
            // 用户自定义 client 同样要经 registrar 初始化，配置指向容器地址才能完成必选 producer 预热
            NatsProperties properties = new NatsProperties();
            properties.setServers(servers());
            return new NatsMQClient(properties);
        }
    }
}
