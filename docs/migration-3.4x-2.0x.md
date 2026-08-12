# ddd4j-boot 3.4.x ↔ ddd4j 2.0.x 迁移指南

> **规格与计划**：本文档是面向用户的迁移指南。完整的实施计划与设计规格见 [docs/superpowers/](superpowers/README.md)，主计划见 [2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md](superpowers/plans/2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md)。

本文档说明 `ddd4j-boot feature/3.4.x` 如何消费 `ddd4j feature/2.0.x`，
以及从旧版本（Spring Boot 2.x / ddd4j 1.x 生态）迁移到当前基线的关键变化。

## 1. 版本基线

| 组件 | 版本 |
| --- | --- |
| ddd4j-boot | `3.4.x.20260630-SNAPSHOT` |
| ddd4j | `2.0.x.20260630-SNAPSHOT` |
| Spring Boot | `3.4.13` |
| Spring Framework | `6.2.19` |
| Java | 17（强制基线，21 兼容） |

两条版本线共用一个 revision 风格（`3.4.x` / `2.0.x` + 日期快照），通过根 POM 的
`${revision}` 与 `${ddd4j.version}` 统一管理。

## 2. ddd4j-boot 的定位

`ddd4j-boot` 只承担四类职责，**不复制上游已实现的能力**：

1. **依赖选择器**：`ddd4j-boot-dependencies` 统一管理第三方与 ddd4j 构件版本，
   `ddd4j-boot-bom` 供外部项目 import。
2. **Spring Boot 条件装配层**：所有模块使用 Boot 3 `@AutoConfiguration` +
   `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册。
3. **SPI 生命周期协调器**：`ddd4j-boot-core` 把 Spring 容器中的 SPI Bean
   （`DomainEventPublisher` / `SubjectProvider` / `SubjectDataProvider` / `I18nProvider` /
   `CommandBus`）注册到 ddd4j `BaseContext`，并在上下文关闭时对称移除。
4. **配置属性层**：`ddd4j.*` 前缀的配置绑定（见第 6 节）。

## 3. 核心 API 变化（从旧版迁移时重点关注）

### 3.1 实体基类

| 旧（1.x / 2.0 早期） | 新（2.0.x） |
| --- | --- |
| `io.ddd4j.core.entity.BaseEntity` | `io.ddd4j.core.ddd.model.Entity<ID>`（接口） |
| `extends BaseEntity<DemoEntity>` | `implements Entity<Long>` + `@Override public Long id()` |
| `io.ddd4j.core.entity.BaseEntity` 泛型自身引用 | 不再需要（`Entity<ID>` 只声明 `ID id()`） |

```java
// 旧写法（已删除）
public class Order extends BaseEntity<Order> { ... }

