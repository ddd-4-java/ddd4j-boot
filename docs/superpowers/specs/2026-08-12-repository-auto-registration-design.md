# Repository 自动发现与注册设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-core`

## 1. 目标与范围

自动发现 Spring 容器中的领域 Repository Bean，注册到 `RepositoryRegistry`，支持 Aggregate 类型及可选 Query 类型。Context 关闭时对称移除映射，解决 `BaseContext` 与兼容静态 Map 双状态泄漏。

### 非目标

- 不以脆弱泛型猜测作为唯一发现方式
- 不处理同一 Aggregate/Query 多实现（fail-fast）

## 2. 总体架构

```
Ddd4jRepositoryAutoConfiguration (@AutoConfiguration)
  ├── BeanPostProcessor: 扫描 Repository Bean
  │     ├── 解析泛型参数 -> Aggregate 类型 / Query 类型
  │     ├── RepositoryRegistry.register(aggregateClass, repository)
  │     └── 重复注册 -> fail-fast
  └── DisposableBean: 关闭时对称 unregister
```

## 3. 模块结构

```
ddd4j-boot-core/
  src/main/java/io/ddd4j/boot/core/
    Ddd4jRepositoryAutoConfiguration.java
  src/test/java/io/ddd4j/boot/core/
    Ddd4jRepositoryAutoConfigurationTest.java
```

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jRepositoryAutoConfiguration` | BeanPostProcessor 自动注册 Repository，DisposableBean 关闭清理 |
| `RepositoryRegistry` | 上游 ddd4j-core 的静态注册表 |

## 5. 配置结构

无独立配置，受 `ddd4j.enabled` 总开关控制。

## 6. 测试策略

- ApplicationContextRunner：默认装配、缺类回退、关闭无残留
- 验证 `RepositoryRegistry` 在上下文关闭后无残留映射

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 3.2 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| 泛型擦除导致类型推断失败 | 提供显式注册器扩展点 |
| 测试间静态 Map 泄漏 | DisposableBean 对称清理 |

## 9. 交付物清单

- [x] `Ddd4jRepositoryAutoConfiguration.java`
- [x] `Ddd4jRepositoryAutoConfigurationTest.java`

## 10. 未决事项

- 无
