# ddd4j-boot Superpowers 规格与计划

本目录是 ddd4j-boot 项目的**规格事实源**，遵循 Spec-Driven Development (SDD) 方法论。

- [跨 JDK 逻辑一致性最终报告](reports/ddd4j-boot-cross-jdk-consistency-final.md)

## 目录结构

```
docs/superpowers/
  README.md                          ← 本文件
  plans/                             ← 实施计划（按日期归档）
    YYYY-MM-DD-<topic>.md
  specs/                             ← 设计规格（按日期归档）
    YYYY-MM-DD-<topic>-design.md
```

## 命名约定

| 类型 | 文件名格式 | 示例 |
|------|-----------|------|
| 实施计划 | `YYYY-MM-DD-<topic>.md` | `2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md` |
| 设计规格 | `YYYY-MM-DD-<topic>-design.md` | `2026-08-12-core-runtime-spi-lifecycle-design.md` |

## 状态标记

| 标记 | 含义 |
|------|------|
| `已完成` | 代码已实现、测试已通过、已合入主分支 |
| `实施中` | 代码正在开发，部分任务已完成 |
| `待实施` | 设计已确认，尚未开始编码 |
| `已废弃` | 被新方案替代，保留归档 |

## 计划文件结构（liteflow 风格）

每个计划文件遵循以下结构：

1. 标题（`# <Title>`）
2. 代理工作指令头（`> **For agentic workers:** ...`）
3. `**Goal:**` / `**Architecture:**` / `**Tech Stack:**`
4. `## Global Constraints`
5. 按阶段拆分的 `- [ ]` / `- [x]` 任务清单

## 规格文件结构

每个规格文件遵循以下结构：

1. 元数据（日期、作者、状态、涉及模块）
2. `## 1. 目标与范围`
3. `## 2. 总体架构`
4. `## 3. 模块结构`
5. `## 4. 核心抽象`
6. `## 5. 配置结构`
7. `## 6. 测试策略`
8. `## 7. 文档与示例`
9. `## 8. 实施风险与缓解`
10. `## 9. 交付物清单`
11. `## 10. 未决事项`

## 当前计划与规格索引

### 计划

| 文件 | 一句话摘要 | 状态 |
|------|-----------|------|
| [2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md](plans/2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md) | ddd4j-boot 3.4.x 适配 ddd4j 2.0.x 核心契约 | 实施中（Phase 1-6 已完成） |
| [2026-08-31-ddd4j-boot-cross-jdk-logical-consistency.md](plans/2026-08-31-ddd4j-boot-cross-jdk-logical-consistency.md) | JDK 8/17/21 维护线逻辑一致性收敛、验证与发布证据 | 待实施 |
| [2026-08-31-jdk8-ddd-core-bridge.md](plans/2026-08-31-jdk8-ddd-core-bridge.md) | JDK 8 / Boot 2 的 ddd4j 1.0.x Core、Repository 与 CQRS 桥接 | 待实施 |
| [2026-08-12-ddd4j-quarkus-align-with-boot.md](plans/2026-08-12-ddd4j-quarkus-align-with-boot.md) | ddd4j-quarkus 对齐 ddd4j-boot 能力矩阵 | 待实施 |
| [2026-08-12-ddd4j-javalin-align-with-boot.md](plans/2026-08-12-ddd4j-javalin-align-with-boot.md) | ddd4j-javalin 对齐 ddd4j-boot 能力矩阵 | 待实施 |

### 规格

