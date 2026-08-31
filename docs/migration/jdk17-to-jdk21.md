# ddd4j-boot：JDK 17 至 JDK 21 / Boot 4 迁移

本迁移适用于 `2.7.x`、`3.0.x`–`3.5.x` 升级到 `4.0.x` 或 `4.1.x`。该路径要求 Java 21 和 Boot 4，但仍以公开行为一致为验收目标。

## 必须处理的替换

| 来源 | 目标 | 保持不变的行为 |
|---|---|---|
| Boot 2.7/3 自动配置入口 | Boot 4 `AutoConfiguration.imports` | 条件装配五维与资源关闭所有权 |
| Java 17 运行时 | Java 21 运行时 | 业务默认值、认证失败语义、缓存与 MQ 关闭语义 |
| Boot 3 BOM/Jackson 适配层 | Boot 4 BOM/Jackson 适配层 | 对外配置键、JSON wire contract、错误响应 |
| Boot 3 Jakarta Web | Boot 4 Jakarta Web | Servlet/WebFlux 条件边界、请求上下文和异常翻译 |

## 最小验证

1. 以 Java 21 运行每个实际 starter 的自动配置契约测试。
2. 运行有效 POM、依赖树和公开构件解析检查，确认 ddd4j 3.0.x 的来源。
3. 运行 Web、Data/Cache、MQ 的可执行真实依赖 profile；不可用环境必须记录 `BLOCKED`。
4. 对移除的上游 API 提供替换类型、配置键映射和消费者验证命令。

同组内 API/配置差异仍需通过组内比较门禁；本迁移文件不豁免未文档化的同组破坏。
