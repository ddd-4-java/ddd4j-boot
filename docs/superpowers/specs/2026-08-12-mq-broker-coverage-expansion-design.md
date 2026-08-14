# MQ Broker Testcontainers 覆盖补齐设计

- 日期：2026-08-12
- 状态：待实施
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-mq/ddd4j-boot-mq-{activemq,nats,ons,pulsar,redis-stream,sqs,tdmq}`

## 1. 目标与范围

当前只有 Kafka、RabbitMQ、RocketMQ 三个 broker 有 Testcontainers 集成测试。需补齐其余 7 个 broker 的发布->消费->ack 全链路测试。

### 非目标

- 不覆盖 MQTT（需要 broker 外部配置，不适合 Testcontainers 快速启动）
- 不覆盖 MQTT-Mica（同上）
- 不覆盖 Disruptor（本地内存，无需容器）

## 2. 总体架构

每个 broker 新增一个 `*ClientIntegrationTest.java`，使用 Testcontainers 启动真实 broker 容器，验证：

1. 发布一条 `MQEvent`
2. 真实 broker 接收
3. 真实消费
4. ack 确认

## 3. 模块结构

| 模块 | 集成测试文件 | 容器镜像 | 状态 |
|------|-------------|---------|------|
| mq-activemq | `ActiveMQClientIntegrationTest.java` | `apache/activemq-classic:5.18.3` | 待实施 |
| mq-nats | `NatsClientIntegrationTest.java` | `nats:2.10-alpine` | 待实施 |
| mq-ons | `OnsClientIntegrationTest.java` | 复用 RocketMQ 镜像 | 待实施 |
| mq-pulsar | `PulsarMQClientIntegrationTest.java` | `apachepulsar/pulsar:3.2.0` | ✅ 已覆盖（2026-08-14，commit f68974c0） |
| mq-redis-stream | `RedisStreamMQClientIntegrationTest.java` | `redis:7.4-alpine` | ✅ 已覆盖（2026-08-14，commit f68974c0） |
| mq-sqs | `SqsClientIntegrationTest.java` | `localstack/localstack:3.4` | 待实施 |
| mq-tdmq | `TdmqClientIntegrationTest.java` | 复用 Pulsar 镜像 | 待实施 |

### 已知上游问题（2026-08-14 发现）

1. **Pulsar 生产者/消费者 topic 不对称**：上游 `PulsarMQClient` 生产者把带 tag 的消息发往
   `tenant/namespace/topic:tag`（tag 拼入 topic 名），消费者却订阅 `tenant/namespace/topic`
   （不含 tag）。两者是不同的 Pulsar topic——**任何带 tag 的事件都会发到消费者永远读不到的
   topic**。当前集成测试绕开（事件不带 tag、listener 不设 tags），测试 Javadoc 有记录。
   修复需改上游 `ddd4j-mq-pulsar` 的 `physicalTopic` 对称性，属上游仓库任务。
2. **Redis Stream 收尾竞态（外观问题）**：context 关闭时容器先停、消费守护线程读流中断，
   日志出现 `JedisConnectionException: Unexpected end of stream` ERROR。测试已通过，
   消息已消费；上游消费者线程缺少优雅停机。

## 4. 核心抽象

复用 Testcontainers 通用模式：

```java
@Testcontainers
@Tag("integration")
class XxxClientIntegrationTest {
    @Container
    static GenericContainer<?> container = new GenericContainer<>("image:tag")
        .withExposedPorts(port);

    @Test
    void shouldProduceAndConsume() { ... }
}
```

## 5. 配置结构

通过 `@DynamicPropertySource` 注入 broker 连接信息。

## 6. 测试策略

- 每个 broker 1 个集成测试类
- 测试通过 `@Tag("integration")` 标记
- `mvn verify` 默认跳过，`mvn verify -P external-infra` 显式开启

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 6.3 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| Docker 镜像拉取慢 | 用 `withReuse(true)` 启用 Docker reuse |
| 部分 broker 容器启动慢 | `waitingFor` 策略 + 超时配置 |

## 9. 交付物清单

- [ ] 7 个 broker 集成测试
- [ ] 集成测试 profile 配置

## 10. 未决事项

- MQTT/MQTT-Mica 是否需要集成测试（需要 broker 外部配置）
