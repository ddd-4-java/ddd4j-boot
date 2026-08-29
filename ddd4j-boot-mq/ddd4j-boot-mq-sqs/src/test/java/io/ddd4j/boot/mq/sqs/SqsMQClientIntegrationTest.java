package io.ddd4j.boot.mq.sqs;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import io.ddd4j.mq.listener.MQListener;
import io.ddd4j.mq.spring.registry.MQListenerBeanPostProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * AWS SQS 端到端集成测试（Testcontainers LocalStack SQS）。
 *
 * <p>覆盖：LocalStack SQS 上的 发送 → 长轮询消费 → 反序列化 全链路。
 * 上游 {@code SqsMQClient} 契约：{@code MQListener.topic} 与事件的 topic 均为
 * 完整 queueUrl。注解 topic 必须是编译期常量，而容器端口动态分配后才可知，
 * 故注解值用占位符常量，由 {@link QueueUrlRewriter} 在上下文就绪（早于
 * {@code MQListenerRegistrar} 装配）时改写为运行时 queueUrl——容器端口动态分配，
 * 无固定端口冲突。无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class SqsMQClientIntegrationTest {

    private static final String QUEUE_NAME = "ddd4j-events";

    /** 占位符（编译期常量，供注解引用），实际 URL 由 QueueUrlRewriter 以动态端口改写。 */
    private static final String QUEUE_URL_PLACEHOLDER = "${test.sqs.queue-url}";

    private static final String ACCESS_KEY = "test";

    private static final String SECRET_KEY = "test";

    @Container
    static final LocalStackContainer LOCALSTACK = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.8.0"))
            .withServices(LocalStackContainer.Service.SQS);

    /** 运行时 queueUrl：动态端口 + LocalStack 固定账号段 000000000000。 */
    private static String queueUrl() {
        return endpoint() + "/000000000000/" + QUEUE_NAME;
    }

    private static URI endpoint() {
        return LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.SQS);
    }

    @BeforeAll
    static void createQueue() {
        try (SqsClient sqs = sqsClient()) {
            sqs.createQueue(CreateQueueRequest.builder().queueName(QUEUE_NAME).build());
        }
    }

    private static SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(endpoint())
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .build();
    }

    @Test
    void shouldPublishAndConsumeEventThroughLocalStackSqs() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, SqsMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=sqs",
                        "ddd4j.mq.sqs.endpoint-override=" + endpoint(),
                        "ddd4j.mq.sqs.region=us-east-1",
                        "ddd4j.mq.sqs.access-key=" + ACCESS_KEY,
                        "ddd4j.mq.sqs.secret-key=" + SECRET_KEY,
                        "ddd4j.mq.sqs.wait-time-seconds=2");

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：LocalStack 连接参数必须已绑定到 SqsProperties
            io.ddd4j.mq.sqs.SqsProperties properties =
                    context.getBean(io.ddd4j.mq.sqs.SqsProperties.class);
            assertThat(properties.getEndpointOverride())
                    .isEqualTo(endpoint().toString());

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic(queueUrl());
            event.setTag("created");
            event.setOrderId("S-001");
            event.publish();

            await(() -> !listener.received().isEmpty(), Duration.ofSeconds(60));
            OrderCreatedEvent received = listener.received().get(0);
            assertThat(received.getOrderId()).isEqualTo("S-001");
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

        @Bean
        QueueUrlRewriter queueUrlRewriter(MQListenerBeanPostProcessor processor) {
            return new QueueUrlRewriter(processor);
        }
    }

    /**
     * 把监听器 topic 的占位符改写为运行时 queueUrl。
     *
     * <p>{@code MQListener} 为可变 POJO，{@code MQListenerBeanPostProcessor#getListeners()}
     * 返回浅拷贝快照（同一批对象引用），本改写器以 {@link Ordered#HIGHEST_PRECEDENCE}
     * 先于 {@code MQListenerRegistrar} 处理 {@link ContextRefreshedEvent}，改写结果
     * 对后续装配可见。
     */
    static final class QueueUrlRewriter {

        private final MQListenerBeanPostProcessor processor;

        QueueUrlRewriter(MQListenerBeanPostProcessor processor) {
            this.processor = processor;
        }

        @EventListener(ContextRefreshedEvent.class)
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public void rewrite() {
            for (MQListener listener : processor.getListeners()) {
                if (QUEUE_URL_PLACEHOLDER.equals(listener.getTopic())) {
                    listener.setTopic(queueUrl());
                }
            }
        }
    }

    public static class TestOrderListener {

        private final List<OrderCreatedEvent> received = new CopyOnWriteArrayList<>();

        @MQEventListener(topic = QUEUE_URL_PLACEHOLDER, tags = "created")
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
