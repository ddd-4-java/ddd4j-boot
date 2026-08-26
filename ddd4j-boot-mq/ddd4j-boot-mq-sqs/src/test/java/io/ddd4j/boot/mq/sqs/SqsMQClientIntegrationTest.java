package io.ddd4j.boot.mq.sqs;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * 完整 queueUrl。LocalStack 绑定宿主机固定端口 4566（{@code PortBinding}），
 * 使 queueUrl 可作为编译期常量供 {@code @MQEventListener} 引用。
 * 无 Docker 环境时自动跳过，不伪报通过。
 */
@Testcontainers(disabledWithoutDocker = true)
class SqsMQClientIntegrationTest {

    private static final int EDGE_PORT = 4566;

    private static final String QUEUE_NAME = "ddd4j-events";

    /** LocalStack edge 端口固定后，queueUrl 可预知 */
    private static final String QUEUE_URL = "http://localhost:" + EDGE_PORT + "/000000000000/" + QUEUE_NAME;

    private static final String ACCESS_KEY = "test";

    private static final String SECRET_KEY = "test";

    @Container
    static final LocalStackContainer LOCALSTACK = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.8.0"))
            .withServices(LocalStackContainer.Service.SQS)
            .withEnv("EDGE_PORT", String.valueOf(EDGE_PORT));

    static {
        // 固定宿主机端口 4566，使 queueUrl 可作为编译期常量供 @MQEventListener 引用
        LOCALSTACK.setPortBindings(java.util.List.of(EDGE_PORT + ":" + EDGE_PORT));
    }

    @BeforeAll
    static void createQueue() {
        try (SqsClient sqs = sqsClient()) {
            sqs.createQueue(CreateQueueRequest.builder().queueName(QUEUE_NAME).build());
        }
    }

    private static SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:" + EDGE_PORT))
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
                        "ddd4j.mq.sqs.endpoint-override=http://localhost:" + EDGE_PORT,
                        "ddd4j.mq.sqs.region=us-east-1",
                        "ddd4j.mq.sqs.access-key=" + ACCESS_KEY,
                        "ddd4j.mq.sqs.secret-key=" + SECRET_KEY,
                        "ddd4j.mq.sqs.wait-time-seconds=2");

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：LocalStack 连接参数必须已绑定到 SqsProperties
            io.ddd4j.mq.sqs.SqsProperties properties =
                    context.getBean(io.ddd4j.mq.sqs.SqsProperties.class);
            assertThat(properties.getEndpointOverride()).isEqualTo("http://localhost:" + EDGE_PORT);

            TestOrderListener listener = context.getBean(TestOrderListener.class);

            OrderCreatedEvent event = new OrderCreatedEvent();
            event.setTopic(QUEUE_URL);
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
    }

    public static class TestOrderListener {

        private final List<OrderCreatedEvent> received = new CopyOnWriteArrayList<>();

        @MQEventListener(topic = QUEUE_URL, tags = "created")
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
