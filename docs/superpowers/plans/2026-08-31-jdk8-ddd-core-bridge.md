# ddd4j-boot JDK 8 DDD Core Bridge Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 `2.3.x`–`2.6.x` 的 Java 8 / Spring Boot 2 维护线上，以 ddd4j 1.0.x 实现默认 DDD 基础设施、用户覆盖、缺类回退和 Repository 生命周期的逻辑等价桥接，同时保持既有 `io.ddd4j.boot.*` API。

**Architecture:** 每个维护线的 `ddd4j-boot-core` 增加一个 Boot 2 `spring.factories` 自动配置入口。核心桥接只创建默认 EventStore、Jackson 模块和 `MultiCommandExecutor`，Repository 桥接只维护应用上下文范围内的注册表；领域行为继续由 `ddd4j-ddd` 与 `ddd4j-core` 提供。历史 `ddd4j-boot-cmpt` starter 不修改默认行为。

**Tech Stack:** Java 8、Spring Boot 2.3–2.6、Spring Framework 5.2/5.3、ddd4j 1.0.x、fuin ddd-4-java / cqrs-4-java、JUnit 5、ApplicationContextRunner、Maven 3.9。

**Spec:** [JDK 8 DDD Core Bridge 设计](../specs/2026-08-31-jdk8-ddd-core-bridge-design.md)

## Global Constraints

- 串行处理 `2.3.x`、`2.4.x`、`2.5.x`、`2.6.x`，每次修改前验证 GitHub/Codeup SHA、`git status` 和 JDK 8。
- 不使用 Git worktree、不推送、不删除或重命名现有 `io.ddd4j.boot.*` 类型、模块或配置键。
- 只使用 `ddd4j 1.0.x.20260630-SNAPSHOT` 的 Java 8 构件；候选仓库不能解析时记录 `BLOCKED`，本地安装只作测试依赖。
- `ddd4j 1.0.x` 保持 `com.github.redacted-legacy-family:mybatis-plus-enhance` 单体 ABI，并使用 `2.7.x.20260630-SNAPSHOT`；禁止用模块化 `io.github.easy4j` 构件伪装旧坐标。
- Boot 2 自动配置使用 `META-INF/spring.factories`，禁止引入 Boot 3 `@AutoConfiguration` 或 `jakarta.*`。
- 默认 Bean 必须 `@ConditionalOnMissingBean`；桥接不得主动关闭用户提供的 EventStore。
- 每条线必须先产生失败的契约测试，再写最小实现；通过构建不等于行为契约通过。

---

### Task 0: 对齐并验证 ddd4j 1.0.x 的旧单体 ABI

**Repository:** `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j`, branch `feature/1.0.x`

**Files:**

- Modify: `ddd4j-dependencies/pom.xml`
- Create: `docs/superpowers/reports/2026-08-31-jdk8-abi-compatibility.md`

**Interfaces:**

- Produces local Java 8 artifacts `io.ddd4j:ddd4j-ddd:1.0.x.20260630-SNAPSHOT` and `io.ddd4j:ddd4j-core:1.0.x.20260630-SNAPSHOT`.
- Consumes `com.github.redacted-legacy-family:mybatis-plus-enhance:2.7.x.20260630-SNAPSHOT`, built from its Java 8 `2.7.x` source line.

- [ ] **Step 1: Write the failing dependency-resolution proof**【外部阻塞: 需 JDK 8 工具链与隔离本地仓库执行 mvn 依赖解析证明】

  With an isolated empty Maven local repository, run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-core -am -DskipTests compile
  ```

  Expected: FAIL while `ddd4j-dependencies` requests old `com.github.redacted-legacy-family:mybatis-plus-enhance:1.0.x.20260630-SNAPSHOT`.

- [x] **Step 2: Update only the dependency-management revision**（证据: ddd4j 仓 ddd4j-dependencies/pom.xml 已为 2.7.x.20260630-SNAPSHOT，commit 6edb64fa5）

  In `ddd4j-dependencies/pom.xml`, change `mybatis-plus-enhance.version` from `1.0.x.20260630-SNAPSHOT` to `2.7.x.20260630-SNAPSHOT`; retain groupId `com.github.redacted-legacy-family` and artifactId `mybatis-plus-enhance`.

- [ ] **Step 3: Build and install the real Java 8 upstream artifacts**【外部阻塞: 需 JDK 8 工具链执行 mvn install 构建上游制品】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -q -DskipTests install
  ```

  Expected: PASS with `ddd4j-core` and `ddd4j-ddd` installed under revision `1.0.x.20260630-SNAPSHOT`.