// 新写法
public class Order implements Entity<Long> {
    private Long id;
    @Override public Long id() { return id; }
    // 其余业务字段与方法
}
```

> `AggregateRoot<ID>` 是唯一推荐的充血聚合根基类，提供 `save()/update()/delete()`、
> 静态 `get()/one()/list()/page()` 等能力，内部通过 `RepositoryRegistry` 查找仓储。

### 3.2 仓储（Repository）

- 统一接口：`io.ddd4j.core.ddd.repository.Repository<M extends AggregateRoot<?>, ID extends Serializable>`
- **只有 `findById` 与 `save` 两个抽象方法**，其余（`updateById`/`deleteById`/`findList`/`page`/`count` 等）
  均为 default 方法。
- 注册与查找：
  - 静态注册：`RepositoryRegistry.register(Order.class, repository)` / `unregister(Order.class)`
  - **Spring Boot 自动注册**：引入 `ddd4j-boot-core` 后，容器中的 `Repository` Bean 会被
    `Ddd4jRepositoryRegistrar`（BeanPostProcessor）自动注册；上下文关闭时对称注销。
- 旧版的 `RichRepository` 已被合并进 `Repository`；`Query<P>` 已绑定 PO 类型，不需要再区分两个接口。

### 3.3 CQRS

- `CommandBus` 由 `ddd4j-boot-core` 自动装配：收集容器中所有 `CommandExecutor<?>` 构建
  `DefaultCommandBus`（`@ConditionalOnMissingBean`，可被用户 Bean 覆盖）。
- 投影（Projection）相关 SPI：`ProjectionPositionRepository` / `EventChunkReader` /
  `ProjectionService` / `ProjectionRunner` 由上游提供，Boot 只负责调度与事务边界。
- 事件发布：`new OrderCreatedEvent(...).publish()` 通过 `Contexts` 自动路由到已注册的
  `DomainEventPublisher`（Spring 容器中的 Bean 由 `SpringContextBridge` 注册）。

### 3.4 Web 包结构

| 旧 | 新（2.0.x） |
| --- | --- |
| `io.ddd4j.spring.web.BaseController` | `io.ddd4j.web.webmvc.controller.BaseController`（构造器注入 `NestedMessageSource` + `Mapper`） |
| `io.ddd4j.web.exception.BaseExceptionHandler` | `io.ddd4j.web.webmvc.exception.BaseExceptionHandler` |
| `io.ddd4j.web.webmvc.webmvc.*`（笔误包） | `io.ddd4j.web.webmvc.*` |

### 3.5 其他已删除/重命名类型

- `io.ddd4j.core.mybatis.mapper.BaseMapper` → 使用 MyBatis-Plus 官方 `com.baomidou.mybatisplus.core.mapper.BaseMapper`
- `io.ddd4j.core.service.IBaseService` / `BaseServiceImpl` → MyBatis-Plus `IService` / `ServiceImpl`
- MQ 旧 SPI（`Message` / `Destination` / `ListenerDefinition` / 自定义 ack 注册器）→ 统一
  `MQClient` / `MQListener` / `MQEvent` / `MQEventSerialization`（见第 4 节）

## 4. 消息队列（MQ）—— 薄适配器模式

2.0.x 的 MQ 统一模型：

- **`MQClient`**：broker 客户端（发布/消费入口）
- **`MQListener`**：`@MQEventListener` 注解方法扫描后的监听器模型
- **`MQEventSerialization`**：消息序列化（默认 `JsonMQEventSerialization`）
- **`MQEventStorer`**：可选的事件持久化端口

每个 broker 在 boot 侧只保留**一个薄适配器**自动配置，注册上游 `XxxMQClient` Bean
并导入 `Ddd4jMQRegistrarConfiguration` 驱动监听器装配：

```java
@AutoConfiguration
@ConditionalOnClass(KafkaMQClient.class)
@ConditionalOnProperty(prefix = "ddd4j.mq", name = "broker", havingValue = "kafka")
@Import(Ddd4jMQRegistrarConfiguration.class)
public class KafkaMQBootAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    public KafkaMQClient kafkaMQClient(KafkaMQProperties properties) {
        return new KafkaMQClient(properties, (Callback) null);
    }
}
```

启用方式（以 Kafka 为例）：

```yaml
ddd4j:
  mq:
    enabled: true        # 总开关（mq-core 自动配置）
    broker: kafka        # 选择 broker 适配器
    kafka:
      # broker 专属配置，绑定到 KafkaMQProperties
