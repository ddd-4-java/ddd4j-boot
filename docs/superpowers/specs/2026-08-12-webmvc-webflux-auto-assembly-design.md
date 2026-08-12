# WebMVC / WebFlux 自动装配设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-web/ddd4j-boot-web-webmvc`、`ddd4j-boot-web/ddd4j-boot-web-webflux`

## 1. 目标与范围

保持 Servlet 与 Reactive 条件互斥，复用 `ddd4j-web-core` 的异常翻译、Request ID、Client IP、访问策略和统一响应。不在 Boot 另建协议。

### 非目标

- 不复制 `ddd4j-web-core` 的异常翻译或统一响应实现

## 2. 总体架构

```
Ddd4jWebMvcAutoConfiguration (@AutoConfiguration)
  ├── @ConditionalOnWebApplication(type = SERVLET)
  ├── @Import(Ddd4jWebCoreConfiguration.class)
  ├── @Bean 公开路径、信任转发头配置
  └── 请求结束清理：Servlet Filter/Interceptor

Ddd4jWebFluxAutoConfiguration (@AutoConfiguration)
  ├── @ConditionalOnWebApplication(type = REACTIVE)
  ├── @ConditionalOnProperty("ddd4j.web.webflux.enabled")
  └── Reactor Context bridge（避免 ThreadLocal 串请求）
```

## 3. 模块结构

```
ddd4j-boot-web/
  ddd4j-boot-web-webmvc/
    src/main/java/io/ddd4j/boot/web/webmvc/
      Ddd4jWebMvcAutoConfiguration.java
      Ddd4jWebMvcProperties.java
    src/test/.../Ddd4jWebMvcAutoConfigurationTest.java
  ddd4j-boot-web-webflux/
    src/main/java/io/ddd4j/boot/web/webflux/
      Ddd4jWebFluxAutoConfiguration.java
      Ddd4jWebFluxProperties.java
    src/test/.../Ddd4jWebFluxAutoConfigurationTest.java
```

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `Ddd4jWebMvcAutoConfiguration` | Servlet 环境条件装配 |
| `Ddd4jWebFluxAutoConfiguration` | Reactive 环境条件装配 |
| `Ddd4jWebMvcProperties` | MVC 配置属性 |
| `Ddd4jWebFluxProperties` | WebFlux 配置属性 |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `ddd4j.web` | `public-paths` | - | 公开路径 |
| `ddd4j.web` | `trust-forwarded-headers` | - | 信任转发头 |
| `ddd4j.web.webflux` | `enabled` | `true` | WebFlux 装配开关 |

## 6. 测试策略

- ApplicationContextRunner：默认装配、enabled=false、缺类回退、用户覆盖、关闭无残留
- WebApplicationContextRunner / ReactiveWebApplicationContextRunner

## 7. 文档与示例

- `docs/guides/webmvc-flow.md`
- `docs/guides/webflux-flow.md`

## 8. 实施风险与缓解

无重大风险。Servlet 与 Reactive 条件互斥已通过 `@ConditionalOnWebApplication` 保证。

## 9. 交付物清单

- [x] `Ddd4jWebMvcAutoConfiguration.java`
- [x] `Ddd4jWebFluxAutoConfiguration.java`
- [x] 契约测试

## 10. 未决事项

- 无