- [ ] **Step 4: Record ABI evidence and commit**【待办】

  Record source SHA, exact coordinates, JDK, command and result in the report. Commit:

  ```bash
  git add ddd4j-dependencies/pom.xml docs/superpowers/reports/2026-08-31-jdk8-abi-compatibility.md
  git commit -m "build: align ddd4j jdk8 enhance abi"
  ```

---

### Task 1: 在 `2.3.x` 建立 ddd4j 1.0.x 的依赖与测试夹具

**Files:**

- Modify: `ddd4j-boot-dependencies/pom.xml`
- Modify: `ddd4j-boot-core/pom.xml`
- Create: `ddd4j-boot-core/src/test/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreAutoConfigurationTest.java`
- Create: `ddd4j-boot-core/src/test/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryAutoConfigurationTest.java`

**Interfaces:**

- Consumes Task 0 installed `io.ddd4j:ddd4j-ddd:1.0.x.20260630-SNAPSHOT` and `io.ddd4j:ddd4j-core:1.0.x.20260630-SNAPSHOT`.
- Produces: Boot 2 contract tests for `Ddd4jJdk8CoreAutoConfiguration` and `Ddd4jJdk8RepositoryAutoConfiguration`.

- [ ] **Step 1: Add failing test dependencies and test classes**【待办】

  Add managed `ddd4j.version` and `ddd4j-ddd` / `ddd4j-core` dependencies. Add `spring-boot-test`, `junit-jupiter-api`, `junit-jupiter-engine` and `assertj-core` with `test` scope to `ddd4j-boot-core/pom.xml`. Create the tests using this runner shape:

  ```java
  private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(Ddd4jJdk8CoreAutoConfiguration.class));

  @Test
  void createsDefaultDddInfrastructure() {
      contextRunner.run(context -> {
          assertThat(context).hasSingleBean(EventStore.class);
          assertThat(context).hasSingleBean(Ddd4JacksonModule.class);
          assertThat(context).hasSingleBean(MultiCommandExecutor.class);
      });
  }
  ```

- [ ] **Step 2: Run tests to verify they fail before implementation**【外部阻塞: 需 JDK 8 工具链执行 mvn 测试】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-boot-core -am \
    -Dtest=Ddd4jJdk8CoreAutoConfigurationTest,Ddd4jJdk8RepositoryAutoConfigurationTest test
  ```

  Expected: compilation fails because the two auto-configuration types do not exist.

- [ ] **Step 3: Commit the test-first dependency baseline**【待办】

  ```bash
  git add ddd4j-boot-dependencies/pom.xml ddd4j-boot-core/pom.xml ddd4j-boot-core/src/test
  git commit -m "test: define jdk8 ddd bridge contracts"
  ```

### Task 2: 实现 Boot 2 DDD 核心自动配置

**Files:**

- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreProperties.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreAutoConfiguration.java`
- Create: `ddd4j-boot-core/src/main/resources/META-INF/spring.factories`
- Modify: `ddd4j-boot-core/src/test/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreAutoConfigurationTest.java`

**Interfaces:**

- Produces `Ddd4jJdk8CoreProperties#isEnabled()`, default `true`.
- Produces default beans `EventStore`, `Ddd4JacksonModule`, `MultiCommandExecutor` when `ddd4j.ddd.enabled=true`.

- [ ] **Step 1: Extend the failing test with all five auto-configuration dimensions**【待办】

  Add tests for `withPropertyValues("ddd4j.ddd.enabled=false")`, `withClassLoader(new FilteredClassLoader(AggregateRoot.class))`, custom EventStore/Module/CommandExecutor beans, and default EventStore lifecycle. Assert the disabled and missing-class contexts do not contain the default EventStore; assert custom beans are retained.

- [ ] **Step 2: Run the focused test to verify each new assertion fails**【外部阻塞: 需 JDK 8 工具链执行 mvn 聚焦测试】

  Run the Task 1 Maven command. Expected: disabled/missing-class/user-bean assertions fail because no implementation exists.

