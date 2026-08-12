# Data 集成设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-data/ddd4j-boot-data-mybatis`、`ddd4j-boot-data/ddd4j-boot-data-crypto`、`ddd4j-boot-data/ddd4j-boot-data-datascope`、`ddd4j-boot-data/ddd4j-boot-data-external`、`ddd4j-boot-data/ddd4j-boot-data-logs`

## 1. 目标与范围

对齐 JPA、MyBatis、MyBatis-Plus、Crypto、DataScope、External、Logs 的上游 artifact 和 API。修复 MyBatis 自动配置时序，确保 DataSource 和框架原生自动配置完成后加载。

### 非目标

- 不把"实现了 Repository"误判为支持全部 CRUD/分页/批量方法
- `ddd4j-boot-data/src/main/java/io/ddd4j/data/*` 的 legacy 重复源码另行处理（见 data-legacy-cleanup-design.md）

## 2. 总体架构

```
ddd4j-boot-data/
  ddd4j-boot-data-mybatis/      → Ddd4jMybatisAutoConfiguration
  ddd4j-boot-data-crypto/       → Ddd4jCryptoAutoConfiguration
  ddd4j-boot-data-datascope/    → Ddd4jDataScopeAutoConfiguration
  ddd4j-boot-data-external/     → Ddd4jExternalAutoConfiguration + Ddd4jGlobalSequenceAutoConfiguration
  ddd4j-boot-data-logs/         → Ddd4jApiLogAspectAutoConfiguration
```

每个模块独立 `@AutoConfiguration`，独立 `AutoConfiguration.imports` 注册。

## 3. 模块结构

| 模块 | 自动配置类 | 测试 |
|------|-----------|------|
| data-mybatis | `Ddd4jMybatisAutoConfiguration` | `Ddd4jMybatisAutoConfigurationTest` |
| data-crypto | `Ddd4jCryptoAutoConfiguration` | `Ddd4jCryptoAutoConfigurationTest` |
| data-datascope | `Ddd4jDataScopeAutoConfiguration` | `Ddd4jDataScopeAutoConfigurationTest` |
| data-external | `Ddd4jExternalAutoConfiguration` | `Ddd4jExternalAutoConfigurationTest` |
| data-logs | `Ddd4jApiLogAspectAutoConfiguration` | `Ddd4jApiLogAspectAutoConfigurationTest` |

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jMybatisAutoConfiguration` | MyBatis-Plus 集成，异常映射 |
| `Ddd4jCryptoAutoConfiguration` | 加解密请求/响应 advice |
| `Ddd4jDataScopeAutoConfiguration` | 数据权限拦截 |
| `Ddd4jExternalAutoConfiguration` | IP 归属地、天气等外部服务 |
| `Ddd4jApiLogAspectAutoConfiguration` | API 操作日志切面 |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `ddd4j.datascope` | `enabled` | `true` | 数据权限开关 |
| `ddd4j.logs` | `enabled` | `true` | API 日志开关 |
| `crypto` | `enabled` | `true` | 加解密开关（历史前缀） |
| `ddd4j.sequence` | - | - | 全局序列号（雪花）配置 |

## 6. 测试策略

- 每个模块均有 `ApplicationContextRunner` 契约测试
- Repository 能力矩阵测试：JPA/MyBatis-Plus CRUD、分页、批量、条件更新

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 4 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| MyBatis 自动配置时序错误 | 确保 DataSource 和框架原生自动配置完成后加载 |
| legacy 重复源码导致冲突 | 独立规格处理（data-legacy-cleanup-design.md） |

## 9. 交付物清单

- [x] 5 个 Data 模块自动配置
- [x] 5 个契约测试
- [x] `AutoConfiguration.imports` 注册

## 10. 未决事项

- `ddd4j-boot-data/src/main/java/io/ddd4j/data/*` legacy 重复源码待清理
