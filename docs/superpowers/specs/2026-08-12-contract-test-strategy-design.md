# ApplicationContextRunner 契约测试策略设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：所有 `@AutoConfiguration` 模块

## 1. 目标与范围

为每个 `@AutoConfiguration` 建立 `ApplicationContextRunner` 契约测试，覆盖五个维度，确保自动配置行为可预测、可回归。

## 2. 总体架构

```
每个 AutoConfiguration 对应一个 Test 类：
  XxxAutoConfigurationTest.java
    ├── 默认装配（上下文启动成功、关键 Bean 存在）
    ├── 开关回退（enabled=false 或 broker 不匹配时不装配）
    ├── 缺类回退（FilteredClassLoader 模拟 classpath 缺失）
    ├── 用户 Bean 覆盖（@ConditionalOnMissingBean back off）
    └── 关闭无残留（上下文关闭后静态注册表/SPI 被对称清理）
```

## 3. 模块结构

所有测试类位于对应模块的 `src/test/java/` 下，命名规则：`XxxAutoConfigurationTest.java`。

## 4. 核心抽象

| 测试工具 | 用途 |
|----------|------|
| `ApplicationContextRunner` | 普通模块 |
| `WebApplicationContextRunner` | WebMVC 模块 |
| `ReactiveWebApplicationContextRunner` | WebFlux 模块 |
| `FilteredClassLoader` | 模拟 classpath 缺失 |
| `BeanClassLoaderOverride` | 覆盖类加载器 |

## 5. 配置结构

无独立配置。

## 6. 测试策略

### 6.1 五个维度

1. **默认装配**：`runner.run(ctx -> assertThat(ctx).hasSingleBean(Xxx.class))`
2. **开关回退**：`runner.withPropertyValues("ddd4j.xxx.enabled=false").run(ctx -> assertThat(ctx).doesNotHaveBean(Xxx.class))`
3. **缺类回退**：`runner.withClassLoader(new FilteredClassLoader(SomeClass.class)).run(...)`
4. **用户覆盖**：`runner.withBean(Xxx.class, customImpl).run(ctx -> assertThat(ctx).getBean(Xxx.class).isSameAs(customImpl))`
5. **关闭无残留**：验证上下文关闭后静态注册表被清理

### 6.2 覆盖率

- `ddd4j-boot-core`：2 个测试（Core + Repository）
- `ddd4j-boot-web`：2 个测试（WebMVC + WebFlux）
- `ddd4j-boot-data`：5 个测试
- `ddd4j-boot-auth`：4 个测试
- `ddd4j-boot-mq`：14 个测试
- `ddd4j-boot-cache`：1 个测试
- `ddd4j-boot-extensions`：9 个测试
- **总计：37 个契约测试**

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 7 节

## 8. 实施风险与缓解

无重大风险。测试本身就是缓解措施。

## 9. 交付物清单

- [x] 37 个 ApplicationContextRunner 契约测试
- [x] 提交：`16529a4f test: 为全部 @AutoConfiguration 补齐 ApplicationContextRunner 契约测试并修复装配缺陷`

## 10. 未决事项

- 无
