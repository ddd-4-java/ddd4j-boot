# ddd4j-boot 可观测性配置手册（链路追踪 · JDK 8 线最小集）

> 模块：`io.ddd4j.boot:ddd4j-boot-observability`（2.7.x 分支，Spring Boot 2.7.18 / JDK 8）
> 规格：`family-observability`（对应 change：`establish-boot-observability`）
> 字段命名：家族 design D9 冻结表——追踪头 `traceparent`、诊断回传头 `X-Trace-Id`、
> 日志字段 `traceId`/`correlationId`/`causationId`、脱敏占位符 `***REDACTED***`。

## 0. 本线范围裁定（design 风险预案）

Spring Boot 2.x（2.3–2.7）生态无 micrometer-tracing / OpenTelemetry 同代栈（Boot 3.0 才引入
micrometer-tracing BOM），本线为**纯 Java 退化最小集**，不引入 micrometer-tracing/OTel 依赖：

- `TraceparentCodec`：W3C traceparent 编解码/生成（纯 Java，无 OTel SpanContext 转换）；
- `TraceContextBridge` + `MdcTtlTraceContextBridge`：traceId/correlationId/causationId
  三键并列双写 SLF4J MDC 与底座 TTL ThreadContext（因果链 D2 语义不变）；
- `TraceparentRestTemplateInterceptor`：RestTemplate 出站注入合法 traceparent
  （trace 标识取自桥，无值时新建）；
- `extension-monitor` 的 `AlertTraceContext`：告警正文追加 traceId/correlationId 关联字段。

行为契约不变：traceparent 编解码语义、因果三键互查、出站头恒合法、D9 命名——与
3.0.x+/4.0.x+ 完整版一致；差异仅为无 OTel span 装配/采样/OTLP 导出/脱敏 span 处理器。
`ddd4j.observability.tracing.enabled` / `sampling-rate` 配置键为本家族冻结键（D9），
本线以文档形式保留契约位，完整装配在 Boot 3.x+ 线生效。

## 1. 引入依赖

```xml
<dependency>
    <groupId>io.ddd4j.boot</groupId>
    <artifactId>ddd4j-boot-observability</artifactId>
</dependency>
```

版本由 `ddd4j-boot-bom` 统一管理（2.7.x 分支 = `io.ddd4j` 1.0.x 线）。

## 2. 上下文桥接与日志渲染

```java
TraceContextBridge bridge = new MdcTtlTraceContextBridge();
bridge.attach(traceId, correlationId, causationId);   // 三键并列双写 MDC + TTL
try {
    // 业务逻辑；日志 pattern 放置 %X{traceId} 即可渲染
} finally {
    bridge.detach();                                   // 防线程池复用泄漏
}
```

logback pattern 示例：

```xml
<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId} corr=%X{correlationId}] - %msg%n</pattern>
```

## 3. 出站注入（RestTemplate）

```java
restTemplate.getInterceptors().add(new TraceparentRestTemplateInterceptor());
```

## 4. 告警关联（extension-monitor）

```java
@Autowired
private AlertTraceContext alertTraceContext;

String payload = alertTraceContext.decorate("订单支付失败：超时");
```

## 5. 全量测试（验收命令）

```bash
export JAVA_HOME=D:/Java/jdk1.8.0_321
mvn test -f ddd4j-boot-observability/pom.xml
mvn test -f ddd4j-boot-extensions/ddd4j-boot-extension-monitor/pom.xml
```
