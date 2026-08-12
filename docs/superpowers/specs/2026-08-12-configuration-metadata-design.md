# 配置属性元数据设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-core`、`ddd4j-boot-extensions`

## 1. 目标与范围

为声明配置属性的 starter 增加 `spring-boot-configuration-processor`，生成 IDE 元数据。不机械地给完全没有属性类的模块添加。

## 2. 总体架构

```
ddd4j-boot-core/pom.xml
  └── <optional>true</optional>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-configuration-processor</artifactId>

ddd4j-boot-extensions/pom.xml
  └── 同上
```

## 3. 模块结构

| 模块 | 是否有配置属性类 | 是否添加 processor |
|------|-----------------|-------------------|
| ddd4j-boot-core | 是 | 是 |
| ddd4j-boot-extensions (聚合) | 是 | 是 |
| ddd4j-boot-web-webmvc | 是 | 否（由 extensions 聚合管理） |
| ddd4j-boot-web-webflux | 是 | 否 |
| ddd4j-boot-mq-core | 是 | 否 |

## 4. 核心抽象

无新增类。仅在 POM 中增加 optional 依赖。

## 5. 配置结构

IDE 元数据自动生成到 `META-INF/additional-spring-configuration-metadata.json` 或 `META-INF/spring-configuration-metadata.json`。

## 6. 测试策略

- 为每个 `AutoConfiguration.imports` 增加元数据加载测试
- 确保自动配置类确实可被 Boot 发现

## 7. 文档与示例

- 无独立文档

## 8. 实施风险与缓解

无重大风险。optional 依赖不影响传递。

## 9. 交付物清单

- [x] `ddd4j-boot-core/pom.xml` 增加 configuration-processor
- [x] `ddd4j-boot-extensions/pom.xml` 增加 configuration-processor

## 10. 未决事项

- 无
