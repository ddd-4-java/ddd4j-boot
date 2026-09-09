# ddd4j-quarkus 对齐 ddd4j-boot 框架集成实施计划

> **状态：已被后续 P0–P5 规格体系取代，不再作为可执行待办。** 本文复选框保留最初方案的
> 历史状态，不代表当前 17 项仍未实施。当前完成事实以 ddd4j-quarkus 仓库的
> `docs/superpowers/specs/2026-08-05-quarkus-alignment-overview-design.md` 为准（P0–P3 已完成）；
> 当前未完成工作以 `docs/superpowers/specs/2026-09-08-quarkus-production-extension-convergence-design.md`
> 和 P5-B–E 后续计划为准，不得回到本计划重复创建模块。

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 保留 ddd4j-quarkus 现有 87 个 Java 文件作为基线，按对齐 ddd4j-boot 的要求补齐所有空 starter，并为每个模块新增 Testcontainers 集成测试。版本基线锁定 `quarkus-bom 3.36.3` + `ddd4j.version 2.0.x.20260630-SNAPSHOT`，消除版本漂移。

**Architecture:** Quarkus 采用编译时增强（BuildStep + Recorder）替代 Spring Boot 的运行时自动配置。所有 starter 通过 `META-INF/quarkus-extension.yaml` 注册，使用 `@ConfigMapping` 替代 `@ConfigurationProperties`，使用 CDI `@Produces` 替代 `@Bean`。

**Tech Stack:** Quarkus 3.36.3、Java 17、CDI（ArC）、JUnit 5、Testcontainers 1.20.4。

## Global Constraints

- 保留 ddd4j-quarkus 现有 87 个 Java 文件，不重写已有功能。
- 版本基线统一 `quarkus-bom 3.36.3`、`ddd4j.version 2.0.x.20260630-SNAPSHOT`。
- 复用主仓 `ddd4j-core` / `ddd4j-cache` / `ddd4j-mq-*` / `ddd4j-auth-*` / `ddd4j-data-*`，不复制实现。
- 转换模式：`@ConfigurationProperties` -> `@ConfigMapping`；`@Bean` -> `@Produces`；`InitializingBean` -> `@Observes StartupEvent`。
- 每个 Quarkus starter 至少 1 个 `@QuarkusTest` + 1 个 Testcontainers 集成测试。
- 测试统一 `@Tag("integration")`，Surefire 默认跳过，Failsafe 单独跑。

---

## 1. Phase P0：基线修复 + Cache 完善（Week 1）

- [ ] **Step 1: 版本基线修复**

  - 统一 `root pom.xml` 与 `ddd4j-quarkus-dependencies/pom.xml` 的 quarkus-bom 版本为 3.36.3
  - 删除 `ddd4j-quarkus-extension-qrcode` 中的 Hibernate/Agroal 显式覆盖

- [ ] **Step 2: ddd4j-quarkus-cache 完善**

  - 新增 `Ddd4jCacheBuildItemProducer.java`（BuildStep）
  - 新增 `CacheRecorder.java`（Recorder）
  - 新增 `quarkus-extension.yaml`
  - 新增 `Ddd4jCacheConfigTest.java`（@QuarkusTest + Caffeine）

- [ ] **Step 3: parent profile 补齐**

  - 移除 `legacy-resteasy` profile
  - 新增 `ddd4j-full` profile

## 2. Phase P1：Web / Data-JPA / Data-External（Week 2-3）

- [ ] **Step 1: 新增 ddd4j-quarkus-web**

  - 从 `ddd4j-web-quarkus` 复用 7 个文件（改包名）
  - 新增 `Ddd4jQuarkusWebHealthCheck.java`
  - 新增 `Ddd4jWebBuildItemProducer.java`
  - 新增 `HealthCheckTest.java`（@QuarkusTest）

- [ ] **Step 2: 补齐 ddd4j-quarkus-data-jpa**

  - 新增 `JpaAggregateRepository.java`、`JpaCdiProducer.java`、`JpaRepositoryBuildItem.java`
  - 新增 Testcontainers PostgreSQL 测试

