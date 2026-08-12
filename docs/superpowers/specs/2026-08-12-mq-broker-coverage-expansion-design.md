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

| 模块 | 集成测试文件 | 容器镜像 |
|------|-------------|---------|
| mq-activemq | `ActiveMQClientIntegrationTest.java` | `apache/activemq-classic:5.18.3` |
| mq-nats | `NatsClientIntegrationTest.java` | `nats:2.10-alpine` |
| mq-ons | `OnsClientIntegrationTest.java` | 复用 RocketMQ 镜像 |
| mq-pulsar | `PulsarClientIntegrationTest.java` | `apachepulsar/pulsar:3.2.0` |
| mq-redis-stream | `RedisStreamClientIntegrationTest.java` | `redis:7.4-alpine` |
| mq-sqs | `SqsClientIntegrationTest.java` | `localstack/localstack:3.4` |
| mq-tdmq | `TdmqClientIntegrationTest.java` | 复用 Pulsar 镜像 |

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
