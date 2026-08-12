# ddd4j-boot-data Legacy 重复源码清理设计

- 日期：2026-08-12
- 状态：待实施
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-data/src/main/java/io/ddd4j/data/*`

## 1. 目标与范围

`ddd4j-boot-data/src/main/java/io/ddd4j/data/` 目录下存在与 `ddd4j-boot-data-*` 子模块重复的自动配置类。需确认无有效消费者后删除，或改为 deprecated 转发层并给出移除窗口。

### 重复文件清单

| Legacy 路径 | 对应子模块 | 状态 |
|------------|-----------|------|
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/config/Ddd4jApiLogAspectAutoConfiguration.java` | `ddd4j-boot-data-logs` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/config/Ddd4jCryptoAutoConfiguration.java` | `ddd4j-boot-data-crypto` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/config/Ddd4jDataScopeAutoConfiguration.java` | `ddd4j-boot-data-datascope` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/config/Ddd4jExternalAutoConfiguration.java` | `ddd4j-boot-data-external` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/config/Ddd4jGlobalSequenceAutoConfiguration.java` | `ddd4j-boot-data-external` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/web/MybatisExceptionHandler.java` | `ddd4j-boot-data-mybatis` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/advice/DecryptRequestBodyAdvice.java` | `ddd4j-boot-data-crypto` | 重复 |
| `ddd4j-boot-data/src/main/java/io/ddd4j/data/advice/EncryptResponseBodyAdvice.java` | `ddd4j-boot-data-crypto` | 重复 |

## 2. 总体架构

两种方案：

### 方案 A：直接删除（推荐）

确认 `ddd4j-boot-data` 顶层模块的 `AutoConfiguration.imports` 未注册这些类，且无外部项目依赖 `ddd4j-boot-data`（而非子模块），则直接删除。

### 方案 B：Deprecated 转发层

若发现公开兼容需求，改为 `@Deprecated` 注解 + 日志警告 + 指向新子模块的 JavaDoc，给出 2 个版本的移除窗口。

## 3. 模块结构

```
ddd4j-boot-data/
  src/main/java/io/ddd4j/data/
    config/          ← 删除（由子模块替代）
    web/             ← 删除（由 data-mybatis 替代）
    advice/          ← 删除（由 data-crypto 替代）
```

## 4. 核心抽象

无新增类。

## 5. 配置结构

无变更。

## 6. 测试策略

- 删除前：确认 `ddd4j-boot-data/pom.xml` 的 `AutoConfiguration.imports` 未注册这些类
- 删除后：全量 `mvn verify` 确认无编译错误

## 7. 文档与示例

- 无

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| 外部项目依赖 `ddd4j-boot-data` 顶层 | 先搜索 Maven Central / 私有仓库是否有消费者 |

## 9. 交付物清单

- [ ] 确认无外部消费者
- [ ] 删除 legacy 重复源码
- [ ] 验证编译通过

## 10. 未决事项

- 需确认是否有外部项目依赖 `ddd4j-boot-data` 顶层模块
