package io.ddd4j.boot.mq.rocketmq;

import io.ddd4j.boot.mq.core.config.Ddd4jMQAutoConfiguration;
import io.ddd4j.mq.annotation.MQEventListener;
import io.ddd4j.mq.event.MQEvent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RocketMQ broker 端到端集成测试（Testcontainers）。
 *
 * <p>覆盖：真实 RocketMQ 上的 发布 → 路由 → 消费 → 反序列化 全链路。
 * 无 Docker 环境时自动跳过（{@code disabledWithoutDocker = true}），不伪报通过。
 *
 * <p>官方 {@code apache/rocketmq} 镜像没有 Testcontainers 专用模块，且 RocketMQ 5.x
 * 要求 namesrv 与 broker 独立进程，故本测试用 {@link GenericContainer} 单容器内
 * 先后拉起两个进程（namesrv + broker，同容器内以 localhost 互联）。
 *
 * <p>三个关键配置（官方镜像不提供开箱即用的单容器组合）：
 * <ul>
 *   <li><b>brokerIP1/brokerPort1</b>：broker 向 namesrv 公告的对外地址。客户端（宿主机）
 *       经 namesrv 拿到该地址直连 broker，故必须等于宿主机可达地址 + 固定映射端口
 *       （{@code 10911:10911}，VIP 通道另需 {@code 10909:10909}，可用
 *       {@code -Dddd4j.test.rocketmq.brokerPort=} 覆盖固定端口以规避 CI 冲突）</li>
 *   <li><b>namesrvAddr</b>：broker 注册目标，同容器内固定 {@code 127.0.0.1:9876}；
 *       客户端侧 namesrv 走动态映射端口（{@link #beforeAll} 从容器读取）</li>
 *   <li><b>topic 预创建</b>：上游 client 按 partition key 走 {@code MessageQueueSelector}
 *       发送路径，该路径没有 TBW102 自动建 topic 兜底（只有普通 send 才有），
 *       topic 不存在时首次发送即报 {@code "No route info for this topic"}。
 *       与生产一致，测试在 {@link #beforeAll} 用 {@code mqadmin} 预创建 topic</li>
 * </ul>
 *
 * <p>broker 默认堆 2g，测试用 {@code JAVA_OPT_EXT} 压到 512m（追加在默认参数之后生效）。
 */
@Testcontainers(disabledWithoutDocker = true)
class RocketMQClientIntegrationTest {

    /**
     * 跨 JVM 串行 RocketMQ 端口选择与容器生命周期，避免并行构建选中同一端口对。
     */
    private static final FileChannel PORT_LOCK_CHANNEL = openPortLockChannel();
    private static final FileLock PORT_LOCK = acquirePortLock();

    /**
     * broker 对外公告端口，每次测试动态选择可用的 broker/VIP 端口对。
     */
    private static final int BROKER_PORT = Integer.getInteger(
            "ddd4j.test.rocketmq.brokerPort", availableBrokerPort());

    /**
     * 覆盖镜像自带 broker.conf 的完整配置（含公告地址/namesrv/自动建 topic）。
     */
    private static final String BROKER_CONF_TEMPLATE =
            "brokerClusterName = DefaultCluster\n"
                    + "brokerName = broker-a\n"
                    + "brokerId = 0\n"
                    + "deleteWhen = 04\n"
                    + "fileReservedTime = 48\n"
                    + "brokerRole = ASYNC_MASTER\n"
                    + "flushDiskType = ASYNC_FLUSH\n"
                    + "brokerIP1 = 127.0.0.1\n"
                    + "listenPort = %d\n"
                    + "namesrvAddr = 127.0.0.1:9876\n"
                    + "autoCreateTopicEnable = true\n";

    private static String nameServerAddress;

    @Container
    static final GenericContainer<?> ROCKETMQ = rocketmqContainer();

    private static GenericContainer<?> rocketmqContainer() {
        try {
            // 临时文件默认 0600，Testcontainers 以 root 拷入容器后 broker（rocketmq 用户）无法读取，
            // 必须显式放宽到 0644
            Path brokerConf = Files.createTempFile("ddd4j-rocketmq-broker", ".conf");
            Files.write(brokerConf,
                    String.format(BROKER_CONF_TEMPLATE, BROKER_PORT).getBytes(StandardCharsets.UTF_8));
            Files.setPosixFilePermissions(brokerConf,
                    java.nio.file.attribute.PosixFilePermissions.fromString("rw-r--r--"));
            GenericContainer<?> container = new LockedRocketMQContainer(
                    DockerImageName.parse("apache/rocketmq:5.3.2"))
                    .withEnv("JAVA_OPT_EXT", "-Xms512m -Xmx512m -Xmn256m")
                    .withCommand("sh", "-c",
                            "sh mqnamesrv & sleep 10 && sh mqbroker -n 127.0.0.1:9876"
                                    + " -c /home/rocketmq/rocketmq-5.3.2/conf/broker.conf")
                    .withCopyFileToContainer(MountableFile.forHostPath(brokerConf),
                            "/home/rocketmq/rocketmq-5.3.2/conf/broker.conf")
                    .withExposedPorts(9876)
                    .waitingFor(Wait.forLogMessage(".*boot success.*", 2));
            container.setPortBindings(Arrays.asList(
                    BROKER_PORT + ":" + BROKER_PORT,
                    (BROKER_PORT - 2) + ":" + (BROKER_PORT - 2)));
            return container;
        } catch (IOException e) {
            releasePortLockResources();
            throw new IllegalStateException("Prepare RocketMQ broker.conf failed", e);
        } catch (RuntimeException e) {
            releasePortLockResources();
            throw e;
        }
    }

    private static int availableBrokerPort() {
        for (int candidate = 20011; candidate < 30000; candidate += 10) {
            try (ServerSocket broker = new ServerSocket(candidate);
                 ServerSocket vip = new ServerSocket(candidate - 2)) {
                return candidate;
            } catch (IOException ignored) {
                // Try the next port pair.
            }
        }
        releasePortLockResources();
        throw new IllegalStateException("No available RocketMQ broker/VIP port pair");
    }

    private static FileChannel openPortLockChannel() {
        try {
            return FileChannel.open(Paths.get(System.getProperty("java.io.tmpdir"), "ddd4j-rocketmq-test.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new IllegalStateException("Open RocketMQ test port lock failed", e);
        }
    }

    private static FileLock acquirePortLock() {
        long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        while (System.nanoTime() < deadline) {
            try {
                FileLock lock = PORT_LOCK_CHANNEL.tryLock();
                if (lock != null) {
                    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            releasePortLockResources();
                        }
                    }));
                    return lock;
                }
            } catch (OverlappingFileLockException ignored) {
                // Another RocketMQ test in this JVM owns the lock.
            } catch (IOException e) {
                releasePortLockResources();
                throw new IllegalStateException("Acquire RocketMQ test port lock failed", e);
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                releasePortLockResources();
                throw new IllegalStateException("Interrupted while acquiring RocketMQ test port lock", e);
            }
        }
        releasePortLockResources();
        throw new IllegalStateException("Acquire RocketMQ test port lock timed out after PT2M");
    }

    @AfterAll
    static void releasePortLock() throws IOException {
        try {
            ROCKETMQ.stop();
        } finally {
            releasePortLockResources();
        }
    }

    private static void releasePortLockResources() {
        try {
            if (PORT_LOCK != null && PORT_LOCK.isValid()) {
                PORT_LOCK.release();
            }
            if (PORT_LOCK_CHANNEL.isOpen()) {
                PORT_LOCK_CHANNEL.close();
            }
        } catch (IOException ignored) {
            // Best-effort cleanup also runs from the JVM shutdown hook.
        }
    }

    private static final class LockedRocketMQContainer extends GenericContainer<LockedRocketMQContainer> {

        private LockedRocketMQContainer(DockerImageName imageName) {
            super(imageName);
        }

        @Override
        public void start() {
            try {
                super.start();
            } catch (RuntimeException e) {
                releasePortLockResources();
                throw e;
            }
        }
    }

    @BeforeAll
    static void beforeAll() throws Exception {
        nameServerAddress = "127.0.0.1:" + ROCKETMQ.getMappedPort(9876);
        // 与生产一致：topic 由运维预创建（select 发送路径没有自动建 topic 兜底）。
        // broker "boot success" 日志先于 namesrv 注册完成打印，mqadmin 未找到集群时
        // 打印 [error] 但退出码仍为 0，故必须按输出轮询直到建成功
        String output = "";
        for (int i = 0; i < 20; i++) {
            org.testcontainers.containers.Container.ExecResult result =
                    ROCKETMQ.execInContainer("sh", "mqadmin", "updateTopic",
                            "-n", "127.0.0.1:9876", "-c", "DefaultCluster", "-t", "order");
            output = result.getStdout();
            if (output.contains("success")) {
                break;
            }
            Thread.sleep(1000);
        }
        assertThat(output).contains("success");
    }

    @Test
    void shouldPublishAndConsumeEventThroughRealRocketMQ() {
        ApplicationContextRunner runner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration.class,
                        Ddd4jMQAutoConfiguration.class, RocketMQBootAutoConfiguration.class))
                .withUserConfiguration(TestListenerConfiguration.class)
                .withPropertyValues(
                        "ddd4j.mq.enabled=true",
                        "ddd4j.mq.broker=rocket",
                        "ddd4j.mq.rocketmq.name-server=" + nameServerAddress);

        runner.run(context -> {
            assertThat(context).hasNotFailed();
            // 属性绑定契约：Testcontainers namesrv 地址必须已绑定到 RocketMQProperties
            io.ddd4j.mq.rocketmq.RocketMQProperties properties =
                    context.getBean(io.ddd4j.mq.rocketmq.RocketMQProperties.class);
            assertThat(properties.getNameServer()).isEqualTo(nameServerAddress);

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
