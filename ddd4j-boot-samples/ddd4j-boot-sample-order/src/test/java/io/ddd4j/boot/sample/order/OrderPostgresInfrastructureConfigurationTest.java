package io.ddd4j.boot.sample.order;

import tools.jackson.databind.ObjectMapper;
import io.ddd4j.sample.order.application.IdempotencyPort;
import io.ddd4j.sample.order.application.IntegrationEventPublisher;
import io.ddd4j.sample.order.application.OrderApplicationService;
import io.ddd4j.sample.order.application.OrderReadModelPort;
import io.ddd4j.sample.order.application.OutboxPort;
import io.ddd4j.sample.order.jdbc.JdbcOrderReadModelPort;
import io.ddd4j.sample.order.jdbc.JdbcOutboxPort;
import io.ddd4j.sample.order.jdbc.TransactionalOutboxPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class OrderPostgresInfrastructureConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(OrderPostgresInfrastructureConfiguration.class, TestClientOverrides.class)
            .withPropertyValues(
                    "ddd4j.sample.order.infrastructure=postgres",
                    "ddd4j.sample.order.redis-enabled=false",
                    "ddd4j.sample.order.kafka-enabled=false",
                    "ddd4j.sample.order.outbox-scheduler-enabled=false"
            );

    private final ApplicationContextRunner fallbackContextRunner = new ApplicationContextRunner()
            .withUserConfiguration(OrderPostgresInfrastructureConfiguration.class, MinimalTestDependencies.class)
            .withPropertyValues(
                    "ddd4j.sample.order.infrastructure=postgres",
                    "ddd4j.sample.order.redis-enabled=false",
                    "ddd4j.sample.order.kafka-enabled=false",
                    "ddd4j.sample.order.outbox-scheduler-enabled=false"
            );

    @Test
    void shouldWireTheSharedOrderKernelToProductionPorts() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(OrderApplicationService.class);
            assertThat(context).hasSingleBean(TransactionalOutboxPublisher.class);
            assertThat(context.getBean(OutboxPort.class)).isInstanceOf(JdbcOutboxPort.class);
            assertThat(context.getBean(OrderReadModelPort.class)).isInstanceOf(JdbcOrderReadModelPort.class);
            assertThat(context).hasSingleBean(IdempotencyPort.class);
        });
    }

    @Test
    void shouldKeepTransactionsAvailableWhenRedisAndKafkaAreDisabled() {
        fallbackContextRunner.run(context -> {
            IdempotencyPort idempotency = context.getBean(IdempotencyPort.class);
            assertThat(idempotency.acquire("payment-1", Duration.ofMinutes(1))).isTrue();
            assertThat(idempotency.acquire("payment-1", Duration.ofMinutes(1))).isFalse();
            idempotency.release("payment-1");
            assertThat(idempotency.acquire("payment-1", Duration.ofMinutes(1))).isTrue();

            assertThat(context).hasSingleBean(OrderApplicationService.class);
            assertThat(context).hasSingleBean(TransactionalOutboxPublisher.class);
            assertThat(context).doesNotHaveBean(IntegrationEventPublisher.class);
        });
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class TestClientOverrides {

        @Bean
        DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MinimalTestDependencies {

        @Bean
        DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}
