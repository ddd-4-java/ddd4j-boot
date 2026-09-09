# ddd4j-boot 4.1 依赖归属审计

## 结论

`ddd4j-boot` 4.1 当前存在两类越界：根 POM 主动导入或直接管理普通 Spring/Reactor/Jackson
组件，`ddd4j-boot-dependencies` 又直接管理 Testcontainers、TrueLicense、ZXing 和 Tio。它们都应由
`ddd4j-dependencies` 统一管理；Boot 层只保留 Spring Boot BOM、Boot 核心制品、Starter 与 Boot
专属集成。

本报告依据结构化 XML 解析和本地已安装的
`io.ddd4j:ddd4j-dependencies:3.0.x.20260630-SNAPSHOT` 有效 POM，不以属性名或注释推断版本来源。

## 父模型告警基线

| 指标 | 修改前 | 方案 1 后 | 结论 |
|---|---:|---:|---|
| 总模型问题 | 178,859 | 175,007 | 减少 3,852 |
| Model 4.1 同时声明 GAV/relativePath | 72 | 0 | 已消除 |
| Maven rc6 默认 `..` 路径不匹配 | 0 | 13 | 精确白名单 |
| `parent.version is missing` | 0 | 0 | 构建可解析 |

13 条默认路径提示由 `config/consistency/model41-parent-warning-allowlist.tsv` 管理。白名单新增、遗漏或
失效都会使 `verify_model41_parent_contract.py` 失败。

## 根 POM 归属清单

| Coordinate | Current owner | Required owner | Current version | Present upstream | Action |
|---|---|---|---|---|---|
| org.springframework:spring-framework-bom | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, BOM 6.2.16 and direct components 7.0.8 | remove downstream BOM import after effective-version test |
| org.springframework.security:spring-security-bom | ddd4j-boot root | ddd4j-dependencies | 7.1.0 | yes, components 7.0.6 | align upstream baseline before removing downstream import |
| io.projectreactor:reactor-bom | ddd4j-boot root | ddd4j-dependencies | 2025.0.6 inherited | yes, effective reactor-core 3.8.6 | remove downstream duplicate after effective-version test |
| tools.jackson:jackson-bom | ddd4j-boot root | ddd4j-dependencies | 3.2.1 | yes, effective Jackson 3.2.1 | remove downstream duplicate |
| org.springframework:spring-aop | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-beans | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-context | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-core | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-expression | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-jms | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-messaging | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-test | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-web | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-webflux | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |
| org.springframework:spring-webmvc | ddd4j-boot root | ddd4j-dependencies | 7.0.8 | yes, 7.0.8 | remove downstream duplicate |

Spring Boot 的以下项目符合生态归属，保留在根 POM：

- `org.springframework.boot:spring-boot-dependencies:4.1.0`；
- `org.springframework.boot:spring-boot:4.1.0`；
- `org.springframework.boot:spring-boot-starter:4.1.0`；
- `org.springframework.boot:spring-boot-starter-web:4.1.0`。

## ddd4j-boot-dependencies 归属清单

| Coordinate | Current owner | Required owner | Current version | Present upstream | Action |
|---|---|---|---|---|---|
| com.fasterxml.jackson.core:jackson-annotations | ddd4j-boot-dependencies property | ddd4j-dependencies | 2.22 | yes, 2.22 | remove orphan property |
| org.testcontainers:testcontainers-localstack | ddd4j-boot-dependencies | ddd4j-dependencies | requested 2.0.5 | yes, effective 2.0.3 | update upstream to 2.0.5 before removing direct version |
| de.schlichtherle.truelicense:truelicense-core | ddd4j-boot-dependencies | ddd4j-dependencies | 1.33 | yes, 1.33 legacy | remove Boot duplicate; remove upstream legacy entry in platform repair |
| de.schlichtherle.truelicense:truelicense-xml | ddd4j-boot-dependencies | ddd4j-dependencies | 1.33 | yes, 1.33 legacy | remove Boot duplicate; remove upstream legacy entry in platform repair |
| io.github.easy4j:zxing-extension | ddd4j-boot-dependencies | ddd4j-dependencies | 4.1.x.20260630-SNAPSHOT | yes, 2.0.x.20260630-SNAPSHOT | verify and correct upstream 3.0-line version before removing duplicate |
| org.t-io:tio-core | ddd4j-boot-dependencies | ddd4j-dependencies | 3.8.6.v20240801-RELEASE | yes, same version | remove downstream duplicate |

