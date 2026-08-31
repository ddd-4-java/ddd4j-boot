# JDK 8 维护线架构差异审计

## 已验证事实

`2.3.x` 在 Corretto 8 下可以完整 reactor 编译，但它不是当前 `3.4.x` 的低语法实现：

| 维度 | `2.3.x` | `3.4.x` |
|---|---|---|
| 根模块 | `core`、`cmpt`、samples 等 6 个模块 | Core、Web、Data、Auth、Cache、MQ 等 11 个模块 |
| 自动配置主体 | `ddd4j-boot-cmpt` 下 13 个历史组件 starter | 分领域 starter 与 40 余个 imports 注册 |
| 当前核心桥接 | 不存在 `Ddd4jCoreAutoConfiguration`、`Ddd4jRepositoryAutoConfiguration` | Core SPI、Repository、CQRS 生命周期桥接已实现 |
| 上游核心 | 旧 `org.fuin.ddd4j` 依赖 | 当前 `ddd4j` 2.0.x 契约与 runtime bridge |

## 结论

JDK 8 组的“逻辑一致”不能通过复制 `3.4.x` 的测试类或仅改自动配置注册完成。必须先定义 Java 8 可承载的 `ddd4j 1.0.x` 适配边界，再为该边界建立等价的 Core/SPI/Repository/CQRS 测试夹具；无法在 Java 8/Boot 2 上承载的能力必须记录 `NOT_APPLICABLE` 与迁移映射。

本审计改变 Task 6 的实施顺序：先完成 JDK 8 核心桥接设计和最小适配，再扩展 Web、Data、Auth、Cache 与 MQ 组件契约。它不降低已确认的逻辑一致性目标。
