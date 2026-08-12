# MQ 集成设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-mq/ddd4j-boot-mq-core` + 13 个 broker 子模块

## 1. 目标与范围

`ddd4j-boot-mq-core` 提供统一配置和默认 `MQEventSerialization`。所有 broker 自动配置明确 `after = Ddd4jMQAutoConfiguration` 以及各自 Spring 原生自动配置。复用核心 `MQListenerDefinition`、`MQListenerMethodInvoker`、`MQConsumeTemplates`、acknowledgment 和上下文清理流程。

### 非目标

- Spring 只替换 Bean 发现、启动和停止生命周期，不复制上游 MQ 消费模板

## 2. 总体架构

```
ddd4j-boot-mq/
  ddd4j-boot-mq-core/
    Ddd4jMQAutoConfiguration     ← 统一配置、MQEventSerialization
  ddd4j-boot-mq-kafka/           ← KafkaMQBootAutoConfiguration
  ddd4j-boot-mq-rabbitmq/        ← RabbitMQBootAutoConfiguration
  ddd4j-boot-mq-rocketmq/        ← RocketMQBootAutoConfiguration
  ddd4j-boot-mq-activemq/        ← ActiveMQBootAutoConfiguration
  ddd4j-boot-mq-disruptor/       ← DisruptorMQBootAutoConfiguration
  ddd4j-boot-mq-mqtt/            ← MqttMQBootAutoConfiguration
  ddd4j-boot-mq-mqtt-mica/       ← MicaMqttMQBootAutoConfiguration
  ddd4j-boot-mq-nats/            ← NatsMQBootAutoConfiguration
  ddd4j-boot-mq-ons/             ← OnsMQBootAutoConfiguration
  ddd4j-boot-mq-pulsar/          ← PulsarMQBootAutoConfiguration
  ddd4j-boot-mq-redis-stream/    ← RedisStreamMQBootAutoConfiguration
  ddd4j-boot-mq-sqs/             ← SqsMQBootAutoConfiguration
  ddd4j-boot-mq-tdmq/            ← TdmqMQBootAutoConfiguration
```

薄适配器模式：每个 broker 只注册上游 `XxxMQClient` Bean 并导入 `Ddd4jMQRegistrarConfiguration`。

## 3. 模块结构

| 模块 | 自动配置类 | 契约测试 | Testcontainers |
|------|-----------|---------|----------------|
| mq-core | `Ddd4jMQAutoConfiguration` | Yes | - |
| mq-kafka | `KafkaMQBootAutoConfiguration` | Yes | Yes |
| mq-rabbitmq | `RabbitMQBootAutoConfiguration` | Yes | Yes |
| mq-rocketmq | `RocketMQBootAutoConfiguration` | Yes | Yes |
| mq-activemq | `ActiveMQBootAutoConfiguration` | Yes | 待补 |
| mq-disruptor | `DisruptorMQBootAutoConfiguration` | Yes | -（本地内存） |
| mq-mqtt | `MqttMQBootAutoConfiguration` | Yes | 待补 |
| mq-mqtt-mica | `MicaMqttMQBootAutoConfiguration` | Yes | 待补 |
| mq-nats | `NatsMQBootAutoConfiguration` | Yes | 待补 |
| mq-ons | `OnsMQBootAutoConfiguration` | Yes | 待补 |
| mq-pulsar | `PulsarMQBootAutoConfiguration` | Yes | 待补 |
| mq-redis-stream | `RedisStreamMQBootAutoConfiguration` | Yes | 待补 |
| mq-sqs | `SqsMQBootAutoConfiguration` | Yes | 待补 |
| mq-tdmq | `TdmqMQBootAutoConfiguration` | Yes | 待补 |

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jMQAutoConfiguration` | 统一配置、Binder 绑定 MQProperties、MQEventSerialization |
| `KafkaMQBootAutoConfiguration` | Kafka client Bean 注册、导入 MQRegistrarConfiguration |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `ddd4j.mq` | `enabled` | - | MQ 总开关 |
| `ddd4j.mq` | `broker` | - | broker 选择（kafka/rabbit/rocket/...） |
| `ddd4j.mq.<broker>` | `*` | - | broker 专属配置 |

## 6. 测试策略

- 契约测试：13 个 broker 均有 `ApplicationContextRunner` 测试
- 集成测试：Kafka/RabbitMQ/RocketMQ 已有 Testcontainers 全链路测试（发布->消费->ack）
- 集成测试通过 `@Tag("integration")` + 显式 profile 开启

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 4 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| 上游 MQProperties 循环依赖 | Ddd4jMQAutoConfiguration 不导入上游配置类，自行 Binder 绑定 |
| broker classpath 顺序不确定 | 所有 broker 明确 after = Ddd4jMQAutoConfiguration |

## 9. 交付物清单

- [x] 14 个 MQ 模块自动配置
- [x] 14 个契约测试
- [x] Kafka/RabbitMQ/RocketMQ Testcontainers 集成测试

## 10. 未决事项

- Redis Stream/Pulsar/NATS/ONS/SQS/TDMQ/ActiveMQ Testcontainers 集成测试待补
