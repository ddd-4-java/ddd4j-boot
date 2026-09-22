package io.ddd4j.boot.mq.mqttmica;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.boot.mq.mqttmica.config.MicaMqttMQBootAutoConfiguration;
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
 * MQTT broker 端到端集成测试（Testcontainers，eclipse-mosquitto 1.x）。
 *
 * <p>覆盖：mica-mqtt 客户端在真实 MQTT 上的 发布 → topic 订阅 → 消费 → 反序列化 全链路。
 * 选 mosquitto 1.6：默认配置即允许匿名连接（2.x 需显式 allow_anonymous）。
 * 无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class MicaMqttMQClientIntegrationTest {

    @Container
    static final GenericContainer<?> MOSQUITTO = new GenericContainer<>(
            DockerImageName.parse("eclipse-mosquitto:1.6.15"))
            .withExposedPorts(1883)
            .waitingFor(Wait.forListeningPort());

    @Test
    void shouldPublishAndConsumeEventThroughRealMqtt() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, MicaMqttMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=mqtt-mica",
                        "ddd4j.mq.mqtt-mica.server-ip=" + MOSQUITTO.getHost(),
                        "ddd4j.mq.mqtt-mica.port=" + MOSQUITTO.getMappedPort(1883));

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：容器连接参数必须已绑定到 MicaMqttProperties
            io.ddd4j.mq.mqttmica.MicaMqttProperties properties =
                    context.getBean(io.ddd4j.mq.mqttmica.MicaMqttProperties.class);
            assertThat(properties.getServerIp()).isEqualTo(MOSQUITTO.getHost());
            assertThat(properties.getPort()).isEqualTo(MOSQUITTO.getMappedPort(1883));

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic("order");
            event.setTag("created");
            event.setOrderId("M-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(30));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("M-001");
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
