# 全链路 Samples 烟雾测试设计

- 日期：2026-08-12
- 状态：待实施
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-samples/*`

## 1. 目标与范围

运行 samples 中 CQRS、rich model、layered、WebMVC/WebFlux 代表应用作为端到端烟雾测试，验证自动装配在真实应用场景下正常工作。

### 代表性 Sample 选择

| Sample | 覆盖能力 |
|--------|---------|
| `ddd4j-boot-sample-cqrs-person-command` + `query` | CQRS、CommandBus、Projection |
| `ddd4j-boot-sample-rich-model` | 充血模型、MyBatis-Plus、Repository |
| `ddd4j-boot-sample-layered` | 经典分层架构 |
| `ddd4j-boot-sample-order` | Spring Boot + Postgres + Kafka + Outbox |
| `ddd4j-boot-sample-starter-druid-kafka` | WebMVC + Kafka |
| `ddd4j-boot-sample-starter-r2dbc-webflux` | WebFlux + R2DBC |

## 2. 总体架构

```
mvn verify -pl ddd4j-boot-samples/ddd4j-boot-sample-order -am
mvn verify -pl ddd4j-boot-samples/ddd4j-boot-sample-rich-model -am
mvn verify -pl ddd4j-boot-samples/ddd4j-boot-sample-cqrs-person-command -am
```

每个 sample 启动 Spring Boot 应用，验证：
1. 自动配置全部装配成功
2. 关键 Bean 可注入
3. HTTP 端点返回 200
4. MQ 发布/消费正常（如有）

## 3. 模块结构

每个 sample 已有 `application.yaml` 和启动类，需补充：

```
ddd4j-boot-sample-xxx/
  src/test/java/.../
    SmokeTest.java       ← @SpringBootTest + TestRestTemplate
```

## 4. 核心抽象

```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
class SmokeTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // 验证应用启动成功
    }

    @Test
    void healthEndpointReturns200() {
        // 验证健康检查端点
    }
}
```

## 5. 配置结构

使用 `application-test.yaml` 覆盖外部依赖连接信息。

## 6. 测试策略

- 每个代表性 sample 1 个 SmokeTest
- 使用 `@SpringBootTest(webEnvironment = RANDOM_PORT)` 启动完整应用上下文
- 通过 `TestRestTemplate` 验证 HTTP 端点

## 7. 文档与示例

- `docs/guides/run-sample.md`

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| sample 依赖外部基础设施 | 使用 Testcontainers 或 H2 内存数据库 |
| sample 启动慢 | 仅对代表性 sample 执行烟雾测试 |

## 9. 交付物清单

- [ ] 6 个代表性 sample 的 SmokeTest
- [ ] CI 集成配置

## 10. 未决事项

- 是否需要 Testcontainers PostgreSQL/MySQL for samples
