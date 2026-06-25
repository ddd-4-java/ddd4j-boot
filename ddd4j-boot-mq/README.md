# ddd4j-boot-mq 架构设计

> 本文档描述 ddd4j 消息队列体系的目标架构、模块边界与落地约定。  
> **状态：阶段二/三已落地**（legacy `impl/*Client` 仍保留兼容，见 §16）。

---

## 1. 背景与动机

### 1.1 旧版 ddd4j-mq 的问题

历史实现（`ddd4j/base-mq` 及本模块中的 `impl/*Client`）将 **领域契约** 与 **Broker 客户端实现** 耦合在同一模块：

| 问题 | 说明 |
|------|------|
| 自研客户端 | 直接使用 `amqp-client`、`jedis`、`rocketmq-client` 等裸 API，未委托 Spring Boot 官方 Starter |
| 可靠性不足 | 手动 ack/nack 逻辑分散且有缺陷；失败时可能既不 ack 也不 nack，消息悬挂 |
| Redis PubSub 误用 | PubSub 无持久化、无消费确认，不适合任务队列 |
| Redis Stream 不完整 | 缺少 pending 回收（XPENDING/XCLAIM）、DLQ 等生产级能力 |
| 发布方式脆弱 | 通过 `BaseContext` 静态查找 `MQEventPublisher`，难以测试与替换 |
| 队列命名脆弱 | 队列名绑定类名/方法名，重构即破坏拓扑 |

### 1.2 设计目标

1. **ddd4j-boot-mq 只做适配层（Port）**：零 Broker SDK，可独立单测。
2. **实现交给成熟组件**：Spring Boot Starter（`spring-amqp`、`spring-kafka` 等）及各 `ddd4j-boot-cmpt-*` 模块。
3. **Spring Cloud 能力归 ddd4j-cloud**：Spring Cloud Stream + Binder 不在 boot 侧实现。
4. **以 RabbitMQ Channel 确认语义为基准**，统一多 Broker 的 ack 模型。
5. **保留 ddd4j 优秀 DX**：`MQEvent`、`@MQEventListener`、租户上下文注入、JSON 序列化约定。

---

## 2. ddd4j-boot-mq 是什么、不是什么

### 2.1 是什么

- **领域消息契约层**：`MQEvent`、`MQDestination`、发布/消费 Port。
- **消费确认抽象层**：`MessageAcknowledgment` 及 `AckDisposition` 高层语义。
- **监听器编排层**：解析 `@MQEventListener`，委托 SPI `MQBrokerAdapter` 注册消费端点。
- **与 DDD 配套的横切能力挂载点**：租户 `ThreadContext`、序列化、可选消息流水拦截器。

### 2.2 不是什么

- **不是** Rabbit/Kafka/Rocket 客户端实现（禁止在本模块引入 `amqp-client`、`kafka-clients` 等）。
- **不是** Spring Cloud Stream 集成（归属 `ddd4j-cloud-cmpt-stream-*`）。
- **不是** 任务调度器或本地内存队列（`ThreadPoolExecutor`、`LinkedBlockingQueue` 属于业务或基础设施，不是 MQ 适配职责）。
- **不是** 额外的 `ddd4j-boot-starter-mq-*` 聚合包——**各 Broker 能力已整合在对应 `ddd4j-boot-cmpt-*` 中**，应用按需引入单个 cmpt 依赖即可。

---

## 3. 总体分层

```
┌─────────────────────────────────────────────────────────────────┐
│ 业务应用（单体 / 微服务）                                         │
│  - 领域事件类 extends MQEvent                                     │
│  - @MQEventListener 或 Spring Cloud Stream Consumer<Message<T>>（cloud 侧） │
├─────────────────────────────────────────────────────────────────┤
│ ddd4j-cloud（Spring Cloud 生态，可选）                            │
│  ddd4j-cloud-cmpt-stream          Spring Cloud Stream 桥接（StreamBridge 等） │
│  ddd4j-cloud-cmpt-stream-rabbit   + rabbit binder                │
│  ddd4j-cloud-cmpt-stream-kafka    + kafka binder                 │
│  ddd4j-cloud-cmpt-stream-rocket     + rocketmq binder              │
│  ddd4j-cloud-cmpt-stream-pulsar     + pulsar binder              │
│  ddd4j-cloud-cmpt-base-mqflow       消息流水/审计（横切，可选）    │
├─────────────────────────────────────────────────────────────────┤
│ ddd4j-boot（Spring Boot 生态）                                   │
│  ddd4j-boot-mq                    纯契约 + SPI（本模块）           │
│  ddd4j-boot-cmpt-rabbit           spring-boot-starter-amqp       │
│  ddd4j-boot-cmpt-kafka            spring-kafka                   │
│  ddd4j-boot-cmpt-rocket           rocketmq-spring-boot-starter   │
│  ddd4j-boot-cmpt-pulsar           spring-pulsar                  │
│  ddd4j-boot-cmpt-redis-stream     spring-data-redis              │
│  ddd4j-boot-cmpt-activemq         spring-boot-starter-artemis    │
│  ddd4j-boot-cmpt-nats / -ons / -tdmq / -sqs  …（按区域选装）      │
├─────────────────────────────────────────────────────────────────┤
│ 开源实现层（不自研协议客户端）                                     │
│ RabbitTemplate · KafkaListener · RocketMQTemplate · Pulsar…      │
└─────────────────────────────────────────────────────────────────┘
```