## 保留的 Boot 生态依赖

以下坐标虽不都以 `spring-boot` 命名，但实际是 Boot Starter 或 Boot 专属聚合，不迁移到平台层：

- Guerlab SMS `*-starter`；
- OpenTracing Spring Jaeger Starter；
- Springdoc OpenAPI Starter；
- Knife4j Spring Boot Starter；
- TongWeb Spring Boot Starter；
- Easy4J 各 Spring Boot Starter；
- Spring Boot Admin 和 Aliyun Spring Boot BOM。

## 上游闭环

为避免操作已有 worktree，本任务使用独立 Git clone 处理 `ddd4j feature/3.0.x`。上游提交
`7c2da70b` 已完成：

- Spring Security `7.1.0`；
- Testcontainers LocalStack `2.0.5` 直接约束；
- ZXing Extension `3.0.x.20260630-SNAPSHOT`；
- TrueLicense 旧坐标在当前远程基线中已为 0。

Maven 4 需要在安装前执行 `clean`，否则可能复用修改前的 `target/consumer-*.pom`。重新生成
consumer POM 后，Boot 4.1 有效模型已通过四项平台版本契约。

## 最终验证

| 项目 | Boot 4.0 | Boot 4.1 |
|---|---|---|
| Model 4.1 结构门禁 | 通过 | 通过 |
| 默认父路径白名单 | 13/13 | 13/13 |
| 依赖归属门禁 | 通过 | 通过 |
| 平台有效版本契约 | 4/4 | 4/4 |
| License 测试 | 3/3 | 3/3 |
| License + Jackson 聚焦编译 | 通过 | 通过 |
| 完整 reactor | 73/73，通过（Boot 4.0.8） | 73/73，通过（Boot 4.1.0） |

Boot 4.0 模型问题从原始 `180,303` 经父引用修复降为 `180,244`，在换用新 ddd4j
consumer POM 并清理 Boot 重复管理后为 `181,401`。该增加表示上游 BOM 冲突展开条目变化，
不表示关键有效版本契约失败。

两条完整 reactor 原先均停在 `ddd4j-boot-sample-starter-druid`，首批编译错误为：

- Boot 4 不再提供旧 `org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer`；
- `com.github.dozermapper.extra.converters` 转换器缺失；
- MyBatis-Plus `IService` / `ServiceImpl` 未进入样例编译类路径。

这些错误及后续 TestRestTemplate、Easy4J Validation、MyBatis-Plus ActiveRecord、WebFlux API
和 Resilience4j Boot Starter 迁移均已完成。`ddd4j-boot-dependencies` 4.x 通过
`spring-boot-starter-resilience4j.version=${resilience4j.version}` 管理 `resilience4j-spring-boot4`；
当前 effective version 为 `2.4.0`。`ddd4j-dependencies` 生成的 consumer POM 不再包含 Boot/Cloud
专属 Starter。

## BOM 冲突精确治理

Boot 4.1 最终 Maven 4 消费模型仍报告 `175,007` 个展开模型问题，其中有 `7,286` 条
`Ignored POM import` 记录；按坐标和两侧版本精确归一后为 232 个元组。旧报告的 233 是按完整
消息文本去重所得。数量最多的组仍包括：

| Group | 展开条数 |
|---|---:|
| `org.apache.activemq` | 4,181 |
| `io.micrometer` | 825 |
| `org.hibernate.orm` | 672 |
| `org.slf4j` | 336 |
| `org.glassfish.jaxb` | 336 |
| `com.sun.xml.bind` | 240 |
| `com.oracle.database.jdbc` | 192 |

ddd4j 源头日志的 `11,786` 条展开记录归一为 449 个精确元组，全部进入坐标、两侧版本、最终版本、
authority 和 reason 完整的白名单。Boot 消费侧另有 232 个精确元组；自动化验证确认 232/232 的
Boot effective final version 与 ddd4j effective version 一致，`drift=0`、`missing_upstream=0`。

独立代码审查最初发现 3 个 Important：上游仍发布四个生态 Starter、effective POM 参数可省略、
authority 仅为自声明。修复后复审结果为 Critical 0、Important 0。Maven deploy、空缓存消费和
GitHub Actions 未作为本次完成证明执行。
