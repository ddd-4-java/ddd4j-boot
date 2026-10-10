package io.ddd4j.boot.mq.mqtt;

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
import io.ddd4j.mq.mqtt.MqttMQClient;
import io.ddd4j.mq.mqtt.MqttMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MqttMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>装配契约上下文同样触发真实建连：{@code MqttMQBootAutoConfiguration} 经
 * {@code @Import(Ddd4jMQRegistrarConfiguration)} 在上下文刷新期执行
 * {@code MQClient#init}（producer 为必选初始化项，fail-fast），
 * 因此默认装配与用户 Bean 覆盖两例的属性必须指向真实 MQTT broker，
 * 本类与同仓 mica 模块共用 Testcontainers Mosquitto 单节点镜像；
 * 无 Docker 环境时自动跳过，不伪报通过。
 *
 * <p>镜像选型：选 {@code eclipse-mosquitto:1.6.15} 而非 2.x——2.x 默认
 * {@code allow_anonymous=false} 且需向容器挂载最小配置文件，1.x 默认配置即
 * 监听 1883 并允许匿名连接（Paho 连接无需凭据）；且该镜像与本仓
 * {@code ddd4j-boot-mq-mqtt-mica} 既有集成测试同款，已在本环境验证可连。
 */
@Testcontainers(disabledWithoutDocker = true)
class MqttMQBootAutoConfigurationTest {

    @Container
    static final GenericContainer<?> MOSQUITTO = new GenericContainer<>(
            // 测试镜像来源: https://testcontainers.com/modules/mosquitto/
            DockerImageName.parse("eclipse-mosquitto:1.6.15"))
            .withExposedPorts(1883)
            .waitingFor(Wait.forListeningPort());

    /**
     * 返回容器映射后的 MQTT broker 地址。
     *
     * <p>Mosquitto 1.x 默认允许匿名连接，无需配置用户名/密码凭据
     * （{@link MqttMQProperties#connectOptions()} 仅在 username/password
     * 非空时才注入 {@code MqttConnectOptions}）。
     *
     * @return {@code tcp://host:port} 形式的 broker URI
     */
    private static String serverUri() {
        return "tcp://" + MOSQUITTO.getHost() + ":" + MOSQUITTO.getMappedPort(1883);
    }

    /**
     * 契约装配 runner：除模板的两个 auto-config 外，还导入
     * {@link ConfigurationPropertiesAutoConfiguration}——{@code mqttMQProperties}
     * 的 {@code @ConfigurationProperties("ddd4j.mq.mqtt")} 绑定依赖其注册的
     * BindingPostProcessor，否则 {@code server-uri} 无法绑定到属性 Bean，
     * 客户端将回落到默认 {@code tcp://localhost:1883} 建连失败。
     */
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration.class,
                    Ddd4jMQAutoConfiguration.class, MqttMQBootAutoConfiguration.class))
            .withPropertyValues(
                    "ddd4j.mq.enabled=true",
                    "ddd4j.mq.broker=mqtt",
                    "ddd4j.mq.mqtt.server-uri=" + serverUri());

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        // 默认装配经 registrar 急切执行 MQClient#init（producer fail-fast），
        // 属性已指向容器映射端口，建连成功上下文才刷新完成。
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(MqttMQClient.class);
            assertThat(context).hasSingleBean(MqttMQProperties.class);
            // 属性绑定契约：容器地址必须已绑定到 MqttMQProperties
            assertThat(context.getBean(MqttMQProperties.class).getServerUri())
                    .isEqualTo(serverUri());
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        // broker 不匹配：不装配 client，无建连动作，不需要 broker。
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
        // 类路径缺 MqttMQClient：@ConditionalOnClass 不满足，无建连动作，不需要 broker。
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
        // 用户自定义 client 同样要经 registrar 急切初始化，属性指向容器地址才能完成必选 producer 预热。
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(MqttMQClient.class))
                        .isSameAs(context.getBean("customMqttMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        MqttMQClient customMqttMQClient() {
            MqttMQProperties properties = new MqttMQProperties();
            properties.setServerUri(serverUri());
            return new MqttMQClient(properties);
        }
    }
}
