# Maven Model 4.1 与平台依赖权威治理规格

状态：已实施。Boot 4.0.8/4.1.0 完整 reactor、平台/生态依赖边界、精确 BOM 冲突契约和独立审查均已闭环；远端发布与 Actions 仍为独立证据层。

## 背景

`ddd4j-boot` 的 `4.0.x`、`4.1.x` 使用 Maven Model 4.1 和 JDK 21。当前聚焦 reactor
可以编译，但 Maven 4 在构建有效模型时报告约 18 万项问题。其中大量问题是同一组父引用告警和
BOM 冲突在 73 个 reactor 模块中重复展开，不能据此认定存在 18 万个独立缺陷，也不能因为编译
成功而忽略最终版本的不确定性。

本次治理必须保持 ddd4j 的平台定位：`ddd4j-dependencies` 是自有体系内普通组件与 ddd4j 模块的
唯一版本权威。生态 BOM 只增加所在生态位的依赖，不得演变成第二套普通组件版本中心。

## 目标

1. 使 `ddd4j-boot` 4.0/4.1 的全部父引用符合 Maven Model 4.1。
2. 建立可执行的依赖归属规则，阻止普通组件版本散落到 Boot、Javalin、Quarkus、Cloud 生态 BOM。
3. 保持 `ddd4j-dependencies` 对普通组件版本的源头控制，使上游修复版本能被所有生态下游继承。
4. 清理 `ddd4j-boot-dependencies` 中越界的普通组件显式版本与直接管理项。
5. 用有效模型证明关键依赖的最终版本和来源，而不是依赖 BOM 导入顺序推断。

## 架构边界

```mermaid
flowchart TD
    Base["ddd4j-dependencies<br/>普通组件与 io.ddd4j 模块的唯一版本权威"]
    Boot["ddd4j-boot-dependencies<br/>仅 Spring Boot 生态增量"]
    Javalin["ddd4j-javalin-dependencies<br/>仅 Javalin 生态增量"]
    Quarkus["ddd4j-quarkus-dependencies<br/>仅 Quarkus 生态增量"]
    Cloud["ddd4j-cloud-dependencies<br/>仅 Spring Cloud 生态增量"]
    App["最终业务服务<br/>允许有意识地显式覆盖"]

    Base --> Boot
    Base --> Javalin
    Base --> Quarkus
    Base --> Cloud
    Boot --> App
    Javalin --> App
    Quarkus --> App
    Cloud --> App
```

### `ddd4j-dependencies`

必须管理：

- `io.ddd4j:*` 全部模块；
- Jackson、SLF4J、Logback、Netty、Hibernate、Micrometer、JAXB、数据库驱动、MQ 客户端等普通组件；
- 多个生态共同消费的工具库、协议库和基础扩展；
- Boot/Javalin/Quarkus/Cloud 未覆盖但属于平台公共基线的依赖。

不得为了消除 Maven 告警而删除这些公共版本的源头治理。需要减少重复 BOM 导入时，必须确保同等
版本约束仍由 `ddd4j-dependencies` 直接或间接提供，并由测试证明。

### 生态依赖 BOM

- `ddd4j-boot-dependencies`：Spring Boot BOM、Boot Starter、Boot 自动配置和 Boot 专属集成。
- `ddd4j-javalin-dependencies`：Javalin 核心及 Javalin 专属扩展。
- `ddd4j-quarkus-dependencies`：Quarkus BOM 与 Quarkus Extension。
- `ddd4j-cloud-dependencies`：Spring Cloud、Spring Cloud Alibaba 和云服务治理组件。

生态 BOM 可以导入官方生态 BOM。官方 BOM 隐式携带的普通组件不视为生态层主动维护，但生态层
不得再为这些普通组件创建独立版本属性或直接管理项。发生版本冲突时，最终普通组件版本必须符合
`ddd4j-dependencies` 的平台基线。

### 最终业务服务

业务服务可以因明确业务需要显式覆盖版本，但必须承担偏离平台基线的兼容验证责任。业务覆盖不应
反向污染平台 BOM，也不纳入生态 BOM 的禁止规则。

## 阶段一：Maven Model 4.1 父引用

### 规则

1. 外部父 POM：保留 `groupId/artifactId/version`，不声明 `relativePath`。
2. reactor 内部父 POM：保留 `groupId/artifactId/version`，不声明 `relativePath`；父坐标由 Maven 4 reactor 解析。
3. 本规则已通过 Maven 4.0.0-rc-6 的非默认父目录场景验证。仅保留 `relativePath` 会触发模型告警；只保留 `groupId/artifactId` 则无法在真实多层 reactor 中解析父版本。
4. 4.x 聚合仍使用 `<subprojects>`，不得退回 `<modules>`。