### 3.1 依赖规则（硬性）

| 模块 | 允许依赖 | 禁止依赖 |
|------|---------|---------|
| `ddd4j-boot-mq` | `ddd4j-boot-core`、`spring-boot-autoconfigure` | 一切 Broker SDK、`spring-cloud-stream` |
| `ddd4j-boot-cmpt-{broker}` | `ddd4j-boot-mq` + 对应 Boot Starter | `ddd4j-cloud-*` |
| `ddd4j-cloud-cmpt-stream-*` | `ddd4j-boot-mq` + `spring-cloud-stream` + binder | — |
| `ddd4j-cloud` | 可依赖 `ddd4j-boot` | — |
| `ddd4j-boot` | — | `ddd4j-cloud` |

### 3.2 应用如何选择依赖

**Spring Boot 单体 / 非 Cloud 微服务：**

```xml
<dependency>
    <groupId>io.ddd4j.boot</groupId>
    <artifactId>ddd4j-boot-cmpt-rabbit</artifactId>
</dependency>
```

`ddd4j-boot-cmpt-rabbit` 内部已传递 `ddd4j-boot-mq` 与 `spring-boot-starter-amqp`，**无需**再引独立 starter。

**Spring Cloud 微服务（需要 StreamBridge / 函数式绑定）：**

```xml
<dependency>
    <groupId>io.ddd4j.cloud</groupId>
    <artifactId>ddd4j-cloud-cmpt-stream-rabbit</artifactId>
</dependency>
```

cloud cmpt 传递 boot cmpt + stream binder。

---

## 4. 两种消息语义

共用 `@MQEventListener` 或统一 Port，但语义应区分：

| 类型 | 用途 | 可靠性 | 典型能力 |
|------|------|--------|---------|
| **Domain Event** | 跨模块/跨服务通知 | at-least-once，幂等可接受重复 | 普通队列 + 业务幂等 |
| **Job Message** | 一个工作单元一条消息（导出、同步、生图子任务等） | 必须 ack/nack/retry/DLQ | 手动确认、重试退避、死信 |

Job Message 应保证：**一条消息 = 一个可独立 ack 的工作单元**，由 Broker 负责排队与重投，而不是在应用内再叠一层内存队列。

---

## 5. 模块规划

### 5.1 `ddd4j-boot-mq`（本模块）包结构（目标）

```
io.ddd4j.boot.mq
├── contract/
│   ├── MQEvent                      # 领域事件基类（与 boot-core 对齐）
│   ├── MQMessage<T>                 # 信封：payload + headers + metadata
│   └── MQDestination                # namespace / topic / tag
├── annotation/
│   └── MQEventListener              # 可置于 boot-core，语义不变
├── acknowledgment/
│   ├── MessageAcknowledgment        # 完整确认端口（见第 6 节）
│   ├── AcknowledgmentContext
│   ├── AckDisposition               # 业务层推荐枚举
│   └── UnsupportedAckOperationException
├── consume/
│   ├── MQConsumerContext            # tenantId、ack、raw headers
│   ├── MQConsumerHandler
│   └── MQConsumeInterceptor         # 幂等/流水等拦截链
├── publish/
│   └── MQEventPublisher             # 替代 BaseContext 静态发布
├── serialization/
│   └── MQEventSerialization
├── registry/
│   ├── MQListenerDefinition
│   ├── MQBindingNaming
│   └── MQBrokerType
├── config/
│   └── Ddd4jMQProperties            # 主前缀 ddd4j.mq，兼容 base-mq
└── spi/
    ├── MQBrokerAdapter              # 各 cmpt 实现
    └── MQPublisherFactory
```

### 5.2 `ddd4j-boot-cmpt-*` 统一内部骨架

每个 Broker 模块结构一致，便于维护：

```
ddd4j-boot-cmpt-rabbit/
├── autoconfigure/Ddd4jRabbitMQAutoConfiguration
├── publisher/RabbitMQEventPublisher
├── consumer/RabbitMQConsumerEndpointRegistrar
├── acknowledgment/AmqpMessageAcknowledgment
└── properties/Ddd4jRabbitMQProperties
```

**实现原则：** 委托 Spring 容器管理连接、并发、prefetch、重试；不在 cmpt 内自研消费循环。

### 5.3 市面 Top 10 队列与模块映射

