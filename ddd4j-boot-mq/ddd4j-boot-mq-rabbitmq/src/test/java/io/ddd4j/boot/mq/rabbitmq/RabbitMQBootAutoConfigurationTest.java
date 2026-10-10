package io.ddd4j.boot.mq.rabbitmq;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.rabbitmq.RabbitMQClient;
import io.ddd4j.mq.rabbitmq.RabbitMQProperties;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link RabbitMQBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：broker 匹配时默认装配 / broker 不匹配回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>装配契约上下文同样触发真实建连：{@code RabbitMQBootAutoConfiguration} 经
 * {@code @Import(Ddd4jMQRegistrarConfiguration)} 在上下文刷新期由
 * {@code MQListenerRegistrar#onContextRefreshed} 执行 {@code MQClient#init}
 * （producer 为必选初始化项，fail-fast），因此默认装配与用户 Bean 覆盖两例复用
 * {@link RabbitMQClientIntegrationTest} 同款 Testcontainers RabbitMQ 容器
 * （同镜像、同凭据创建策略）；无 Docker 环境时自动跳过，不伪报通过。
 *
 * <p>契约测试无 {@code @MQEventListener} 方法，消费者初始化（queueBind 等 broker 拓扑）
 * 不会执行，故容器只需可达 + 凭据有效，无需预声明交换机。两个 backoff 用例不触发装配，
 * 不依赖 broker，保持原样。
 *
 * <p>{@code ConfigurationPropertiesAutoConfiguration} 提供 {@code @ConfigurationProperties}
 * 绑定基础设施，使 {@code ddd4j.mq.rabbitmq.*}（host/port/username/password，父类字段随
 * 子类前缀绑定）真正落到 {@link RabbitMQProperties}，与集成测试装配方式一致。
 */
@Testcontainers(disabledWithoutDocker = true)
class RabbitMQBootAutoConfigurationTest {

    private static final String USER = "ddd4j";
    private static final String PASSWORD = "ddd4j-secret";

    @Container
    static final RabbitMQContainer RABBIT = new RabbitMQContainer(
            // 测试镜像来源: https://testcontainers.com/modules/rabbitmq/
            DockerImageName.parse("rabbitmq:3.13-management"));

    @BeforeAll
    static void beforeAll() throws Exception {
        // 与集成测试同一套凭据策略：官方镜像默认 guest/guest 仅允许 localhost 访问，
        // 宿主机经映射端口接入会被 RabbitMQ 拒绝，故显式创建业务用户并授权。
        // 不用 withUser()：Testcontainers 1.20.x 的 withUser() 只追加一条
        // rabbitmqadmin declare user（无 administrator 标签、无 vhost 权限），
        // 环境变量仍是默认 guest/guest，创建出的用户无法访问 vhost（见集成测试注释）。
        RABBIT.execInContainer("rabbitmqctl", "add_user", USER, PASSWORD);
        RABBIT.execInContainer("rabbitmqctl", "set_user_tags", USER, "administrator");
        RABBIT.execInContainer("rabbitmqctl", "set_permissions", "-p", "/", USER, ".*", ".*", ".*");
    }

    /**
     * 返回容器映射后的 AMQP 端口。
     *
     * @return {@code 5672} 在宿主机的映射端口
     */
    private static int amqpPort() {
        return RABBIT.getAmqpPort();
    }

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    ConfigurationPropertiesAutoConfiguration.class,
                    Ddd4jMQAutoConfiguration.class, RabbitMQBootAutoConfiguration.class))
            .withPropertyValues(
                    "ddd4j.mq.enabled=true", "ddd4j.mq.broker=rabbit",
                    // 连接参数绑定到 RabbitMQProperties，指向容器映射地址与 @BeforeAll 创建的凭据，
                    // 上下文刷新期的急切建连（initProducer fail-fast）才能成功
                    "ddd4j.mq.rabbitmq.host=" + RABBIT.getHost(),
                    "ddd4j.mq.rabbitmq.port=" + amqpPort(),
                    "ddd4j.mq.rabbitmq.username=" + USER,
                    "ddd4j.mq.rabbitmq.password=" + PASSWORD);

    @Test
    void defaultAssemblyShouldCreateClientAndProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(RabbitMQClient.class);
            assertThat(context).hasSingleBean(RabbitMQProperties.class);
            // 属性绑定契约：容器连接参数必须已绑定到 RabbitMQProperties（急切建连依赖该绑定）
            RabbitMQProperties properties = context.getBean(RabbitMQProperties.class);
            assertThat(properties.getHost()).isEqualTo(RABBIT.getHost());
            assertThat(properties.getPort()).isEqualTo(amqpPort());
            assertThat(properties.getUsername()).isEqualTo(USER);
        });
    }

    @Test
    void shouldBackOffWhenBrokerPropertyMismatch() {
        // 回退路径不建连、不需要 broker，保持原有断言语义
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(RabbitMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=other")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(RabbitMQClient.class);
                });
    }

    @Test
    void shouldBackOffWhenClientClassMissing() {
        // 缺类回退不建连、不需要 broker，保持原有断言语义
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(RabbitMQClient.class))
                .withConfiguration(AutoConfigurations.of(RabbitMQBootAutoConfiguration.class))
                .withPropertyValues("ddd4j.mq.broker=rabbit")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(RabbitMQClient.class);
                });
    }

    @Test
    void customClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomClientConfiguration.class)
                .run(context -> assertThat(context.getBean(RabbitMQClient.class))
                        .isSameAs(context.getBean("customRabbitMQClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomClientConfiguration {

        @Bean
        RabbitMQClient customRabbitMQClient() {
            // 用户自定义 client 同样要经 registrar 初始化（急切建连），
            // 连接参数指向容器地址与有效凭据才能完成必选 producer 预热
            RabbitMQProperties properties = new RabbitMQProperties();
            properties.setHost(RABBIT.getHost());
            properties.setPort(amqpPort());
            properties.setUsername(USER);
            properties.setPassword(PASSWORD);
            return new RabbitMQClient(properties);
        }
    }
}