### 可观察验收

- 4.0、4.1 所有实际 `pom.xml` 都通过结构检查。
- Maven 4 输出中的 Model 4.1 `GAV + relativePath` 语法告警从 72 条降为 0；当前 Maven rc6 默认 `..` 路径检查保留 13 条精确白名单。
- 根、BOM、dependencies、parent、license 聚焦 reactor 均能解析和编译。
- 不改变任何制品坐标、版本号和模块集合。

## 阶段二：依赖归属和版本权威

### 归属清单

建立机器可读的坐标归属规则，至少区分：

- `platform`：只允许在 `ddd4j-dependencies` 主动管理；
- `spring-boot`：允许在 `ddd4j-boot-dependencies` 管理；
- `javalin`、`quarkus`、`spring-cloud`：预留给对应生态 BOM；
- `business-override`：仅用于最终业务服务，不进入框架 BOM。

检查器必须分析版本属性、直接 dependencyManagement 项和 BOM import。不能只按 groupId 粗略判断；
同一组织可能同时包含普通组件和生态专属 Starter，归属清单必须允许坐标级例外。

### Boot 层清理

1. 盘点 `ddd4j-boot-dependencies` 中全部显式版本属性和直接管理项。
2. 普通组件已有上游管理时，删除 Boot 层重复声明。
3. 普通组件上游缺失时，先补入 `ddd4j-dependencies`，验证发布/本地消费后再删除 Boot 层声明。
4. 保留 Spring Boot 官方 BOM、Boot Starter 和 Boot 专属扩展。
5. 有意覆盖必须落在 `ddd4j-dependencies`，附原因和回归证据，不得留在 Boot 层。

### 关键版本契约

有效模型检查至少覆盖：

- Jackson；
- SLF4J 与 Logback；
- Hibernate；
- Micrometer；
- ActiveMQ；
- JAXB；
- Netty；
- 数据库驱动。

每项契约必须同时断言期望版本和归属来源。仅断言“存在版本”或构建成功不算通过。

### 告警治理

- 下游显式重复管理导致的告警必须消除。
- BOM 隐式交集必须统计并建立基线，确认最终版本由平台层控制。
- 不允许通过交换 BOM 顺序、关闭告警或批量添加下游覆盖来刷绿。
- 如果 Maven 4 对已确定且无法消除的官方 BOM 交集继续告警，必须进入精确白名单；白名单包含坐标、
  两侧版本、最终版本、权威来源和保留原因。
- 新增未登记冲突必须使验证失败。

## 测试策略

### RED

1. Model 4.1 结构测试在当前父引用写法下失败，并报告具体 POM。
2. 依赖归属测试在 Boot 层发现普通组件显式版本时失败。
3. 有效模型测试在关键组件版本与 `ddd4j-dependencies` 不一致时失败。

### GREEN

1. 修改父引用后结构测试和 Maven 4 聚焦模型通过。
2. 将越界管理迁回平台层后归属测试通过。
3. 4.0、4.1 分别生成有效模型，关键组件版本与平台基线一致。

### 回归

- `scripts/consistency/test-maintenance-build-matrix.sh`；
- 依赖 BOM 边界测试；
- 4.0、4.1 license 聚焦测试；
- 受依赖调整影响的 Boot Starter 聚焦测试；
- 条件允许时执行 4.x 完整 reactor；否则明确记录阻塞模块，不以聚焦测试替代整仓结论。

## 分支与发布策略

1. 先在 `ddd4j-boot` `4.1.x` 建立测试和实现，再传播到 `4.0.x`。
2. 平台依赖缺口必须在 `ddd4j` `feature/3.0.x` 修复。本任务使用独立 Git clone，不操作既有 worktree。
3. 每个仓库、每条分支独立提交并验证。
4. commit、push、Maven deploy、空缓存消费和 Actions 是独立证据层。
5. 本规格不授权 Maven deploy；push 沿用当前用户授权，但必须先处理远端并行提交且禁止 force push。

## 非目标

- 不削弱 `ddd4j-dependencies` 对普通组件的统一治理。
- 不把 Spring Boot BOM 提升为全平台版本权威。
- 不在本阶段修改 Javalin、Quarkus、Cloud 仓库；只保证归属规则可扩展。
- 不允许生态 BOM 为追求局部构建成功复制普通组件版本。
- 不修改业务服务的显式版本覆盖。
- 不使用 Git worktree，不移除现有 worktree，不重写已推送历史。

## 完成定义