| # | 中间件 | Spring Boot 主流集成 | boot cmpt 模块 | cloud stream cmpt |
|---|--------|---------------------|----------------|-------------------|
| 1 | RabbitMQ | `spring-boot-starter-amqp` | `ddd4j-boot-cmpt-rabbit` | `ddd4j-cloud-cmpt-stream-rabbit` |
| 2 | Apache Kafka | `spring-kafka` | `ddd4j-boot-cmpt-kafka` | `ddd4j-cloud-cmpt-stream-kafka` |
| 3 | Apache RocketMQ | `rocketmq-spring-boot-starter` | `ddd4j-boot-cmpt-rocket` | `ddd4j-cloud-cmpt-stream-rocket` |
| 4 | Apache Pulsar | `spring-pulsar` | `ddd4j-boot-cmpt-pulsar` | `ddd4j-cloud-cmpt-stream-pulsar` |
| 5 | Redis Stream | `spring-boot-starter-data-redis` | `ddd4j-boot-cmpt-redis-stream` | 无官方 Spring Cloud Stream Binder，短期走 boot cmpt |
| 6 | ActiveMQ Artemis | `spring-boot-starter-artemis` | `ddd4j-boot-cmpt-activemq` | 视 binder 成熟度 |
| 7 | NATS JetStream | `jnats` + AutoConfig | `ddd4j-boot-cmpt-nats` | 视社区 binder |
| 8 | 阿里云 ONS | `ons-client`（Rocket 兼容） | `ddd4j-boot-cmpt-ons` | 可复用 stream-rocket |
| 9 | 腾讯云 TDMQ | `tdmq-client` | `ddd4j-boot-cmpt-tdmq` | 视 binder |
| 10 | AWS SQS | `spring-cloud-aws-sqs` | `ddd4j-boot-cmpt-sqs` | `ddd4j-cloud-cmpt-stream-aws` |

**明确废弃：** Redis **PubSub**（`ddd4j-boot-mq` 中 legacy `RedisClient`）仅可作广播通知，不得作为任务队列。

### 5.4 `ddd4j-cloud` 侧（Spring Cloud Stream）

```
ddd4j-cloud-cmpt-stream/              # 核心桥接
├── StreamBridgeMQEventPublisher
├── ScsMessageAcknowledgmentResolver  # Message → MessageAcknowledgment
├── FunctionalConsumerRegistrar       # 函数式 Consumer 辅助
├── BindingNamingContributor          # 绑定名 / destination 约定
└── Ddd4jStreamConsumeSupport         # 消费模板 + Ack

ddd4j-cloud-cmpt-stream-rabbit/       # + spring-cloud-stream-binder-rabbit
ddd4j-cloud-cmpt-stream-kafka/
ddd4j-cloud-cmpt-stream-rocket/
ddd4j-cloud-cmpt-stream-pulsar/
```

`ddd4j-cloud-cmpt-base-mqflow` 为**消息流水/审计**（DB 落库），通过 `MQConsumeInterceptor` 挂载，与队列实现正交。

---

## 6. 核心 SPI：`MQBrokerAdapter`

```java
public interface MQBrokerAdapter {
    MQBrokerType brokerType();
    MQEventPublisher createPublisher(Ddd4jMQProperties props);
    void registerConsumer(MQListenerDefinition def, MQConsumerHandler handler);
    MessageAcknowledgment resolveAcknowledgment(MQMessage<?> message);
    boolean supports(MQBrokerType configured);
}
```

启动时根据 `ddd4j.mq.broker`（或兼容的 `base-mq.impl`）从 `List<MQBrokerAdapter>` 中选取唯一实现。

---

## 7. 发布侧设计

### 7.1 从 `BaseContext` 到 Spring Bean

**旧方式（废弃）：**

```java
BaseContext.get("MQEventPublisher").accept(event);
```

**新方式：**

```java
public interface MQEventPublisher {
    <T extends MQEvent> void publish(T event, MQDestination destination);
}
```

`MQEvent.publish(topic, tag, tenantId)` 委托注入的 `MQEventPublisher` Bean，便于测试与替换。

### 7.2 出站路径

| 运行形态 | 发布实现 |
|---------|---------|
| Boot 单体 | cmpt 内 `RabbitTemplate` / `KafkaTemplate` / `RocketMQTemplate` |
| Cloud 微服务 | `ddd4j-cloud-cmpt-stream` 内 `StreamBridge.send(binding, message)` |

### 7.3 目的地值对象

```java
public record MQDestination(String topic, String tag, String namespace) {
    public String bindingOutName() { /* 由 MQBindingNaming 生成 */ }
    public String physicalDestination() { return namespace + "." + topic; }
}
```

---

## 8. 消费侧设计

### 8.1 两种编程模型（并存）

#### 模型 A：`@MQEventListener`（迁移友好）

```java
@MQEventListener(topic = "order.paid", tags = "notify", group = "billing-service")
public AckDisposition onOrderPaid(MQConsumerContext ctx, OrderPaidEvent event) {
    // ctx.ack() 可使用完整 MessageAcknowledgment API
    return AckDisposition.ACK;
}
```

