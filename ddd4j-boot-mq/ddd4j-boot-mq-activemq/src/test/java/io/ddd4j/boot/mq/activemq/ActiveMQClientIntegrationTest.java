package io.ddd4j.boot.mq.activemq;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
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
 * ActiveMQ Artemis broker 端到端集成测试（Testcontainers，apache/activemq-artemis 单节点）。
 *
 * <p>覆盖：真实 Artemis 上的 发布 → 目的地路由 → 消费 → 反序列化 全链路。
 * {@code ActiveMQBootAutoConfiguration} 不创建连接工厂（{@code @ConditionalOnMissingBean}
 * {@code ActiveMQClient(ActiveMQConnectionFactory)}），由测试以容器地址注入
 * Artemis 默认凭据（artemis/artemis）。无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = false)
class ActiveMQClientIntegrationTest {

    private static final String USER = "artemis";
    private static final String PASSWORD = "artemis";

    @Container
    static final GenericContainer<?> ARTEMIS = new GenericContainer<>(
            DockerImageName.parse("apache/activemq-artemis:2.33.0-alpine"))
            .withEnv("ARTEMIS_USER", USER)
            .withEnv("ARTEMIS_PASSWORD", PASSWORD)
            .withEnv("ANONYMOUS_LOGIN", "false")
            .withExposedPorts(61616)
            .waitingFor(Wait.forListeningPort());

    @Test
    void shouldPublishAndConsumeEventThroughRealArtemis() {
        String brokerUrl = "tcp://" + ARTEMIS.getHost() + ":" + ARTEMIS.getMappedPort(61616);
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, ActiveMQBootAutoConfiguration.class))
                .withUserConfiguration(TestInfrastructureConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=activemq",
                        "ddd4j.mq.activemq.broker-url=" + brokerUrl,
                        "ddd4j.mq.activemq.username=" + USER,
                        "ddd4j.mq.activemq.password=" + PASSWORD);

        runner.run(context -> {
            assertThat(context).hasNotFailed();

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic("order");
            event.setTag("created");
            event.setOrderId("A-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(30));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("A-001");
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
    static class TestInfrastructureConfiguration {

        @Bean
        ActiveMQConnectionFactory activeMQConnectionFactory() {
            return new ActiveMQConnectionFactory(
                    "tcp://" + ARTEMIS.getHost() + ":" + ARTEMIS.getMappedPort(61616),
                    USER, PASSWORD);
        }

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
