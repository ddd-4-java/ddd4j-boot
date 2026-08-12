# Cache 与扩展模块自动装配设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-cache`、`ddd4j-boot-extensions/*`

## 1. 目标与范围

Cache 自动配置统一为 Boot 3 `@AutoConfiguration` 形态，真正装配上游 cache SPI/实现。扩展模块逐一按 classpath、enabled property、用户 Bean backoff 三项审查。

## 2. 总体架构

```
ddd4j-boot-cache/                    → Ddd4jCacheAutoConfiguration
ddd4j-boot-extensions/
  ddd4j-boot-extension-akka/         → Ddd4jAkkaBootAutoConfiguration
  ddd4j-boot-extension-cola/         → ColaAutoConfiguration
  ddd4j-boot-extension-dubbo/        → DubboAutoConfiguration
  ddd4j-boot-extension-excel/        → Ddd4jExcelBootAutoConfiguration
  ddd4j-boot-extension-jackson/      → DefaultJacksonAutoConfiguration
  ddd4j-boot-extension-monitor/      → Ddd4jMonitorBootAutoConfiguration
  ddd4j-boot-extension-qlexpress/    → Ddd4jQLExpressBootAutoConfiguration
  ddd4j-boot-extension-qrcode/       → Ddd4jQrCodeBootAutoConfiguration
```

## 3. 模块结构

| 模块 | 自动配置类 | 测试 |
|------|-----------|------|
| cache | `Ddd4jCacheAutoConfiguration` | `Ddd4jCacheAutoConfigurationTest` |
| extension-akka | `Ddd4jAkkaBootAutoConfiguration` | `Ddd4jAkkaBootAutoConfigurationTest` |
| extension-cola | `ColaAutoConfiguration` | `ColaAutoConfigurationTest` |
| extension-dubbo | `DubboAutoConfiguration` | `DubboAutoConfigurationTest` |
| extension-excel | `Ddd4jExcelBootAutoConfiguration` | `Ddd4jExcelBootAutoConfigurationTest` |
| extension-jackson | `DefaultJacksonAutoConfiguration` | `DefaultJacksonAutoConfigurationTest` |
| extension-monitor | `Ddd4jMonitorBootAutoConfiguration` | `Ddd4jMonitorBootAutoConfigurationTest` |
| extension-qlexpress | `Ddd4jQLExpressBootAutoConfiguration` | `Ddd4jQLExpressBootAutoConfigurationTest` |
| extension-qrcode | `Ddd4jQrCodeBootAutoConfiguration` | `Ddd4jQrCodeBootAutoConfigurationTest` |

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jCacheAutoConfiguration` | 装配上游 cache SPI/实现 |
| `Ddd4jQLExpressBootAutoConfiguration` | QLExpress 规则引擎 + RuleService |
| `DefaultJacksonAutoConfiguration` | Jackson 序列化定制 |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `ddd4j.akka` | `enabled` | `true` | Akka 开关 |
| `ddd4j.cola` | `enabled` | `true` | COLA 开关 |
| `ddd4j.dubbo` | `enabled` | `true` | Dubbo 开关 |
| `ddd4j.excel` | `enabled` | `true` | Excel 开关 |
| `ddd4j.qrcode` | `enabled` | - | 二维码开关 |
| `ddd4j.qrcode.web` | `enabled` | - | Web 端点开关 |

## 6. 测试策略

- 每个模块均有 `ApplicationContextRunner` 契约测试
- 不让扩展模块修改全局 ObjectMapper 的不可预期行为

## 7. 文档与示例

- 迁移指南：`docs/migration-3.4x-2.0x.md` 第 5 节

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| 扩展模块修改全局 ObjectMapper | 优先使用 Jackson2ObjectMapperBuilderCustomizer 或独立 qualifier |

## 9. 交付物清单

- [x] 1 个 Cache + 8 个 Extension 自动配置
- [x] 9 个契约测试

## 10. 未决事项

- 无