- Model 4.1 父引用结构规则在 4.0/4.1 全部通过，相关告警为 0。
- Boot 依赖归属检查通过，普通组件主动管理集中在 `ddd4j-dependencies`。
- 关键版本的最终值和权威来源均有自动化断言。
- 4.0/4.1 聚焦构建通过，并记录完整 reactor 的真实结果。
- 本地、跟踪分支和 GitHub SHA 完成对账；发布与空缓存消费状态单独报告。

## 阶段三：Boot 4 样例 reactor 闭环

Boot 4.0 和 4.1 均在完整 73 模块 reactor 的第 57 个模块 `ddd4j-boot-sample-starter-druid` 停止，前 56 个模块通过。已证实三类失败：

- `MeterRegistryCustomizer` 仍引用 Boot 3 Actuator 包；Boot 4 包为 `org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer`。
- `io.github.easy4j:dozer-extra-converters` 2.0/3.0 本地 JAR 均无 class；全树扫描确认 14 份 `DozerMapperConfiguration` 引用其类，14 个样例 POM 声明该空制品。
- MyBatis-Plus 3.5.17 已将 `IService` / `ServiceImpl` 迁移到 `com.baomidou.mybatisplus.spring.service` 包。
- Boot 4 将 `TestRestTemplate` 移入 `spring-boot-resttestclient`，并要求 `@AutoConfigureTestRestTemplate`。
- Easy4J Validation、MyBatis-Plus ActiveRecord 和 Spring WebFlux 7 均有命名空间或常量迁移。
- Resilience4j 的 Boot 专属 Starter 由 `ddd4j-boot-dependencies` 按生态线管理：4.x 使用 `resilience4j-spring-boot4:2.4.0`，不得下放到 `ddd4j-dependencies`。

实施规则：

- 修改两份 `DemoApplication` 的 MeterRegistryCustomizer import，保持公共标签行为。
- 删除 14 份无实现依赖的 `DozerMapperConfiguration` 和 14 个样例 POM 中的空 converter 依赖，保留 Dozer 核心 Starter；静态契约必须保证两类引用均归零。
- 将样例 MyBatis-Plus Service import 迁移到 3.5.17 新包，不创建兼容空壳。
- 将测试、Validation、ActiveRecord 和 WebFlux 旧 API 迁移到当前 Boot 4 / Spring 7 坐标，并由全树静态契约防止回归。
- `ddd4j-boot-dependencies` 以 `<spring-boot-starter-resilience4j.version>${resilience4j.version}</spring-boot-starter-resilience4j.version>` 复用 ddd4j 按 JDK 线确定的统一版本，并管理 `resilience4j-spring-boot4`；4.x 样例禁止依赖 Boot 2 Starter。
- 4.1 先完成测试和 73/73，再传播到 4.0；禁止排除样例或跳过编译刷绿。

验收：两个分支的目标样例测试通过，完整 reactor 都达到 73/73。

## 阶段四：ddd4j 多 BOM 冲突源头治理

Boot 4.1 当前有 `7,286` 条 `Ignored POM import` 展开记录，去重后为 `233` 个冲突组合。治理顺序为 ActiveMQ、Micrometer、Hibernate、SLF4J/JAXB，再处理 Oracle JDBC、Brave、gRPC、GraphQL、Ehcache、Elasticsearch 等剩余组。

每个冲突族必须在 `ddd4j-dependencies` 选定唯一期望版本并由有效 POM 契约断言。优先删除与平台基线重复的 BOM import，但只有该 BOM 的独有坐标仍被平台管理时才能删除；必须保留的 BOM 由 ddd4j 源头添加直接约束。

禁止仅交换 BOM 顺序、关闭告警或扩大模糊白名单。每完成一族，必须运行 ddd4j 有效模型、`clean install`、Boot 4.1/4.0 有效模型和聚焦 reactor。

验收：233 个基线冲突全部消除，或进入包含两侧版本、最终版本、权威来源和原因的坐标级精确白名单；新增或版本变化的冲突必须使验证失败。

## 阶段五：独立代码审查

样例 73/73 和 BOM 契约完成后，对实际 Git 变更派发独立代码审查。审查覆盖正确性、版本权威、Boot 4.0/4.1 兼容、consumer POM 和回归证据。Critical/Important 问题必须修复并重新验证。

## 扩展完成定义

- Boot 4.1 和 4.0 完整 reactor 均为 73/73。
- 233 个冲突已消除或进入带最终版本和权威来源的精确白名单。
- 独立审查无未处理的 Critical/Important 问题。
- 两个 Boot 分支及 ddd4j `feature/3.0.x` 的本地/远程 SHA 一致。
- Maven deploy、空缓存消费和 Actions 仍作为独立证据层，未执行时不声称完成。
