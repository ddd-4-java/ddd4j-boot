# CQRS CommandBus 自动装配设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-core`

## 1. 目标与范围

收集容器中所有 `CommandExecutor<?>`，构建唯一 `DefaultCommandBus`。保留重复 handler 启动失败、缺失 handler 执行期失败的核心语义。条件装配 `ProjectionPositionRepository`、`EventChunkReader`、`ProjectionService`、`ProjectionRunner`。

### 非目标

- Spring 只承担调度与事务边界，不复制 `ProjectionRunner` 和投影算法
- 不擅自引入异步或 `REQUIRES_NEW`

## 2. 总体架构

```
Ddd4jCoreAutoConfiguration
  ├── @Bean DefaultCommandBus (收集所有 CommandExecutor)
  │     ├── @ConditionalOnMissingBean
  │     └── 重复 handler -> fail-fast
  ├── @Bean ProjectionPositionRepository (条件装配)
  ├── @Bean EventChunkReader (条件装配)
  ├── @Bean ProjectionService (条件装配)
  └── @Bean ProjectionRunner (条件装配)
```

## 3. 模块结构

在 `Ddd4jCoreAutoConfiguration` 中实现，不独立建模块。

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `DefaultCommandBus` | 收集 CommandExecutor，唯一路由 |
| `ProjectionRunner` | 投影执行（上游提供，Boot 只调度） |

## 5. 配置结构

无独立配置，受 `ddd4j.enabled` 总开关控制。

## 6. 测试策略

- ApplicationContextRunner：验证 CommandBus 装配、用户 Bean 覆盖
- 功能契约：CommandBus 唯一路由、Projection position 推进

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 3.3 节

## 8. 实施风险与缓解

无重大风险。

## 9. 交付物清单

- [x] CommandBus 自动装配（在 Ddd4jCoreAutoConfiguration 中）
- [x] 契约测试

## 10. 未决事项

- 无