- [ ] **Step 3: Write the minimal Boot 2 implementation**【待办】

  Implement properties and configuration with this public shape:

  ```java
  @Configuration
  @ConditionalOnClass({AggregateRoot.class, EventStore.class, Command.class})
  @ConditionalOnProperty(prefix = "ddd4j.ddd", name = "enabled", havingValue = "true", matchIfMissing = true)
  @EnableConfigurationProperties(Ddd4jJdk8CoreProperties.class)
  public class Ddd4jJdk8CoreAutoConfiguration {
      @Bean(destroyMethod = "close")
      @ConditionalOnMissingBean(EventStore.class)
      public EventStore ddd4jEventStore() { ... }

      @Bean
      @ConditionalOnMissingBean(Ddd4JacksonModule.class)
      public Ddd4JacksonModule ddd4JacksonModule(ObjectProvider<EntityIdFactory> factory) { ... }

      @Bean
      @ConditionalOnMissingBean(MultiCommandExecutor.class)
      public MultiCommandExecutor dddCommandBus(ObjectProvider<List<CommandExecutor>> executors) { ... }
  }
  ```

  Register `io.ddd4j.boot.core.ddd.Ddd4jJdk8CoreAutoConfiguration` under `org.springframework.boot.autoconfigure.EnableAutoConfiguration` in `spring.factories`. Use Java 8 collections APIs, not `List.of`.

- [ ] **Step 4: Run focused tests and JDK 8 module regression**【外部阻塞: 需 JDK 8 工具链执行 mvn 聚焦测试与模块回归】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-boot-core -am \
    -Dtest=Ddd4jJdk8CoreAutoConfigurationTest test
  ```

  Expected: PASS; custom EventStore behavior proves the bridge did not take ownership of the user Bean.

- [ ] **Step 5: Commit the core bridge**【待办】

  ```bash
  git add ddd4j-boot-core/pom.xml ddd4j-boot-core/src/main ddd4j-boot-core/src/test
  git commit -m "feat: add jdk8 ddd core bridge"
  ```

### Task 3: 实现 JDK 8 Repository Registry 与关闭清理

**Files:**

- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryRegistry.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryRegistrar.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryAutoConfiguration.java`
- Modify: `ddd4j-boot-core/src/main/resources/META-INF/spring.factories`
- Modify: `ddd4j-boot-core/src/test/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryAutoConfigurationTest.java`

**Interfaces:**

- `Ddd4jJdk8RepositoryRegistry#register(String beanName, Object repository)` fails on duplicate bean names.
- `Ddd4jJdk8RepositoryRegistry#snapshot()` returns an unmodifiable `Map<String, Object>`.
- `Ddd4jJdk8RepositoryRegistry#clear()` removes every registration.

- [ ] **Step 1: Add failing repository discovery and cleanup tests**【待办】

  Define nested test beans annotated `@io.ddd4j.annotation.DomainRepository` and `@io.ddd4j.boot.core.annotation.DomainRepository`. Assert that either annotation produces one registry entry. Create and close two runner contexts, then assert the first registry snapshot is empty after close and the second has only its own repository.

- [ ] **Step 2: Run the repository test to verify failure**【外部阻塞: 需 JDK 8 工具链执行 mvn 仓库发现测试】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-boot-core -am \
    -Dtest=Ddd4jJdk8RepositoryAutoConfigurationTest test
  ```

  Expected: FAIL because the registry and registrar do not exist.

- [ ] **Step 3: Implement registry and registrar**【待办】

  Implement the registry with `ConcurrentHashMap<String, Object>`. Implement registrar as `SmartInitializingSingleton` plus `DisposableBean`: inspect all singleton bean names after initialization, register beans with either supported repository annotation or assignable to `BaseRepository`, and call `registry.clear()` in `destroy()`. Use `@ConditionalOnMissingBean` for the registry.

- [ ] **Step 4: Register and verify**【待办】

  Add `Ddd4jJdk8RepositoryAutoConfiguration` to `spring.factories`, then run the Task 3 focused test and:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-boot-core -am test
  ```

  Expected: PASS; no repository survives a closed context.

- [ ] **Step 5: Commit the registry bridge**【待办】

  ```bash
  git add ddd4j-boot-core/src/main ddd4j-boot-core/src/test ddd4j-boot-core/src/main/resources/META-INF/spring.factories
  git commit -m "feat: add jdk8 ddd repository registry"
  ```

### Task 4: 验证历史 starter 不回归并记录分支证据

**Files:**

- Create: `ddd4j-boot-cmpt/ddd4j-boot-cmpt-crypto/src/test/java/io/ddd4j/boot/cmpt/crypto/DefaultCryptoAutoConfigurationContractTest.java`
- Modify: `docs/superpowers/reports/ddd4j-boot-logical-consistency.md` on `3.4.x` after branch test evidence is recorded

**Interfaces:**

- Consumes the JDK 8 core bridge and existing `DefaultCryptoAutoConfiguration` registration.
- Produces one legacy-starter regression proof plus an evidence reference for `2.3.x`.

