# ddd4j-javalin 能力对齐 ddd4j-boot 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** ddd4j-javalin (feature/6.3.x) 对齐 ddd4j-boot (feature/3.4.x) 的能力矩阵，完成与 ddd4j (feature/2.0.x) 的全面适配，集成 Testcontainers 完善集成测试。保留 Guice + Javalin 编程式路由范式，不引入 Spring。

**Architecture:** ddd4j-javalin 采用 Guice 依赖注入 + Javalin 编程式路由 + SubjectKit 静态门面。对齐目标是复制 ddd4j-boot 的能力矩阵，而非平移 Spring 代码。每个模块实现为 Guice `AbstractModule`。

**Tech Stack:** Java 17、Javalin 7、Guice 7、JUnit 5、Testcontainers 1.20.6。

## Global Constraints

- 维持 Guice + Javalin 编程式路由范式，不引入 Spring。
- 保留 `SubjectKit` 静态门面。
- 不破坏现有 22 个 Test 类。
- 复用主仓 `ddd4j-core` / `ddd4j-mq-*` / `ddd4j-auth-*` / `ddd4j-data-*`，不复制实现。
- Testcontainers 版本锁定 1.20.6。

---

## 1. 现状分析

| 维度 | ddd4j | ddd4j-boot | ddd4j-javalin（当前） |
|------|-------|------------|---------------------|
| 模块数 | 100+ | 38 个子模块 | 35 个子模块 |
| 测试模式 | 1427 Test | 8 Test | 22 Test |
| Testcontainers | 无 | 完全无 | 完全无 |
| DI 容器 | 6 大 Runtime | Spring Boot | Guice |
| Web 入口 | 6 大 Web Adapter | AutoConfiguration | 手工 Javalin.create() |

## 2. Step 1：基础设施层（预计 2 天）

- [ ] **Task 1: 注册 testcontainers BOM**

  - 在 `ddd4j-javalin-dependencies/pom.xml` 注册 testcontainers BOM 1.20.6

- [ ] **Task 2: 新建 ddd4j-javalin-testcontainers 共享模块**

  - 11 个 fixture：MySQL、Postgres、MariaDB、MongoDB、Redis、Kafka、RabbitMQ、ActiveMQ、Keycloak、WireMock、MQTT
  - `Ddd4jTestContainersExtension.java`（JUnit 5 Extension）
  - `JunitJupiterTestContainers.java` 注解

- [ ] **Task 3: 新建 ddd4j-javalin-web 核心模块**

  - `Ddd4jJavalinProperties.java`（配置 POJO）
  - `Ddd4jJavalinAutoConfiguration.java`（Guice Module）
  - `Ddd4jJavalinApplication.java`（一键启动入口）
  - `JavalinTestFixture.java`（测试基类）
  - `Ddd4jJavalinAutoConfigurationTest.java`

## 3. Step 2：数据层（预计 1.5 天）

- [ ] **Task 4: 删除 3 个 @Deprecated Module**

  - cache-javalin、data-mybatisplus-javalin、data-crypto-javalin

- [ ] **Task 5: 补 4 个数据空壳**

  - data-logs：`Ddd4jApiLogJavalinModule`
  - data-external：`Ddd4jExternalJavalinModule`
  - data-datascope：`Ddd4jDataScopeJavalinModule`
  - data-jpa：`Ddd4jJpaJavalinModule` + Testcontainers PostgreSQL 测试

- [ ] **Task 6: data-mybatisplus 加集成测试**

  - `Ddd4jMybatisJavalinMySqlIT.java`（Testcontainers MySQL + 完整 CRUD）

## 4. Step 3：鉴权层（预计 1 天）

- [ ] **Task 7: 补 auth-license 空壳**

  - `Ddd4jLicenseJavalinModule`

- [ ] **Task 8: 3 个 auth 加 Keycloak 集成测试**

  - satoken/security/shiro 各加 `*KeycloakIT.java`

## 5. Step 4：MQ 层（预计 2 天）

- [ ] **Task 9: 4 个核心 broker 集成测试**

  - kafka / rabbitmq / redis-stream / activemq 各加 `*IT.java`

- [ ] **Task 10: 其余 9 个 broker 加骨架**

  - 只放 Container 启动与配置注入

## 6. Step 5：扩展层（预计 0.5 天）

- [ ] **Task 11: 补 extension-qlexpress 空壳**

  - `Ddd4jQLExpressJavalinModule`

## 7. Step 6：Sample 升级与新增（预计 1.5 天）

- [ ] **Task 12: 5 个现有 sample 升级**

  - 使用 `Ddd4jJavalinApplication` + `JavalinTestFixture`

- [ ] **Task 13: 新增 5 个 sample**

  - jdbc / mybatis-testcontainers / cqrs-person-kafka / qlexpress / keycloak

## 8. Step 7：文档与收尾（预计 0.5 天）

- [ ] **Task 14: 顶层 README.md + docs/ 文档**

- [ ] **Task 15: 跑全量集成测试**

  - `mvn verify -Pjavalin-integration-tests`

---

## 9. 验收标准

1. `mvn -pl ddd4j-javalin -am clean install` 成功
2. `mvn -pl ddd4j-javalin -am test` 全部绿色（至少 30 个 Test 类）
3. `mvn -pl ddd4j-javalin -am verify -Pjavalin-integration-tests` 通过（预期 60+ IT）
4. 模块对齐率 >= 80%
5. 维持 Guice + Javalin 范式，不引入 Spring

## 10. 不在范围

1. 响应式（WebFlux 等价）
2. 7 个 extension-* 完整实现（保留空 pom）
3. OWASP / Jacoco 质量门禁
4. CI/GitHub Actions 配置
5. Sample 数量补齐到 22 个
