# Core Runtime SPI 生命周期自动配置设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-core`

## 1. 目标与范围

为 `ddd4j-boot-core` 建立 Spring Core 自动配置切面，负责：

1. 条件导入 `ddd4j-runtime-spring` 的核心 Bean（复用 `SpringCoreConfig`、`SpringDomainEventPublisher` 等）
2. 收集 Spring Bean 中的 `DomainEventPublisher`、`SubjectProvider`、`SubjectDataProvider`、`I18nProvider`、`CommandBus`
3. 使用 `SpiRegistrationScope` 进行成组注册、启动失败回滚、关闭恢复和 `ThreadContext` 清理
4. 支持重复 refresh、Spring 测试上下文重建和父子 ApplicationContext
5. 用户 Bean 始终通过 `@ConditionalOnMissingBean` 优先于默认实现

### 非目标

- 不在 Boot 中复制 `ddd4j-core` 的领域模型或 CQRS 算法
- 不直接散落调用 `BaseContext.inject()`

## 2. 总体架构

```
Spring ApplicationContext
  │
  ├── Ddd4jCoreAutoConfiguration (@AutoConfiguration)
  │     ├── @Import(SpringCoreConfig.class)
  │     ├── @Bean DomainEventPublisher (via SpringDomainEventPublisher)
  │     ├── @Bean SubjectProvider (收集容器中 Bean)
  │     ├── @Bean SubjectDataProvider (收集容器中 Bean)
  │     ├── @Bean I18nProvider (收集容器中 Bean)
  │     ├── @Bean CommandBus (DefaultCommandBus, @ConditionalOnMissingBean)
  │     └── @Bean SpiRegistrationScope (成组注册、关闭清理)
  │
  └── META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## 3. 模块结构

```
ddd4j-boot-core/
  src/main/java/io/ddd4j/boot/core/
    Ddd4jCoreAutoConfiguration.java      # 核心自动配置
    Ddd4jRepositoryAutoConfiguration.java # 仓储自动注册（见独立规格）
  src/main/resources/META-INF/spring/
    org.springframework.boot.autoconfigure.AutoConfiguration.imports
  src/test/java/io/ddd4j/boot/core/
    Ddd4jCoreAutoConfigurationTest.java   # 契约测试
    Ddd4jRepositoryAutoConfigurationTest.java
```

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jCoreAutoConfiguration` | 条件装配核心 SPI Bean，注册到 `SpiRegistrationScope` |
| `SpiRegistrationScope` | 成组注册 SPI，支持启动失败回滚、关闭恢复、ThreadContext 清理 |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `ddd4j` | `enabled` | `true` | 总开关 |

## 6. 测试策略

使用 `ApplicationContextRunner` 覆盖五个维度：

1. 默认装配：上下文启动成功、关键 Bean 存在
2. 开关回退：`enabled=false` 时不装配
3. 缺类回退：`FilteredClassLoader` 模拟 classpath 缺失
4. 用户 Bean 覆盖：`@ConditionalOnMissingBean` back off
5. 关闭无残留：上下文关闭后静态注册表/SPI 被对称清理

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 3.3 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| SPI 注册泄漏到测试间 | `SpiRegistrationScope` 关闭时对称清理 |
| 父子 ApplicationContext 重复注册 | 使用 `@ConditionalOnMissingBean` + 注册 scope 去重 |

## 9. 交付物清单

- [x] `Ddd4jCoreAutoConfiguration.java`
- [x] `Ddd4jCoreAutoConfigurationTest.java`
- [x] `AutoConfiguration.imports` 注册

## 10. 未决事项

- 无
