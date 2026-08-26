package io.ddd4j.boot.mq.nats;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NATS broker 端到端集成测试（Testcontainers，nats:2.10 单节点）。
 *
 * <p>覆盖：真实 NATS 上的 发布 → subject 路由 → 消费 → 反序列化 全链路。
 * 上游 {@code NatsMQClient} JetStream 优先、失败回落 core NATS——单节点 nats:2.10
 * 未启用 JetStream，正好覆盖回落路径。无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class NatsMQClientIntegrationTest {

    @Container
    static final GenericContainer<?> NATS = new GenericContainer<>(DockerImageName.parse("nats:2.10.22"))
            .withExposedPorts(4222)
            .waitingFor(Wait.forListeningPort());

    @Test
    void shouldPublishAndConsumeEventThroughRealNats() {
        String servers = "nats://" + NATS.getHost() + ":" + NATS.getMappedPort(4222);
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, NatsMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=nats",
                        "ddd4j.mq.nats.servers=" + servers);

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：容器连接参数必须已绑定到 NatsProperties
            io.ddd4j.mq.nats.NatsProperties properties =
                    context.getBean(io.ddd4j.mq.nats.NatsProperties.class);
            assertThat(properties.getServers()).isEqualTo(servers);

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic("order");
            event.setTag("created");
            event.setOrderId("N-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(30));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("N-001");
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