- [ ] **Step 1: Write a failing representative historical starter test**【待办】

  Use `ApplicationContextRunner` for `DefaultCryptoAutoConfiguration`. Assert a default crypto provider is created when its prerequisites are present and a user-provided provider is retained. The test must not depend on the new bridge to instantiate legacy APIs.

- [ ] **Step 2: Run test to verify existing behavior or expose a gap**【外部阻塞: 需 JDK 8 工具链执行 mvn 测试验证既有行为】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -pl ddd4j-boot-cmpt/ddd4j-boot-cmpt-crypto -am \
    -Dtest=DefaultCryptoAutoConfigurationContractTest test
  ```

  Expected: either PASS with documented legacy behavior or a failure that is fixed only within the crypto starter.

- [ ] **Step 3: Run full local JDK 8 verification**【外部阻塞: 需 JDK 8 工具链执行全量 mvn 本地验证】

  Run:

  ```bash
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -q -DskipTests install
  JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -q test
  ```

  Expected: both commands pass. Candidate repository resolution remains separately `BLOCKED` until an empty local cache can resolve exact SNAPSHOTs.

- [ ] **Step 4: Commit branch evidence**【待办】

  ```bash
  git add ddd4j-boot-cmpt/ddd4j-boot-cmpt-crypto/src/test
  git commit -m "test: verify jdk8 legacy starter contract"
  ```

### Task 5: 移植经验证的桥接行为到 `2.4.x`–`2.6.x`

**Files per branch:**

- Modify: `ddd4j-boot-dependencies/pom.xml`
- Modify: `ddd4j-boot-core/pom.xml`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreProperties.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8CoreAutoConfiguration.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryRegistry.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryRegistrar.java`
- Create: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/ddd/Ddd4jJdk8RepositoryAutoConfiguration.java`
- Create: `ddd4j-boot-core/src/main/resources/META-INF/spring.factories`
- Create: matching `src/test/java/io/ddd4j/boot/core/ddd/*Test.java`

- [ ] **Step 1: Verify each branch remote parity and JDK 8 build before change**【外部阻塞: 跨分支 JDK 8 构建验证，候选仓 SNAPSHOT 不可解析（见 2026-08-31-jdk8-2.3-build-blocked.md）】

  For `2.4.x`, then `2.5.x`, then `2.6.x`, check both remote SHAs, switch serially, and run `JAVA_HOME=$(/usr/libexec/java_home -v 1.8) mvn -q -DskipTests install`.

- [ ] **Step 2: Port tests before implementation**【待办】

  Copy no source files blindly. Recreate the Task 1/2/3 tests with that branch's actual Boot test dependency versions, run them to verify failure, then add the minimal bridge implementation with Java 8/Boot 2 APIs.

- [ ] **Step 3: Verify and commit each branch separately**【待办】

  Run the focused bridge tests plus `mvn -q test`. Commit with concrete subjects: `feat: add jdk8 ddd bridge to 2.4.x`, then `2.5.x`, then `2.6.x`.

### Task 6: 更新跨分支一致性证据与发布门禁

**Files:**

- Modify: `config/consistency/ddd4j-boot-contract-exceptions.tsv` on `3.4.x`
- Modify: `docs/superpowers/reports/ddd4j-boot-logical-consistency.md` on `3.4.x`
- Modify: `docs/superpowers/reports/ddd4j-boot-branch-baseline.json` on `3.4.x`

- [x] **Step 1: Re-run branch baseline and auto-configuration audit**（证据: docs/superpowers/reports/ddd4j-boot-branch-baseline.json 于 3.4.x commit 270de582 重跑审计，含 2.3.x-2.6.x jdk8 基线）

  Run the existing `scripts/consistency/audit_branch_baselines.py` and `audit_auto_configuration_contracts.py` against all `origin/*` refs after their local commits are deliberately synchronized or passed by explicit refs.

- [x] **Step 2: Record only executed evidence**（证据: config/consistency/ddd4j-boot-contract-exceptions.tsv 与 docs/superpowers/reports/ddd4j-boot-logical-consistency.md 于 3.4.x commit 5f7ee00d/2c793478 记录 BLOCKED/MISSING_EVIDENCE）

  Replace a branch/contract state with `PASS` only when it has its exact Maven command and result. Retain `BLOCKED` for candidate-repository artifact availability until clean-cache resolution is executed successfully.

- [ ] **Step 3: Run all consistency script tests and commit evidence**【存疑】

  Run every `scripts/consistency/test-*.sh`, regenerate reports, run `git diff --check`, and commit `docs: record jdk8 bridge evidence`.