```

> 注意：broker 模块均依赖 `ddd4j-boot-mq-core`，引入任一 broker starter 即获得属性绑定与序列化器。

## 5. 配置属性索引

| 前缀 | 模块 | 说明 |
| --- | --- | --- |
| `ddd4j.mq.enabled` / `ddd4j.mq.broker` | boot-mq-core / broker | MQ 总开关与 broker 选择 |
| `ddd4j.mq.<broker>.*` | 各 broker | broker 专属配置（kafka/rabbit/rocket/...） |
| `ddd4j.web.webflux.enabled` | boot-web-webflux | WebFlux 装配开关（默认 true） |
| `ddd4j.web.public-paths` / `ddd4j.web.trust-forwarded-headers` | boot-web-webmvc | 公开路径 / 信任转发头 |
| `ddd4j.akka.enabled` | extension-akka | Akka 开关（默认 true） |
| `ddd4j.cola.enabled` | extension-cola | COLA 集成开关（默认 true） |
| `ddd4j.dubbo.enabled` | extension-dubbo | Dubbo 开关（默认 true） |
| `ddd4j.excel.enabled` | extension-excel | Excel 开关（默认 true） |
| `ddd4j.qrcode.enabled` / `ddd4j.qrcode.web.enabled` | extension-qrcode | 二维码开关 / Web 端点开关（web 需显式 true） |
| `ddd4j.datascope.enabled` | data-datascope | 数据权限开关（默认 true） |
| `ddd4j.logs.enabled` | data-logs | API 日志开关（默认 true） |
| `crypto.enabled`（历史前缀，无 ddd4j 命名空间） | data-crypto | 加解密开关（默认 true） |
| `license.*`（历史前缀） | auth-license | License 配置（subject/alias/storePass/路径） |
| `ddd4j.sequence` | data-external | 全局序列号（雪花）配置 |

## 6. 已知注意事项

### 6.1 上游 BOM 缺陷与 Boot 侧 workaround

以下问题源自上游 `ddd4j` BOM/模块，boot 侧已做适配，迁移时**不要回退这些修复**：

| 问题 | Boot 侧处理 |
| --- | --- |
| `MQProperties` / `CryptoProperties` / `ExternalProperties` / `SequenceProperties` / `LicenseProperties` 是零 Spring 依赖纯 POJO（无 `@ConfigurationProperties` 注解） | 不用 `@EnableConfigurationProperties`，改用 `Binder` 手动绑定 |
| 上游 `Ddd4jMQPropertiesConfiguration.mqEventStorer` 存在循环依赖 | `Ddd4jMQAutoConfiguration` 不导入该配置类，自行 Binder 绑定 |
| 上游 POM 无效导致传递依赖丢失（`jackson-extension`、`truelicense-core/xml`） | Boot BOM 补充版本管理 + 模块显式声明 |
| `resilience4j` 上游管理 2.4.0（Central 不存在） | Boot BOM 统一 2.2.0（全家桶直接条目） |
| `opentelemetry-api` 被第三方 BOM 压到 1.25.0（pulsar 需要 1.40+） | Boot BOM 统一 1.57.0 |
| `jedis` 7.1.0 缺少 `RedisClient`（上游需要 7.4.1+） | Boot BOM 对齐 7.5.2 |
| `rocketmq-remoting` 与 client 版本错位 | Boot BOM 管理 remoting/common 与 client 同版本 |
| 若干 `3.3.x-SNAPSHOT` 构件在远端不存在 | 降级到远端可用版本（如 redistpl-plus 2.3.x、validation-mimetypes 2.3.x） |

### 6.2 Lombok

项目父 POM（`ddd4j-boot-dependencies`）已配置 Lombok annotation processor。
**模块代码可以继续使用 Lombok 注解**（`@Data` / `@Slf4j` 等）；不使用 Lombok 的模块
请保持显式 JavaBean 风格。不要在同一个类上同时写 Lombok 注解和手写方法（会产生重复方法）。

### 6.3 外部基础设施测试

依赖外部服务（MQ broker、数据库等）的测试统一标记 `@Disabled`，需要显式启用 profile 运行：

```bash
mvn verify -P external-infra
```

`mvn verify` 默认跳过它们（报告为 skipped，不伪报通过）。

### 6.4 幂等与缓存

- Web 幂等防护（`CacheIdempotencyGuard`）默认使用 `ddd4j-web-idempotency` 缓存名，
  `Ddd4jWebMvcAutoConfiguration` 会按需注册本地 Caffeine CAS 缓存；业务可注册同名缓存覆盖。
- 应用侧如需自定义缓存（Redis 等），通过 `CacheKit.register(biz, cache)` 显式注册。

## 7. 契约测试约定

每个 `@AutoConfiguration` 均有 `ApplicationContextRunner` 契约测试，覆盖五个维度：

1. 默认装配（上下文启动成功、关键 Bean 存在）
2. 开关回退（`enabled=false` 或 broker 不匹配时不装配）
3. 缺类回退（`FilteredClassLoader` 模拟 classpath 缺失）
4. 用户 Bean 覆盖（`@ConditionalOnMissingBean` back off）
5. 关闭无残留（上下文关闭后静态注册表/SPI 被对称清理）

新增自动配置时请同步补齐契约测试（参考 `ddd4j-boot-core` 或 `ddd4j-boot-mq-core` 的测试）。

## 8. 快速开始

```xml
<!-- 1. 引入 BOM -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.ddd4j.boot</groupId>
            <artifactId>ddd4j-boot-bom</artifactId>
            <version>3.4.x.20260630-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- 2. 按需引入模块（无需版本号） -->
<dependencies>
    <dependency>
        <groupId>io.ddd4j.boot</groupId>
        <artifactId>ddd4j-boot-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.ddd4j.boot</groupId>
        <artifactId>ddd4j-boot-web-webmvc</artifactId>
    </dependency>
    <dependency>
        <groupId>io.ddd4j.boot</groupId>
        <artifactId>ddd4j-boot-mq-kafka</artifactId>
    </dependency>
</dependencies>
```

完整示例见 `ddd4j-boot-samples/ddd4j-boot-sample-order`（Spring Boot + Postgres + Kafka + Outbox）
与 `ddd4j-boot-sample-rich-model`（充血模型 + MyBatis-Plus）。
