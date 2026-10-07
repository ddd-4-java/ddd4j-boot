# ddd4j-boot 可观测性配置手册（链路追踪）

> 模块：`io.ddd4j.boot:ddd4j-boot-observability`（3.5.x 分支，Spring Boot 3.5.16 / JDK 17）
> 规格：`family-observability`（对应 change：`establish-boot-observability`）
> 字段命名：家族 design D9 冻结表——追踪头 `traceparent`、诊断回传头 `X-Trace-Id`、
> 日志字段 `traceId`/`correlationId`/`causationId`、脱敏占位符 `***REDACTED***`。

## 1. 引入依赖

业务项目（继承 `ddd4j-boot-parent` 或导入 `ddd4j-boot-bom`）：

```xml
<dependency>
    <groupId>io.ddd4j.boot</groupId>
    <artifactId>ddd4j-boot-observability</artifactId>
</dependency>
```

版本由 `ddd4j-boot-bom` / `ddd4j-boot-dependencies` 统一管理
（micrometer-tracing 对齐 Spring Boot 3.5.16 BOM 管理版本，选版理由见
`ddd4j-boot-dependencies/pom.xml` properties 注释）。

## 2. 开启 / 关闭

追踪能力**默认关闭**（`enabled=false`），关闭时零装配、对外行为与未引入本模块前一致。

```properties
# 开启链路追踪
ddd4j.observability.tracing.enabled=true

# 关闭（默认值，可省略）
ddd4j.observability.tracing.enabled=false
```

开启后自动生效：

- 入站：Servlet 过滤器继承合法 `traceparent`（无头/非法头则新建 trace 标识），
  响应回传 `X-Trace-Id`；
- 上下文：`traceId`/`correlationId`/`causationId` 双写 SLF4J MDC 与 TTL `ThreadContext`
  （日志 pattern 放置 `%X{traceId}` 即可渲染）；
- 异步：`TraceContextPropagatingExecutor.decorate(executor)` 装饰任意线程池即可透传；
- 出站：把 `TraceparentRestTemplateInterceptor` 加入 `RestTemplate#setInterceptors`，
  或 `WebClient.builder().filter(TraceparentWebClientFilter.traceparent())`。

日志 pattern 示例（logback）：

```xml
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId} corr=%X{correlationId}] - %msg%n</pattern>
```

## 3. 采样率

```properties
# 全采样（默认）
ddd4j.observability.tracing.sampling-rate=1.0

# 10% 采样
ddd4j.observability.tracing.sampling-rate=0.1

# 不导出（仅保留本地关联标识）
ddd4j.observability.tracing.sampling-rate=0.0
```

## 4. OTLP 导出端点

不配置端点 = 无导出模式（Graceful degradation：仅本地记录，业务零影响）。
配置端点后经 OTLP/HTTP 异步批量导出，后端不可达仅丢失导出、不产生调用失败。

```properties
ddd4j.observability.tracing.otlp-endpoint=http://otel-collector:4318/v1/traces
```

服务名注册（span resource `service.name`）：

```properties
ddd4j.observability.tracing.service-name=my-application
```

## 5. 敏感材料脱敏

默认拒绝 + 白名单放行。首批敏感 key 匹配集（D9，归一化后 contains 命中）：
`privateKey` / `private_key` / `mnemonic` / `seed` / `seedPhrase` / `secretKey` / `keystore`，
命中值在 span 起始期与导出期双重拦截，置为 `***REDACTED***`。
**私钥、助记词不得进入日志/指标/通用 DTO/追踪。**

```properties
# 业务白名单（逗号分隔，命中白名单的 key 保持原值）
ddd4j.observability.tracing.redact-allowlist=seed,serviceName
```

## 6. 告警关联（extension-monitor）

`ddd4j-boot-extension-monitor` 提供 `AlertTraceContext` Bean（自动装配），
发送钉钉/企微告警时装饰正文即可携带 `traceId` + `correlationId`：

```java
@Autowired
private AlertTraceContext alertTraceContext;

String payload = alertTraceContext.decorate("订单支付失败：超时");
```

## 7. 开销基准

开启态单次开销门禁：< 100µs（`ObservabilityGoldenMustTest#mustBoundTracingOverhead` 断言）。
每次复测数据落盘 `ddd4j-boot-observability/target/observability-baseline/trace-overhead-baseline.json`；
4.0.x 线基线数据见该分支 `docs/observability/trace-overhead-baseline.json`。

复测命令（按写即跑）：

```bash
export JAVA_HOME=D:/Java/jdk-17.0.12
mvn test -f ddd4j-boot-observability/pom.xml -Dtest='ObservabilityGoldenMustTest#mustBoundTracingOverhead'
```

## 8. 全量测试（验收命令）

```bash
export JAVA_HOME=D:/Java/jdk-17.0.12
mvn test -f ddd4j-boot-observability/pom.xml
mvn test -f ddd4j-boot-extensions/ddd4j-boot-extension-monitor/pom.xml
```