- Boot 侧：cmpt 注册为 `@RabbitListener` / `@KafkaListener` 等。
- Cloud 侧：`FunctionalConsumerRegistrar` 生成 Spring Cloud Stream 函数 Bean 并写入 `spring.cloud.stream.function.definition`。

#### 模型 B：Spring Cloud Stream 函数式（Cloud 推荐）

```java
@Bean
Consumer<Message<OrderPaidEvent>> orderPaid() {
    return message -> Ddd4jStreamConsumeSupport.consume(message, payload -> {
        // 业务逻辑
        return AckDisposition.ACK;
    });
}
```

模型 B 更贴近 Binder 运行时；模型 A 降低从 legacy ddd4j 迁移成本。**两者共用同一套 `MessageAcknowledgment` 与 `AckDisposition`。**

### 8.2 绑定命名约定

```
@MQEventListener(topic="order.paid", tags="notify", group="billing-service")
  ↓
函数 Bean 名（cloud）:     orderPaidNotify
入站 binding（cloud）:     orderPaidNotify-in-0
物理 destination:          {namespace}.order.paid
消费组 group:              billing-service
路由键 / tag（rabbit）:    notify
```

业务注解只声明 **语义**（topic / tag / group）；Broker 细节（prefetch、DLX、分区）在 yaml 与 cmpt 配置中完成。

---

## 9. `MessageAcknowledgment` — 以 RabbitMQ `Channel` 为基准

### 9.1 设计原则

- 以 AMQP 0-9-1 `com.rabbitmq.client.Channel` 的确认 API 为**完整参考模型**。
- 其他 Broker 通过 Adapter **尽力映射**；不支持的操作抛出 `UnsupportedAckOperationException`，禁止静默吞掉。
- 业务层优先使用 `AckDisposition` + `MQConsumeTemplates`；需要 `basicRecover` 等高级语义时直接使用 `MessageAcknowledgment`。

### 9.2 Rabbit 确认 API 与业务场景

| 场景 | Rabbit API | 说明 |
|------|-----------|------|
| 成功消费 | `basicAck(tag, false)` | 单条确认，从队列删除 |
| 批量确认 | `basicAck(tag, true)` | 确认 tag 及之前所有未 ack 消息 |
| 失败重试（倾向其他消费者） | `basicRecover(true)` | 重新投递，尽量给其他 consumer |
| 失败重试（显式） | `basicNack(tag, false, true)` | 单条 nack + requeue |
| 异常重试 | `basicReject(tag, true)` | 单条拒绝 + requeue |
| 幂等跳过 / 终态丢弃 | `basicAck(tag, false)` | 不再处理 |
| 永久失败进 DLQ | `basicNack(tag, false, false)` 或 `basicReject(tag, false)` | 需队列配置 DLX |
| 执行中防并发 | `basicNack(tag, false, true)` | 消费前检查发现「处理中」 |

### 9.3 统一接口（目标定稿）

```java
/**
 * 消息确认端口。以 AMQP Channel 语义为基准；其他 Broker 由 Adapter 映射。
 */
public interface MessageAcknowledgment {

    // ── 元数据 ──
    long deliveryTag();
    String messageId();
    String correlationId();
    boolean isOpen();           // 对应 channel.isOpen()
    boolean isAcknowledged();   // 防止重复确认
    MQBrokerType brokerType();

    // ── 确认成功 ──
    void ack();                 // basicAck(tag, false)
    void ack(boolean multiple); // basicAck(tag, multiple)

    // ── 否定确认 ──
    void nack(boolean requeue);                    // basicNack(tag, false, requeue)
    void nack(boolean multiple, boolean requeue);  // basicNack(tag, multiple, requeue)
    void reject(boolean requeue);                  // basicReject(tag, requeue)
    void recover(boolean requeue);                 // basicRecover(requeue)

    // ── 便捷方法 ──
    default void ackSingle() { ack(false); }
    default void discard() { nack(false); }            // requeue=false → DLQ
    default void requeue() { nack(true); }
    default void requeueViaRecover() { recover(true); }

    // ── 原生逃逸（高级场景）──
    <T> Optional<T> unwrap(Class<T> nativeType);
}
```

### 9.4 高层 `AckDisposition`

```java
public enum AckDisposition {
    ACK,              // 成功 → ack(false)
    DISCARD,          // 幂等跳过/终态 → ack(false)
    REQUEUE,          // 瞬时失败 → nack(requeue=true) 或 recover(true)
    REJECT_TO_DLQ,    // 永久失败 → nack(requeue=false)
    DEFER             // 处理中/锁占用 → nack(requeue=true)
}
```

### 9.5 消费模板

