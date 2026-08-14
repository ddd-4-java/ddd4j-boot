package io.ddd4j.boot.mq.pulsar;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import io.ddd4j.mq.pulsar.PulsarMQClient;
import io.ddd4j.mq.pulsar.PulsarProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Apache Pulsar broker end-to-end integration test (Testcontainers).
 *
 * <p>Covers: publish -> route -> consume -> deserialize full chain on real Pulsar standalone.
 * Automatically skipped when Docker is unavailable ({@code disabledWithoutDocker = true}).
 *
 * <p>Pulsar standalone startup takes 30-60s; wait strategy uses log message with generous timeout.
 * The {@code ddd4j.mq.pulsar.namespace=default} property is CRITICAL: empty default namespace produces
 * invalid physical topic {@code public//order}.
 *
 * <p>Note on tag/topic symmetry: upstream {@code PulsarMQClient} producer resolves physical topic
 * as {@code tenant/namespace/topic[:tag]} (tag included), while consumer subscribes to
 * {@code tenant/namespace/topic} (tag excluded, app-layer filtering via message property).
 * These are distinct Pulsar topics, so the test omits tag to ensure producer/consumer topic match.
 */
@Testcontainers(disabledWithoutDocker = true)
class PulsarMQClientIntegrationTest {

    @Container
    static final GenericContainer<?> PULSAR = new GenericContainer<>(DockerImageName.parse("apachepulsar/pulsar:3.2.0"))
            .withCommand("bin/pulsar", "standalone")
            .withExposedPorts(6650)
            .waitingFor(Wait.forLogMessage(".*messaging service is ready.*", 1)
                    .withStartupTimeout(Duration.ofSeconds(180)));

    @Test
    void shouldPublishAndConsumeEventThroughRealPulsar() {
        String serviceUrl = "pulsar://" + PULSAR.getHost() + ":" + PULSAR.getMappedPort(6650);

        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class,
                        PulsarMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=pulsar",
                        "ddd4j.mq.pulsar.namespace=default",
                        "ddd4j.mq.pulsar.service-url=" + serviceUrl);

        runner.run(context -> {
            assertThat(context).hasNotFailed();

            // Verify properties binding
            PulsarProperties properties = context.getBean(PulsarProperties.class);
            assertThat(properties.getServiceUrl()).isEqualTo(serviceUrl);

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic("order");
            event.setOrderId("P-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(60));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("P-001");
            assertThat(received.getTopic()).isEqualTo("order");

            // Explicitly close PulsarMQClient to avoid JVM hang from non-daemon threads
            context.getBean(PulsarMQClient.class).close();
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

        @MQEventListener(topic = "order")
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