| 文件 | 一句话摘要 | 状态 |
|------|-----------|------|
| [2026-08-12-core-runtime-spi-lifecycle-design.md](specs/2026-08-12-core-runtime-spi-lifecycle-design.md) | Core Runtime SPI 生命周期自动配置 | 已完成 |
| [2026-08-12-repository-auto-registration-design.md](specs/2026-08-12-repository-auto-registration-design.md) | Repository 自动发现与注册 | 已完成 |
| [2026-08-12-cqrs-commandbus-auto-assembly-design.md](specs/2026-08-12-cqrs-commandbus-auto-assembly-design.md) | CQRS CommandBus 自动装配 | 已完成 |
| [2026-08-12-webmvc-webflux-auto-assembly-design.md](specs/2026-08-12-webmvc-webflux-auto-assembly-design.md) | WebMVC / WebFlux 自动装配 | 已完成 |
| [2026-08-12-data-integration-design.md](specs/2026-08-12-data-integration-design.md) | Data 集成（JPA/MyBatis/Crypto/DataScope/Logs） | 已完成 |
| [2026-08-12-auth-integration-design.md](specs/2026-08-12-auth-integration-design.md) | Auth 集成（Sa-Token/Security/Shiro/License） | 已完成 |
| [2026-08-12-mq-integration-design.md](specs/2026-08-12-mq-integration-design.md) | MQ 集成（核心 + 13 broker + Testcontainers） | 已完成 |
| [2026-08-12-cache-extensions-design.md](specs/2026-08-12-cache-extensions-design.md) | Cache 与扩展模块自动装配 | 已完成 |
| [2026-08-12-configuration-metadata-design.md](specs/2026-08-12-configuration-metadata-design.md) | 配置属性元数据（spring-boot-configuration-processor） | 已完成 |
| [2026-08-12-contract-test-strategy-design.md](specs/2026-08-12-contract-test-strategy-design.md) | ApplicationContextRunner 契约测试策略 | 已完成 |
| [2026-08-31-cross-jdk-logical-consistency-design.md](specs/2026-08-31-cross-jdk-logical-consistency-design.md) | JDK 8/17/21 维护线逻辑能力契约与收敛规则 | 已确认，待执行 |
| [2026-08-31-jdk8-ddd-core-bridge-design.md](specs/2026-08-31-jdk8-ddd-core-bridge-design.md) | JDK 8 / Boot 2 的 ddd4j 1.0.x Core、Repository 与 CQRS 桥接 | 已确认，待执行 |
| [2026-08-12-mq-broker-coverage-expansion-design.md](specs/2026-08-12-mq-broker-coverage-expansion-design.md) | MQ broker Testcontainers 覆盖补齐 | 待实施 |
| [2026-08-12-data-legacy-cleanup-design.md](specs/2026-08-12-data-legacy-cleanup-design.md) | ddd4j-boot-data legacy 重复源码清理 | 待实施 |
| [2026-08-12-full-pipeline-smoke-test-design.md](specs/2026-08-12-full-pipeline-smoke-test-design.md) | 全链路 samples 烟雾测试 | 待实施 |

## 使用指南

### 对于开发者

1. **了解项目全貌**：先读主计划 `2026-08-12-ddd4j-boot-3.4x-adapt-ddd4j-2.0x.md`
2. **了解某个模块的设计**：在 `specs/` 中查找对应模块的设计文档
3. **新增功能**：先写规格（`specs/`），再写计划（`plans/`），最后实施

### 对于 AI 代理

1. 实施任务前，先读取对应计划文件了解上下文
2. 按计划中的 `- [ ]` 任务逐项实施
3. 完成后将 `[ ]` 改为 `[x]` 并注明提交 SHA
4. 不得跳过 Global Constraints 中的约束

## 与其他文档的关系

| 目录 | 用途 | 谁维护 |
|------|------|--------|
| `docs/superpowers/` | 规格与计划（SDD 事实源） | AI 代理 + 开发者 |
| `docs/migration-3.4x-2.0x.md` | 面向用户的迁移指南 | 开发者 |
| `docs/guides/` | 面向用户的使用指南 | 开发者 |
| `docs/concepts/` | 概念性思维导图与架构概览 | 开发者 |
| `docs/references/` | DDD 架构参考文档 | 开发者 |