```java
public final class MQConsumeTemplates {

    public static void execute(
            MQMessage<?> message,
            MessageAcknowledgment ack,
            IntSupplier preCheck,              // 0=继续, 1=DISCARD, 2=DEFER
            Supplier<AckDisposition> business) {

        int pre = preCheck.getAsInt();
        if (pre == 1) { ack.ackSingle(); return; }
        if (pre == 2) { ack.requeue(); return; }

        switch (business.get()) {
            case ACK, DISCARD -> ack.ackSingle();
            case REQUEUE -> ack.requeue();
            case REJECT_TO_DLQ -> ack.discard();
            case DEFER -> ack.requeue();
        }
    }
}
```

### 9.6 多 Broker 映射矩阵

| MessageAcknowledgment | Rabbit | Kafka | RocketMQ | Pulsar | Redis Stream |
|----------------------|--------|-------|----------|--------|--------------|
| `ack(false)` | basicAck | offset commit | CONSUME_SUCCESS | acknowledge | XACK |
| `ack(true)` | basicAck multiple | commit 至当前 | 批量 commit | cumulative ack | 批量 XACK |
| `nack(false, true)` | basicNack requeue | 不 commit + 重平衡 | RECONSUME_LATER | negativeAcknowledge | 不 XACK |
| `nack(false, false)` | basicNack no requeue | commit + DLT | 进 DLQ | ack + DLQ | XACK + dead stream |
| `reject(requeue)` | basicReject | 同 nack 语义 | 同 nack | 同 nack | 同 nack |
| `recover(requeue)` | basicRecover | 不支持* | 不支持* | 不支持* | XPENDING reclaim |

\* 不支持时抛 `UnsupportedAckOperationException`，文档中说明替代方案（如 Kafka `SeekToCurrentErrorHandler`）。

### 9.7 Broker 专属扩展（可选）

```java
public interface AmqpMessageAcknowledgment extends MessageAcknowledgment {
    Channel channel();
}

public interface KafkaMessageAcknowledgment extends MessageAcknowledgment {
    Acknowledgment kafkaAck();
    ConsumerRecord<?, ?> record();
}
```

Cloud 侧从 `org.springframework.messaging.Message` 解析：

- `AmqpHeaders.CHANNEL`
- `AmqpHeaders.DELIVERY_TAG`
- `AmqpHeaders.MESSAGE_ID`

由 `ScsMessageAcknowledgmentResolver` 构造 `AmqpMessageAcknowledgment`，避免各项目重复编写手动 ack 工具类。

---

## 10. 配置体系

### 10.1 配置前缀

| 前缀 | 说明 |
|------|------|
| `ddd4j.mq.*` | **主前缀**（新应用使用） |
| `base-mq.*` | **兼容别名**（legacy ddd4j / 存量配置，经 `EnvironmentPostProcessor` 映射） |

### 10.2 Boot 单体示例

```yaml
ddd4j:
  mq:
    enabled: true
    broker: rabbit          # rabbit | kafka | rocket | pulsar | redis-stream | ...
    namespace: app
    default-topic: DEFAULT
    consumer:
      ack-mode: manual      # manual | auto
    serialization: json

spring:
  rabbitmq:
    host: localhost
    port: 5672
```

### 10.3 Cloud + Stream 示例

```yaml
ddd4j:
  mq:
    enabled: true
    broker: rabbit
    namespace: app
    consumer:
      ack-mode: manual

spring:
  cloud:
    stream:
      default-binder: rabbit
      function:
        definition: orderPaidNotify;jobExecute
      bindings:
        orderPaidNotify-in-0:
          destination: app.order.paid
          group: billing-service
          consumer:
            max-attempts: 3
            back-off-initial-interval: 5000
            back-off-multiplier: 2.0
            back-off-max-interval: 30000
      rabbit:
        bindings:
          orderPaidNotify-in-0:
            consumer:
              acknowledge-mode: manual
              prefetch: 16
              max-concurrency: 8
              binding-routing-key: notify
```

**职责划分：**

- `ddd4j.mq`：语义层（namespace、topic、tag、group、ack 策略、序列化）。
- `spring.cloud.stream` + binder：运行时（重试、DLQ、并发、prefetch）。
- `BindingNamingContributor`：可根据 `@MQEventListener` 自动生成默认 bindings，减少手写 yaml。

### 10.4 主要配置项（`Ddd4jMQProperties` 目标）

| 属性 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `enabled` | boolean | false | 是否启用 MQ |
| `broker` | string | none | 当前 Broker 类型 |
| `namespace` | string | "" | 环境/租户级前缀 |
| `default-topic` | string | DEFAULT | `MQEvent.publish()` 默认 topic |
| `consumer.ack-mode` | string | manual | manual / auto |
| `serialization` | string | json | 序列化器 Bean 名或类型 |
| `persist` | boolean | false | 是否启用消息本地持久化（需 `MQEventStorer`） |
| `retries` | int | 0 | 发送失败重试（cmpt 实现） |

Legacy `base-mq.server` / `username` / `password` 等连接信息**逐步下沉**到各 Broker 标准配置（`spring.rabbitmq.*`、`spring.kafka.*` 等），避免 duplicate 配置。

