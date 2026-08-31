# ddd4j-boot：JDK 8 至 JDK 17 迁移

本迁移适用于 `2.3.x`–`2.6.x` 升级到 `2.7.x` 或 `3.0.x`–`3.5.x`。目标是逻辑等价，而非二进制兼容。

## 必须处理的替换

| 来源 | 目标 | 保持不变的行为 |
|---|---|---|
| `META-INF/spring.factories` | Boot 2.7 兼容入口或 Boot 3 `AutoConfiguration.imports` | 默认 Bean、`enabled=false`、缺类回退、用户 Bean 退让、关闭清理 |
| `javax.servlet.*` | Boot 3 的 `jakarta.servlet.*` | HTTP 状态、错误体、请求上下文、公共路径和幂等策略 |
| Java 8 字节码 | Java 17 字节码 | 公开 ddd4j 配置键、默认值和禁用语义 |
| Boot 2 Jackson 2 坐标 | 目标维护线声明的 Jackson 适配层 | 对外 JSON 字段名、错误结构和时间格式 |

## 最小验证

1. 使用目标分支实际的 JDK 和 Maven wrapper/插件运行自动配置契约测试。
2. 运行 WebMVC 与 WebFlux 的 default/disable/missing-class/user-bean/close-recreate 测试。
3. 用消费者 fixture 比较相同请求的 HTTP 状态与 JSON 外部形状。
4. 记录任何删改的配置键；没有映射的删除不能标为兼容。

跨此边界的 japicmp 报告只作迁移审查输入，不能单独判定逻辑失败。
