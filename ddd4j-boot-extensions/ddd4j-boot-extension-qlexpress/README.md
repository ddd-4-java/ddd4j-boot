# ddd4j-boot-extension-qlexpress

QLExpress 的 Spring Boot 适配和规则管理模块。

模块边界：

- `ddd4j-extension-qlexpress`：纯 Java 表达式执行、校验、函数和安全选项
- `ddd4j-boot-extension-qlexpress`：配置绑定、Bean 装配、规则 CRUD、仓储、缓存和事件

## 自动装配

引入依赖后自动提供：

- `QLExpressProperties`
- `QLExpressEngine`
- `RuleRepository`：缺省为 `InMemoryRuleRepository`
- `RuleCache`：存在 `CacheManager` 时使用 Spring Cache，否则使用进程内缓存
- `RuleService`

所有 Bean 均支持业务侧通过自定义 Bean 覆盖。

## 配置

```yaml
ddd4j:
  qlexpress:
    enabled: true
    built-in-functions: true
    timeout-millis: 3000
    cache: true
    precise: false
    avoid-null-pointer: false
    max-array-length: 10000
    trace-expression: false
    allow-private-access: false
    rules:
      enabled: true
      cache-name: ddd4j:qlexpress:rules
```

## 直接执行表达式

```java
@Service
public class PricingService {

    private final QLExpressEngine engine;

    public PricingService(QLExpressEngine engine) {
        this.engine = engine;
    }

    public BigDecimal calculate(BigDecimal amount, BigDecimal rate) {
        return engine.execute(
                "amount * rate",
                Map.of("amount", amount, "rate", rate),
                BigDecimal.class);
    }
}
```

## 规则管理

```java
RuleDefinition rule = RuleDefinition.builder()
        .code("discount.vip")
        .name("VIP 折扣")
        .type(RuleType.CALCULATION)
        .expression("amount * 0.8")
        .enabled(true)
        .priority(100)
        .build();

RuleDefinition saved = ruleService.create(rule);
QLExpressExecutionResult<Object> result = ruleService.execute(
        "discount.vip", Map.of("amount", new BigDecimal("100")));
```

规则变更后会发布 `RuleChangedEvent`，操作类型包括：

- `CREATED`
- `UPDATED`
- `DELETED`
- `ENABLED`
- `DISABLED`

## 替换持久化

默认仓储只适合开发和无状态临时规则。生产系统应提供自己的 `RuleRepository` Bean：

```java
@Bean
RuleRepository ruleRepository(MyRuleMapper mapper) {
    return new MyBatisRuleRepository(mapper);
}
```

自动配置使用 `@ConditionalOnMissingBean`，业务实现会自然覆盖默认内存实现。

## 缓存

模块只依赖 Spring Cache 抽象，不绑定 Redis、JetCache、Redisson 或 Caffeine。
业务系统只需提供 `CacheManager`，模块会使用 `ddd4j.qlexpress.rules.cache-name`
对应的缓存实例；没有 `CacheManager` 时回退到进程内缓存。

## 自定义函数

将 `NamedQLFunction` 注册成 Spring Bean，自动配置会在构建引擎时发现并注册：

```java
@Bean
NamedQLFunction tenantLevel() {
    return new TenantLevelFunction();
}
```

## 迁移说明

原 `io.ddd4j.extension.express.*` 中的业务式规则模型已删除：

| 旧能力 | 新位置 |
| --- | --- |
| `RuleDefinition` | `io.ddd4j.boot.qlexpress.rule.RuleDefinition` |
| `RuleDefinitionRepository` | `RuleRepository` |
| `RuleCacheService` | `RuleCache` |
| `RuleManagementService` | `RuleService` |
| 三个领域事件 | `RuleChangedEvent` |
| `RuleEngineApplicationService` | 直接使用 `QLExpressEngine` 或 `RuleService.execute` |
| Spring/JetCache/Redis 命名实现 | Spring Cache 适配或业务自定义 Bean |

Web Controller、鉴权、审计、数据库表结构属于具体业务系统，不在通用 Boot 扩展中预设。

## 后续优化方向

1. **规则版本与发布态**：增加草稿、已发布、已下线状态，使用乐观锁避免并发覆盖。
2. **仓储适配子模块**：按需提供独立的 JPA、MyBatis、配置中心适配器，不把技术依赖塞回主模块。
3. **缓存一致性**：分布式部署时通过规则变更事件广播失效，避免节点本地缓存长期不一致。
4. **事务事件**：数据库规则与 `RuleChangedEvent` 使用事务同步或 Outbox，避免保存成功但事件丢失。
5. **表达式 Guardrails**：基于外部变量/函数分析增加白名单、复杂度、长度和危险调用检查。
6. **编译预热**：规则发布时预校验并预编译，按规则版本管理编译缓存，降低首请求延迟。
7. **可观测性**：单独提供 Micrometer/Tracing 适配，记录规则编码、版本、耗时、命中率和失败类型。
8. **多租户隔离**：缓存键、规则编码和仓储查询显式携带 tenant/namespace，避免跨租户污染。
9. **评测体系**：建立规则数据集、回归样本、边界值和历史流量回放，发布前比较新旧版本结果。
10. **Web 能力拆分**：如需 CRUD API，创建独立 Web adapter，统一鉴权、审计、幂等和错误响应。