---

## 11. 与旧版 ddd4j 对比

| 维度 | 旧 ddd4j-mq | 新 ddd4j-boot-mq + cmpt |
|------|------------|------------------------|
| 架构角色 | 契约 + 实现混合 | boot-mq 仅契约；cmpt 实现 |
| 发布 | `BaseContext` 静态 | `MQEventPublisher` Bean |
| 消费注册 | `MQClient.init()` 扫全局 | `MQBrokerAdapter` + Spring 容器 |
| 换 Broker | 改 `impl` 枚举 | 换 cmpt 依赖 + `ddd4j.mq.broker` |
| Ack | 各 Client 各自实现 | `MessageAcknowledgment` 统一 |
| Cloud | 无 | `ddd4j-cloud-cmpt-stream-*` |
| 测试 | 难 mock | Port 单测 + Testcontainers 集成测 |
| Redis PubSub | 支持 | **废弃** |

---

## 12. 迁移与兼容

| 项 | 策略 |
|----|------|
| `base-mq.*` | 映射到 `ddd4j.mq.*` |
| `MQEvent.publish()` | 委托 `MQEventPublisher`；短期保留 `BaseContext` 桥接 |
| `@MQEventListener` | 注解语义不变，注册改走 `MQBrokerAdapter` |
| `impl/*Client` | 标记 `@Deprecated`，下个大版本移除 |
| `MQClient` / `MQListener` | 由 `MQListenerDefinition` + Adapter 替代 |

---

## 13. 落地路线图

### 阶段一：契约冻结（boot-mq）✅

- [x] `MessageAcknowledgment`、`AckDisposition`、`MQEventPublisher`、`MQBrokerAdapter`
- [x] `MQConsumeTemplates` 与 Ack 状态机单测
- [x] `Ddd4jMQProperties` + `base-mq` 别名
- [x] `MQListenerBeanPostProcessor` + `MQListenerDefinitionRegistry`（替代全容器扫描）
- [x] `MQListenerClasspathScanner` + `MQListenerEndpointNaming`（跨 cmpt / cloud 复用）
- [ ] legacy `impl/*Client` 移除（下个大版本）

### 阶段二：Boot cmpt（全 Broker 消费端）✅

| 模块 | 消费端 | 官方 Starter | Testcontainers IT |
|------|--------|-------------|-------------------|
| `ddd4j-boot-cmpt-rabbit` | ✅ `SimpleRabbitListenerEndpoint` | `spring-boot-starter-amqp` | ✅ `RabbitMQContainerIT` |
| `ddd4j-boot-cmpt-kafka` | ✅ `ConcurrentMessageListenerContainer` | `spring-kafka` + `KafkaAutoConfiguration` | ✅ `KafkaContainerIT` |
| `ddd4j-boot-cmpt-rocket` | ✅ `DefaultMQPushConsumer` | `rocketmq-spring-boot-starter` | [ ] |
| `ddd4j-boot-cmpt-pulsar` | ✅ `PulsarClient` messageListener | `spring-boot-starter-pulsar` | [ ] |
| `ddd4j-boot-cmpt-redis-stream` | ✅ `StreamMessageListenerContainer` | `spring-boot-starter-data-redis` | [ ] |
| `ddd4j-boot-cmpt-activemq` | ✅ `SimpleJmsListenerEndpoint` | `spring-boot-starter-artemis` | [ ] |
| `ddd4j-boot-cmpt-nats` | ✅ JetStream / Core Dispatcher | 无官方 Starter（`jnats`） | [ ] |
| `ddd4j-boot-cmpt-ons` | ✅ `ONSFactory.createConsumer` | 无官方 Starter（`ons-client`） | [ ] |
| `ddd4j-boot-cmpt-sqs` | ✅ 长轮询 | 待迁 `io.awspring.cloud` Starter | [ ] |
| `ddd4j-boot-cmpt-tdmq` | ✅ 占位进程内总线 | 无官方 Starter | [ ] |
| `ddd4j-boot-cmpt-disruptor` | ✅ `DisruptorMQBus` | 无（`com.lmax:disruptor`） | N/A 本地 |

运行集成测试（需 Docker）：

```bash
cd ddd4j-boot-cmpt
mvn verify -Pmq-integration-tests -pl ddd4j-boot-cmpt-rabbit,ddd4j-boot-cmpt-kafka -am
```

默认 `skipTests=true`；`*IT.java` 需 `-Pmq-integration-tests`。IT 使用 `org.testcontainers:testcontainers` + 模块包（`rabbitmq` / `kafka`），版本 `1.20.6` 与 Spring Boot 3.4.x 对齐（勿用 `ddd4j-boot-dependencies` 的 testcontainers 2.x）。

**IT 注意事项**：

