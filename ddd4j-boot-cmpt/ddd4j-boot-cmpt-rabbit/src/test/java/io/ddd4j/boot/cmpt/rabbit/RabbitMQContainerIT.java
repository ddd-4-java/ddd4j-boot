package io.ddd4j.boot.cmpt.rabbit;

import io.ddd4j.boot.core.contract.MQEvent;
import io.ddd4j.boot.mq.contract.MQDestination;
import io.ddd4j.boot.mq.publish.MQEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * RabbitMQ 发布路径 Testcontainers 冒烟集成测试。
 */
@Testcontainers
@SpringBootTest(classes = RabbitMQContainerIT.TestApplication.class)
@EnabledIf("io.ddd4j.boot.cmpt.rabbit.RabbitMQContainerIT#isDockerAvailable")
class RabbitMQContainerIT {

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management");

    @Autowired
    private MQEventPublisher mqEventPublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("ddd4j.mq.enabled", () -> "true");
        registry.add("ddd4j.mq.broker", () -> "rabbit");
        registry.add("ddd4j.mq.namespace", () -> "it");
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbit::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbit::getAdminPassword);
    }

    /**
     * Docker 是否可用（Testcontainers 前置条件）。
     */
    static boolean isDockerAvailable() {
        try {
            DockerClientFactory.instance().client();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Test
    void publishShouldNotThrow() {
        assertNotNull(mqEventPublisher);
        assertNotNull(rabbitTemplate);

        DemoPublishEvent event = new DemoPublishEvent();
        event.setTopic("smoke");
        event.setTag("ping");
        event.setTenantId("tenant-it");

        assertDoesNotThrow(() -> mqEventPublisher.publish(
                event,
                MQDestination.of("smoke", "ping", "it")));
    }

    @SpringBootApplication
    @Import({
            io.ddd4j.boot.mq.config.Ddd4jMQAutoConfiguration.class,
            io.ddd4j.boot.cmpt.rabbit.autoconfigure.Ddd4jRabbitMQAutoConfiguration.class
    })
    static class TestApplication {
    }

    static class DemoPublishEvent extends MQEvent {
    }
}
