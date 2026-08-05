package io.ddd4j.boot.mq.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RabbitMQ broker 端到端集成测试（Testcontainers）。
 *
 * <p>覆盖：真实 RabbitMQ 上的 发布 → 路由 → 消费 → 反序列化 全链路。
 * 无 Docker 环境时自动跳过（{@code disabledWithoutDocker = true}），不伪报通过。
 *
 * <p>三个 broker 前置条件（上游 {@code RabbitMQClient} 不负责声明）：
 * <ul>
 *   <li><b>凭据</b>：上游 {@code RabbitMQProperties} 默认 username/password 为空串，
 *       空凭据必被 RabbitMQ 拒绝（PLAIN 认证失败），故 {@link #beforeAll} 用
 *       {@code rabbitmqctl} 显式创建用户并授权。不用 {@code withUser()}：
 *       Testcontainers 1.20.x 的 {@code withUser()} 只追加一条
 *       {@code rabbitmqadmin declare user ... tags=}（无 administrator 标签、无 vhost 权限），
 *       环境变量仍是默认 guest/guest，导致创建出的用户无法访问 vhost</li>
 *   <li><b>交换机</b>：默认 {@code exchange=""} 是 RabbitMQ 直连默认交换机，
 *       路由键必须等于队列名，而上游队列名是 {@code group.namespace.Class.method}，
 *       永远无法匹配 {@code namespace.topic.tag} 路由键；且上游 {@code initConsumer}
 *       无条件调用 {@code queueBind}，对默认交换机构会直接被 RabbitMQ 以 403 拒绝。
 *       故 exchange 必须指向预声明的 topic 交换机，且走全局 {@code ddd4j.mq.exchange}
 *       （init 阶段 {@code RabbitMQClient} 用的是注册器注入的全局 {@link MQProperties}）</li>
 *   <li><b>属性前缀</b>：连接参数（host/port/username/password）来自
 *       {@code RabbitMQProperties}（{@code ddd4j.mq.rabbitmq.*}，父类字段随子类前缀绑定），
 *       路由参数（exchange 等）来自全局 {@code ddd4j.mq.*}，两者相互独立</li>
 * </ul>
 */
@Testcontainers(disabledWithoutDocker = true)
class RabbitMQClientIntegrationTest {

    private static final String USER = "ddd4j";
    private static final String PASSWORD = "ddd4j-secret";
    private static final String EXCHANGE = "ddd4j-events";

    @Container
    static final RabbitMQContainer RABBIT = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.13-management"));

    @BeforeAll
    static void beforeAll() throws Exception {
        createUserAndPermissions();
        declareTopicExchange();
    }

    private static void createUserAndPermissions() throws Exception {
        RABBIT.execInContainer("rabbitmqctl", "add_user", USER, PASSWORD);
        RABBIT.execInContainer("rabbitmqctl", "set_user_tags", USER, "administrator");
        RABBIT.execInContainer("rabbitmqctl", "set_permissions", "-p", "/", USER, ".*", ".*", ".*");
    }

    private static void declareTopicExchange() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RABBIT.getHost());
        factory.setPort(RABBIT.getAmqpPort());
        factory.setUsername(USER);
        factory.setPassword(PASSWORD);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE, "topic", true);
        }
    }

    @Test
    void shouldPublishAndConsumeEventThroughRealRabbit() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, RabbitMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=rabbit",
                        "ddd4j.mq.exchange=" + EXCHANGE,
                        "ddd4j.mq.rabbitmq.host=" + RABBIT.getHost(),
                        "ddd4j.mq.rabbitmq.port=" + RABBIT.getAmqpPort(),
                        "ddd4j.mq.rabbitmq.username=" + USER,
                        "ddd4j.mq.rabbitmq.password=" + PASSWORD);

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：Testcontainers 连接参数必须已绑定到 RabbitMQProperties
            io.ddd4j.mq.rabbitmq.RabbitMQProperties properties =
                    context.getBean(io.ddd4j.mq.rabbitmq.RabbitMQProperties.class);
            assertThat(properties.getHost()).isEqualTo(RABBIT.getHost());
            assertThat(properties.getPort()).isEqualTo(RABBIT.getAmqpPort());
            assertThat(properties.getUsername()).isEqualTo(USER);
            // 路由契约：交换机来自全局 MQProperties（init 阶段 client 使用注册器注入的全局配置）
            io.ddd4j.mq.MQProperties global = context.getBean(io.ddd4j.mq.MQProperties.class);
            assertThat(global.getExchange()).isEqualTo(EXCHANGE);
            assertThat(global.getBroker()).isEqualTo("rabbit");

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic("order");
            event.setTag("created");
            event.setOrderId("R-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(30));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("R-001");
            assertThat(received.getTopic()).isEqualTo("order");
            assertThat(received.getTag()).isEqualTo("created");
        });
    }

    private static void await(java.util.function.BooleanSupplier condition, Duration timeout) throws InterruptedException {
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return;
            }
            Thread.sleep(200);
        }
        throw new AssertionError("condition not met within " + timeout);
    }

    @Configuration(proxyBeanMethods = false)
    static class TestListenerConfiguration {

        @Bean
        TestOrderListener testOrderListener() {
            return new TestOrderListener();
        }
    }

    public static class TestOrderListener {

        private final List<OrderCreatedEvent> received = new CopyOnWriteArrayList<>();

        @MQEventListener(topic = "order", tags = "created")
        public void onOrderCreated(OrderCreatedEvent event) {
            received.add(event);
        }

        public List<OrderCreatedEvent> received() {
            return received;
        }
    }

    public static class OrderCreatedEvent extends MQEvent {

        private String orderId;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
}