- `spring-biz` 传递的 JUnit 5.8.2 已与 BOM 排除对齐（`ddd4j-boot-dependencies` 引入 `junit-bom` + 排除 `junit-jupiter-api`），避免与 Spring Boot 3.4 的 `junit-platform` 6.x 冲突。
- Kafka IT 使用 Testcontainers 1.20+ 的 `org.testcontainers.kafka.KafkaContainer` + 官方镜像 `apache/kafka:3.8.1`（勿用 `confluentinc/cp-kafka`，需 `ConfluentKafkaContainer`）。
- IT 内手动 `@BeforeAll` 启动容器，不依赖 `testcontainers-junit-jupiter` 扩展。

### 阶段三：Cloud Stream 桥接 ✅

- [x] `ddd4j-cloud-cmpt-stream` + `Ddd4jStreamConsumeSupport`
- [x] `ddd4j-cloud-cmpt-stream-rabbit` / `kafka` / `rocket` / `pulsar`
- [x] `FunctionalConsumerRegistrar` 复用 `MQListenerClasspathScanner`
- [x] `StreamListenerMetadata.from(MQListenerDefinition)`

### 阶段四：深化与治理

- [ ] 各 Broker Ack 映射矩阵配置模板（yaml 片段）
- [ ] SQS 迁移至 `io.awspring.cloud:spring-cloud-aws-starter-sqs`
- [ ] Rocket / Redis Stream Testcontainers IT
- [ ] 可选：`ddd4j-cloud-cmpt-base-mqflow` 拦截器集成
- [ ] legacy `impl/*Client` 清理

---

## 14. 官方 Starter 选型矩阵（Maven Central）

> **原则**：有 Spring Boot 官方 Starter → 必用 Starter；有 Spring Cloud Stream Binder → cloud 侧用 Binder（库模块不引 `starter-stream-*` 避免重复自动配置）；**仅在没有 Starter 时**才在 cmpt 内手写 `Connection` / `Consumer` 初始化。

### Boot 层（`ddd4j-boot-cmpt-*`）

