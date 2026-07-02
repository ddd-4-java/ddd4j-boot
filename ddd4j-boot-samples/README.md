# ddd4j-boot 示例工程

本目录放 Spring Boot 运行时关注的示例：自动配置、Spring MVC、MyBatis-Plus、数据源、事务和 Spring 生态接入。通用领域模型优先复用 `io.ddd4j:ddd4j-sample-*`。

## 重点示例

| 示例                                | 方向        | 说明 |
|-----------------------------------|-----------|------|
| `ddd4j-boot-sample-rich-model`    | 普通充血模型   | Spring MVC + `MybatisAggregateRepository` + MyBatis-Plus Mapper，演示 Model/PO 分离和 `lambdaQuery()` 语义查询 |
| `ddd4j-boot-sample-layered`       | 兼容 CRUD 轨道 | 旧 `Model/Query/BaseRepository` ActiveRecord 风格示例，保留给快速 CRUD 和迁移参考 |
| `ddd4j-boot-sample-cqrs-person-*` | CQRS / ES | Spring Boot 命令侧、查询侧和共享模块示例 |
| `ddd4j-boot-sample-auth-*`        | Auth      | 三种鉴权实现接入示例 |

验证命令：

```bash
mvn -pl ddd4j-boot-samples/ddd4j-boot-sample-rich-model -am compile -DskipTests
```
