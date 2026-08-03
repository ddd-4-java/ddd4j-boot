# ddd4j-boot 示例工程

本目录放 Spring Boot 运行时关注的示例：自动配置、Spring MVC、MyBatis-Plus、数据源、事务和 Spring 生态接入。通用领域模型优先复用 `io.ddd4j:ddd4j-sample-*`。

## 重点示例

| 示例                                | 方向        | 说明 |
|-----------------------------------|-----------|------|
| `ddd4j-boot-sample-order`         | DDD / CQRS | 基于共享 Order 领域与应用内核；默认内存启动，`postgres` profile 演示 PostgreSQL、Redis、Kafka 与事务 Outbox |
| `ddd4j-boot-sample-layered`       | 兼容 CRUD 轨道 | 旧 `Model/Query/BaseRepository` ActiveRecord 风格示例，保留给快速 CRUD 和迁移参考 |
| `ddd4j-boot-sample-auth-*`        | Auth      | 三种鉴权实现接入示例 |

验证命令：

```bash
mvn -pl ddd4j-boot-samples/ddd4j-boot-sample-order -am test
```

`ddd4j-boot-sample-order` 默认使用 `in-memory` profile，便于先验证显式用例路由、Bearer Subject 与业务不变量。切换到
`postgres` profile 后，示例会使用 PostgreSQL 写侧/读模型、Redis 支付幂等和 Kafka Outbox 发布；Redis 不可用时降级到本地
`CacheKit`，Kafka 关闭时 Outbox 消息保持待发布状态，等待后续重试。
