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

## Boot 4.0 传播结果

Boot 4.0 使用 Spring Boot `4.0.8`，同时继承与 4.1 相同的 ddd4j 3.0 平台基线。归属门禁、
四项有效版本契约、license/Jackson 聚焦编译均通过。

4.0 总模型问题从原始 `180,303` 经父模型修复降为 `180,244`，换用新 ddd4j consumer POM 并清理
Boot 重复管理后为 `181,401`。该数量增加表示上游 BOM 冲突在 Maven 4.0.0-rc-6 中的展开条目
变化，不表示归属门禁失败；仍需在最终告警台账中按坐标归类，不使用总数代替版本契约。