- [ ] **Step 3: 补齐 ddd4j-quarkus-data-external**

  - 新增 `IpRegionQuarkusAdapter.java`、`WeatherQuarkusAdapter.java`、`QuarkusExternalCdiProducer.java`
  - 新增 Testcontainers Redis 测试

## 3. Phase P2：MQ Testcontainers + 13 Broker 测试（Week 4-5）

- [ ] **Step 1: 新增 ddd4j-quarkus-mq-testcontainers 共享 fixture**

  - 从 `ddd4j-javalin-testcontainers` 复用 13 个 fixture（改包名）
  - 新增 `JunitJupiterQuarkusTestContainers` 注解

- [ ] **Step 2: 13 Broker 集成测试**

  - 每个 broker 新增 `@QuarkusTest` + Testcontainers 测试
  - 断言 produce -> consume 端到端流转
  - 验证 manual ack / requeue / dead letter 三种场景

- [ ] **Step 3: mq-core 测试补齐**

  - `QuarkusMQListenerRegistrarTest` + `Ddd4jMQCdiProducerTest`

## 4. Phase P3：Auth 完善 + JWT Testcontainers（Week 6）

- [ ] **Step 1: 异常映射器补齐**

  - Sa-Token：抽离 `SaTokenExceptionMapper` 为独立类
  - Security：新增 `SecurityExceptionMapper`
  - Shiro/License：无需改动

- [ ] **Step 2: 新增 ddd4j-quarkus-auth-testcontainers**

  - `JwtTestContainerFixture`（Keycloak）
  - `RedisTestContainerFixture`（sa-token session）

- [ ] **Step 3: 各 auth 子模块集成测试**

  - satoken/security/shiro：登录 -> 访问受保护资源 -> token 失效
  - jwt：Keycloak 签发 -> SmallRye 验签 -> Subject 注入

## 5. Phase P4：Extensions 7 个补齐（Week 7-8）

- [ ] **Step 1: 删除 ddd4j-quarkus-extension-pf4j**

  - 改用 Quarkus Plugin 体系

- [ ] **Step 2: 新增 6 个 extension**

  - akka / cola / excel / jackson / monitor / qlexpress
  - 参考 ddd4j-boot 对应模块，改为 Quarkus CDI Producer + ConfigMapping

## 6. Phase P5：Samples 完整化 + CI/CD（Week 9-10）

- [ ] **Step 1: 完整化 14 个 sample**

  - 补 1 个 domain entity + 1 个 application service + 1 个 infrastructure repository + 1 个 adapter resource

- [ ] **Step 2: 各 sample 新增 @QuarkusTest 集成测试**

- [ ] **Step 3: CI/CD**

  - 新增 `.github/workflows/ci.yml`
  - 移除 Jenkinsfile 引用

---

## 7. 验收标准

1. `|ddd4j-quarkus 模块| >= |ddd4j-boot 模块| x 80%`
2. 每个 Quarkus starter 至少 1 个 `@QuarkusTest` + 1 个 Testcontainers 集成测试
3. 版本一致性：所有模块锁定 `quarkus-bom 3.36.3`
4. `mvn verify` 在 JDK 17 + 21 矩阵 100% 通过
5. 每个 starter 有 `README.md`

## 8. 风险与对策

| 风险 | 影响 | 对策 |
|------|------|------|
| Quarkus BuildStep 编译慢 | CI 耗时 | 拆分为 core + broker 两层 profile |
| Quarkus 无 servlet 容器 | shiro/satoken-web 受限 | 用 ContainerRequestFilter 替代 servlet filter |
| Hibernate 6.6 vs Quarkus 3.36 | 升级复杂 | 在 dependencies 统一升级 Hibernate 到 7.3 |
| Testcontainers Docker 镜像拉取慢 | CI 超时 | 用 withReuse(true) + 阿里云镜像加速 |

## 9. 总工作量

- 约 **80+ 新增/修改文件**
- 约 **3000+ 行新增 Java 代码**
- 约 **40+ @QuarkusTest 测试类**
- 约 **10 周（1 人全职）**