| Broker | Maven 坐标（优先） | mvnrepository | ddd4j 模块 | 自定义部分 |
|--------|-------------------|---------------|-----------|-----------|
| **RabbitMQ** | `org.springframework.boot:spring-boot-starter-amqp` | [spring-rabbit](https://mvnrepository.com/artifact/org.springframework.amqp/spring-rabbit) | `ddd4j-boot-cmpt-rabbit` | 仅 `@MQEventListener` 动态 `SimpleRabbitListenerEndpoint` |
| **Kafka** | `org.springframework.kafka:spring-kafka`（Boot 无 `starter-kafka`） | [spring-kafka](https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka) | `ddd4j-boot-cmpt-kafka` | 编程式 `ConcurrentMessageListenerContainer`；连接配置走 `spring.kafka.*` |
| **RocketMQ** | `org.apache.rocketmq:rocketmq-spring-boot-starter` | [rocketmq-spring-boot-starter](https://mvnrepository.com/artifact/org.apache.rocketmq/rocketmq-spring-boot-starter) | `ddd4j-boot-cmpt-rocket` | 编程式 `DefaultMQPushConsumer`（复用 `RocketMQProperties`） |
| **Pulsar** | `org.springframework.boot:spring-boot-starter-pulsar` | [spring-pulsar](https://mvnrepository.com/artifact/org.springframework.pulsar/spring-pulsar) | `ddd4j-boot-cmpt-pulsar` | `PulsarClient` messageListener |
| **Redis Stream** | `org.springframework.boot:spring-boot-starter-data-redis` | [spring-data-redis](https://mvnrepository.com/artifact/org.springframework.data/spring-data-redis) | `ddd4j-boot-cmpt-redis-stream` | `StreamMessageListenerContainer` |
| **ActiveMQ Artemis** | `org.springframework.boot:spring-boot-starter-artemis` | [spring-jms](https://mvnrepository.com/artifact/org.springframework/spring-jms) | `ddd4j-boot-cmpt-activemq` | `SimpleJmsListenerEndpoint` |
| **NATS** | 无 Spring 官方 Starter | [jnats](https://mvnrepository.com/artifact/io.nats/jnats) | `ddd4j-boot-cmpt-nats` | `Nats.connect` + JetStream `subscribe`（**必须**手写） |
| **阿里云 ONS** | 无 Spring 官方 Starter | [ons-client](https://mvnrepository.com/artifact/com.aliyun.openservices/ons-client) | `ddd4j-boot-cmpt-ons` | `ONSFactory.createConsumer`（**必须**手写） |
| **AWS SQS** | `io.awspring.cloud:spring-cloud-aws-starter-sqs`（目标） | [spring-cloud-aws-starter-sqs](https://mvnrepository.com/search?q=spring-cloud-aws-starter-sqs) | `ddd4j-boot-cmpt-sqs` | 当前 `aws-java-sdk-sqs` + 长轮询，待迁 awspring |
| **腾讯云 TDMQ** | 无统一 Spring Starter | TDMQ / Pulsar 兼容 SDK | `ddd4j-boot-cmpt-tdmq` | `TdmqClient` 占位 + 进程内总线 |
| **Disruptor** | 无 | [disruptor](https://mvnrepository.com/artifact/com.lmax/disruptor) | `ddd4j-boot-cmpt-disruptor` | 本地 `RingBuffer`（参考 disruptor-spring-boot-starter 思路） |

### Cloud 层（`ddd4j-cloud-cmpt-stream-*`）

| Broker | Binder（cmpt 库依赖） | 应用可选用 Starter | mvnrepository |
|--------|----------------------|-------------------|---------------|
| **RabbitMQ** | `spring-cloud-stream-binder-rabbit` | `spring-cloud-starter-stream-rabbit` | [binder-rabbit](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream-binder-rabbit) · [starter](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-stream-rabbit) |
| **Kafka** | `spring-cloud-stream-binder-kafka` | `spring-cloud-starter-stream-kafka` | [binder-kafka](https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream-binder-kafka) |
| **RocketMQ** | `spring-cloud-starter-stream-rocketmq`（Alibaba Cloud） | 同左 | [starter-stream-rocketmq](https://mvnrepository.com/artifact/com.alibaba.cloud/spring-cloud-starter-stream-rocketmq) |
| **Pulsar** | `spring-pulsar-spring-cloud-stream-binder` | `spring-boot-starter-pulsar` | [pulsar binder](https://mvnrepository.com/search?q=spring-pulsar-spring-cloud-stream-binder) |

**cmpt 库 vs 应用 Starter**：

- `ddd4j-cloud-cmpt-stream-rabbit` 只引 **binder** + `ddd4j-boot-cmpt-rabbit`（已含 `starter-amqp`），**不引** `spring-cloud-starter-stream-rabbit`，避免双份自动配置。
- 业务应用若不用 ddd4j cloud cmpt，可直接引 `spring-cloud-starter-stream-rabbit`，自行配置 `spring.cloud.stream.*`。

### 依赖决策树

```
需要 MQ？
 ├─ 单体 / Boot 微服务 → ddd4j-boot-cmpt-{broker}
 │    └─ Starter 由 cmpt 传递（spring-boot-starter-* / rocketmq-spring-boot-starter）
 ├─ Spring Cloud Stream → ddd4j-cloud-cmpt-stream-{broker}
 │    └─ Binder 由 cloud cmpt 传递；连接仍走 spring.rabbitmq.* / spring.kafka.* 等标准配置
 └─ 无官方 Starter（NATS/ONS/TDMQ）→ cmpt 内最小化手写客户端，仅补 @MQEventListener 动态注册
```

---

## 15. 设计决策摘要

1. **boot-mq 零 Broker SDK**：实现全部在 `ddd4j-boot-cmpt-*`。
2. **无独立 `ddd4j-boot-starter-mq-*`**：cmpt 模块即开箱依赖单元。
3. **Spring Cloud Stream 仅存在于 ddd4j-cloud**：boot 不引入 `spring-cloud-stream`。
4. **Ack 以 Rabbit Channel 为完整集**：其他 Broker 映射 + 显式不支持异常。
5. **两层 Ack API**：业务用 `AckDisposition`；高级用 `MessageAcknowledgment`。
6. **同一套 `@MQEventListener`**：boot 走 Listener 注册，cloud 走 Spring Cloud Stream 函数注册。
7. **Redis Stream 走 boot cmpt**；cloud binder 后续再评估。

---

## 16. 参考关系（模块依赖简图）

```
ddd4j-boot-core (MQEvent, @MQEventListener)
        │
        ▼
ddd4j-boot-mq  ◄────────────────────────────┐
        │                                      │
        ├──► ddd4j-boot-cmpt-rabbit            │
        ├──► ddd4j-boot-cmpt-kafka             │
        ├──► ddd4j-boot-cmpt-rocket            │
        ├──► ddd4j-boot-cmpt-pulsar            │
        ├──► ddd4j-boot-cmpt-redis-stream      │
        └──► …                                 │
                                               │
ddd4j-cloud-cmpt-stream ─────────────────────┘
        │
        ├──► ddd4j-cloud-cmpt-stream-rabbit
        ├──► ddd4j-cloud-cmpt-stream-kafka
        └──► …
```

---

## 17. 附录：legacy 代码说明

当前 `ddd4j-boot-mq` 仍包含自 ddd4j 迁入的 legacy 实现：

- `impl/RabbitClient.java`
- `impl/KafkaClient.java`
- `impl/RocketClient.java`
- `impl/RedisClient.java`（PubSub，将废弃）
- `impl/RedisStreamClient.java`
- `core/MQClient.java`

上述类**不代表目标架构**，仅作过渡兼容。新功能开发请遵循本文档，在 `ddd4j-boot-cmpt-*` 与 `ddd4j-cloud-cmpt-stream-*` 中实现。

---

*文档版本：1.1 | 维护：ddd4j-boot 团队*
