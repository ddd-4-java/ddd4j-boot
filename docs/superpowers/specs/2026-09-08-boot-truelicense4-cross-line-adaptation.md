# ddd4j-boot 与 ddd4j 跨版本适配规格

状态：已批准，实施中。`2.7.x` 基线代码与目标测试已闭合；完整 reactor 在第 56/72 模块因三个外部 Easy4J Starter 未发布而停止，详见 `docs/superpowers/reports/2026-09-09-boot-2.7-adaptation-baseline.md`。

## 目标

在不抹平各 Spring Boot 维护线差异的前提下，使 13 条 `ddd4j-boot` 分支分别消费对应的 `ddd4j` 主版本，并完成 TrueLicense 4、依赖边界、自动配置、构建模型和集成测试契约的适配。

## 架构边界

- `ddd4j` 是框架无关的领域、CQRS、事件存储、投影和可靠消息内核。
- `ddd4j-boot` 只承担 Spring Boot 组合根职责：属性绑定、条件装配、SPI 桥接、资源生命周期和 Starter 依赖组织。
- TrueLicense 的文件格式、默认签名算法及校验实现属于 `ddd4j-auth-license`；Boot 层只绑定配置并创建、安装、卸载 `LicenseVerify`。
- Boot 层不得重新声明已由对应 `ddd4j-dependencies` 管理的旧 TrueLicense 1.x 构件。
- 全局静态注册表或资源必须具有对称的注册/注销或初始化/销毁路径，测试上下文关闭后不得泄漏状态。

## 分支矩阵

| Boot 分支 | Spring Boot | 制品版本 | ddd4j 分支 | JDK | Maven 模型 |
|---|---|---|---|---:|---|
| `2.3.x` | `2.3.12.RELEASE` | `2.3.x.20260630-SNAPSHOT` | `feature/1.0.x` | 8 | 4.0 / `<modules>` |
| `2.4.x` | `2.4.13` | `2.4.x.20260630-SNAPSHOT` | `feature/1.0.x` | 8 | 4.0 / `<modules>` |
| `2.5.x` | `2.5.15` | `2.5.x.20260630-SNAPSHOT` | `feature/1.0.x` | 8 | 4.0 / `<modules>` |
| `2.6.x` | `2.6.15` | `2.6.x.20260630-SNAPSHOT` | `feature/1.0.x` | 8 | 4.0 / `<modules>` |
| `2.7.x` | `2.7.18` | `2.7.x.20260630-SNAPSHOT` | `feature/1.0.x` | 8 | 4.0 / `<modules>` |
| `3.0.x` | `3.0.13` | `3.0.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `3.1.x` | `3.1.12` | `3.1.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `3.2.x` | `3.2.12` | `3.2.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `3.3.x` | `3.3.13` | `3.3.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `3.4.x` | `3.4.13` | `3.4.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `3.5.x` | `3.5.16` | `3.5.x.20260630-SNAPSHOT` | `feature/2.0.x` | 17 | 4.0 / `<modules>` |
| `4.0.x` | `4.0.8` | `4.0.x.20260630-SNAPSHOT` | `feature/3.0.x` | 21 | 4.1 / `<subprojects>` |
| `4.1.x` | `4.1.0` | `4.1.x.20260630-SNAPSHOT` | `feature/3.0.x` | 21 | 4.1 / `<subprojects>` |

## TrueLicense 4 可观察行为

1. 对应 `ddd4j-dependencies` 必须管理 `global.namespace.truelicense:truelicense-v1:4.1.4`，且不得管理 `de.schlichtherle.truelicense:truelicense-core` 或 `truelicense-xml`。
2. `ddd4j-boot-auth-license` 只依赖 `io.ddd4j:ddd4j-auth-license`，不直接补回旧 TrueLicense 坐标。
3. `license.signature-algorithm` 必须传入六参数 `LicenseVerify` 构造器；未显式配置时沿用 ddd4j 的兼容默认值。
4. 缺失许可证文件可以导致安装失败日志，但不得破坏 Spring 应用上下文创建和条件退让行为。
5. `ddd4j-boot-auth-license` 必须加入对应聚合器，保证常规 reactor 能发现和验证该模块。

## Boot 大版本差异

- Boot 2.x 使用 Spring 5.3 可用 API；样例 HTTP 客户端使用 `RestTemplate`，不得引入 Spring 6.1 `RestClient`。
- Boot 2.x 使用兼容的 MyBatis-Plus Boot 2 starter；Boot 3.x/4.x 保留对应 Jakarta/Boot 3+ starter，不机械复制 2.7 坐标。
- Boot 2.x/3.x 保持 Maven POM Model 4.0.0 和 `<modules>`。
- 只要依赖 ddd4j 3.0.x，根聚合 POM 必须使用 Maven POM Model 4.1.0 和 `<subprojects>`。

## Testcontainers 契约

- 优先采用 Testcontainers 官方 Java 模块；无官方 Java 模块的中间件允许使用 `GenericContainer`，但镜像名、端口、等待策略和清理必须显式。
- 当前消息中间件最低覆盖 ActiveMQ/Artemis、Kafka、MQTT、NATS、Pulsar、RabbitMQ、Redis、RocketMQ 和兼容 SQS 的 LocalStack。
- 测试必须验证真实发布/消费往返或等价业务行为，容器能启动或端口可连接不能单独作为通过依据。
- 镜像必须固定版本，禁止 `latest`；测试失败时必须区分 Docker/拉取环境故障和业务行为失败。

## CI 与发布边界

- Verify workflow 必须支持 `workflow_dispatch`，用于逐线人工重跑。
- Deploy 使用组织级 `MAVEN_SETTINGS_XML`；缺失时应明确失败，不在仓库提交凭据。
- GitHub Free Actions 因 Billing 阻止执行时，状态记为“CI 未验证”，不能记为代码失败或发布成功。
- 本地 Maven 发布是独立门禁；上传成功、空缓存消费成功和 Actions 通过分别记录。

## 验收标准

- 13 条分支的 Spring Boot、制品版本、ddd4j 版本、JDK、Maven 模型全部通过矩阵脚本。
- 每条分支的 TrueLicense 依赖树只解析到 4.1.4 新坐标，自动配置测试验证签名算法透传。
- 各线使用自己的 JDK/Maven 完成目标模块测试，并按风险执行受影响 reactor 验证。
- 4.x 的聚合模型只出现 `<subprojects>`，2.x/3.x 只出现 `<modules>`。
- Testcontainers 源码审计无 `latest`，生命周期和往返断言完整；能运行 Docker 时执行集成测试。
- 每条线分别记录本地 SHA、两个远端跟踪状态、测试结果、发布和 CI 状态。

## 非目标

- 不改变 ddd4j 领域内核、许可证文件格式或默认算法。
- 不把各维护线重构为同一份源码，不升级用户指定的 Spring Boot 版本。
- 不在仓库保存 Maven 凭据，不提高 GitHub Actions 付费额度。
- 不使用 Git worktree，不重写已推送历史。
