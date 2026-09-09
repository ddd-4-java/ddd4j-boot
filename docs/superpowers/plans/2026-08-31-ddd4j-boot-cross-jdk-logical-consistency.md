# ddd4j-boot 跨 JDK 维护线逻辑一致性 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让 ddd4j-boot 的 13 条维护线在各自支持的 Spring Boot/JDK 平台上满足同一组可观察能力契约；组内保持公开 API 与配置兼容，跨组以可验证的迁移映射保持逻辑等价。

**Architecture:** 在业务 starter 之外建立独立、机器可读的能力契约层。审计器从每个 Git 分支读取 POM、模块、自动配置注册和测试证据；比较器将实测结果和例外映射为每条分支的逻辑一致性报告。业务适配仍留在各分支的现有模块中，按 JDK 8、17、21 的 Spring/Jackson/Jakarta 边界分别实现，不复制源文件。

**Tech Stack:** Maven、Java 8/17/21、Spring Boot 2.3–2.7/3.x/4.x、JUnit 4 或 JUnit 5、Spring Boot test context runners（或 JDK 8 等价夹具）、Python 3 标准库、Bash、Testcontainers（在该分支和 JDK 可用时）。

**Spec:** [跨 JDK 逻辑一致性设计](../specs/2026-08-31-cross-jdk-logical-consistency-design.md)

## Global Constraints

- 维护线分组是唯一输入：JDK 8 为 `2.3.x`–`2.7.x`；JDK 17 为 `3.0.x`–`3.5.x`；JDK 21 为 `4.0.x`、`4.1.x`。
- 每次变更某条维护线前，先记录 `git status --short --branch`、GitHub/Codeup 两端 SHA、POM revision、JDK 和 Boot 版本。只有双方 SHA 与本次基线记录一致时才能开始该线工作。
- 本计划只接受行为、公开 API、公开配置和资源生命周期一致；不以文件文本、模块目录数或自动配置资源文件名相同作为通过条件。
- `REQUIRED`、`ADAPTED`、`NOT_APPLICABLE`、`BLOCKED` 是仅有的能力状态；后两者必须有可追溯原因，`NOT_APPLICABLE` 还必须有迁移说明。
- 同一 JDK 组要求公开 API/配置兼容；跨 JDK 组只要求逻辑等价和显式迁移说明。不得用跨组 japicmp 失败判定逻辑失败。
- 先写会失败的审计或契约测试，再补最小适配；禁止把 `@Disabled`、skipped 或 context 能启动作为通过证据。
- 不使用 Git worktree、不强推、不重写远端历史。执行分支阶段时采用串行 `git switch <branch>`；每次切换和提交前保留并检查用户已有修改。
- 未经额外确认，不修复 `2.3.x` revision 双 `.x`、不更改候选仓库发布策略、也不修改 `ddd4j`、`ddd4j-cloud` 或 `ddd4j-weixin`。

---

## Phase 1：建立可执行的逻辑事实源

- [x] **Task 1: 定义分支、能力和例外的机器可读契约**（完成：`9f1d02fc`）

  **Files:**
  - Create: `config/consistency/ddd4j-boot-branch-groups.tsv`
  - Create: `config/consistency/ddd4j-boot-logical-contracts.tsv`
  - Create: `config/consistency/ddd4j-boot-contract-exceptions.tsv`
  - Create: `scripts/consistency/test-contract-data.sh`
  - Create: `scripts/consistency/verify_contract_data.py`

  **Step 1: Write the failing data-validation test.**

  In `scripts/consistency/test-contract-data.sh`, construct temporary malformed TSV inputs: a missing maintenance branch, an unsupported status, a `NOT_APPLICABLE` row without a migration document, and a `REQUIRED` contract without an owner. Invoke `verify_contract_data.py` for each and assert non-zero exit plus a precise diagnostic.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-contract-data.sh`

  Expected: failure because the validator and TSV files do not yet exist.

  **Step 3: Implement the smallest validator and data files.**

  Define these exact headers:

  ```text
  branch\tjdk_group\tboot_line\tddd4j_line\tregistration_mode
  contract_id\tcapability\tobservable_behavior\trequired_state\towner
  branch\tcontract_id\tstatus\treason\tmigration_document\tevidence_ref
  ```

  Populate all 13 branches and the seven capability groups in the approved design: Core/SPI/Repository/CQRS, Auto-configuration, WebMVC/WebFlux, Auth, Data/Cache, MQ, and Configuration/BOM. Require every contract to declare a named owner module. Implement only Python standard-library parsing, reject duplicate keys, verify every branch has every required contract or an explicit exception, and require `docs/migration/...` for `NOT_APPLICABLE`.

  **Step 4: Run test to verify it passes.**

  Run: `bash scripts/consistency/test-contract-data.sh`

  Expected: PASS with 13 branches and no unclassified contract rows.

  **Step 5: Commit.**

  ```bash
  git add config/consistency scripts/consistency
  git commit -m "test: define boot logical consistency contracts"
  ```

- [x] **Task 2: 形成可重复的分支基线审计**（完成：`270de582`）

  **Files:**
  - Create: `scripts/consistency/audit_branch_baselines.py`
  - Create: `scripts/consistency/test-branch-baselines.sh`
  - Create: `scripts/consistency/fixtures/baseline-jdk8-pom.xml`
  - Create: `scripts/consistency/fixtures/baseline-jdk17-pom.xml`
  - Create: `scripts/consistency/fixtures/baseline-jdk21-pom.xml`
  - Create: `docs/superpowers/reports/ddd4j-boot-branch-baseline.json`

  **Step 1: Write failing fixture tests.**

  Make `test-branch-baselines.sh` feed each fixture to the audit script and assert extraction of revision, Java level, parent Boot version, root modules, `spring.factories` count and `AutoConfiguration.imports` count. Add an invalid POM fixture generated in the test and assert a non-zero exit.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-branch-baselines.sh`

  Expected: failure because the branch audit cannot yet read POM/module metadata.

  **Step 3: Implement baseline scan and initial report.**

  Implement `audit_branch_baselines.py --repo <path> --branches <tsv> --output <json>`. Use `git show <branch>:pom.xml` and `git ls-tree -r <branch>` rather than switching branches. Emit one JSON record per branch with: commit SHA, revision, Java, Spring Boot parent/BOM coordinates, root module list, `spring.factories` files, `AutoConfiguration.imports` files and their entry counts. Parse Maven XML namespace-safely and fail on missing POM or ambiguous Java value.

  Generate the initial `docs/superpowers/reports/ddd4j-boot-branch-baseline.json` from current local refs. This report is evidence, not a hand-edited source of truth.

  **Step 4: Run tests and real audit.**

  Run:

  ```bash
  bash scripts/consistency/test-branch-baselines.sh
  python3 scripts/consistency/audit_branch_baselines.py \
    --repo . \
    --branches config/consistency/ddd4j-boot-branch-groups.tsv \
    --output docs/superpowers/reports/ddd4j-boot-branch-baseline.json
  ```

  Expected: tests pass and report contains exactly 13 records. Any missing module list is reported as an audit finding, not converted to an empty success.

  **Step 5: Commit.**

  ```bash
  git add scripts/consistency docs/superpowers/reports
  git commit -m "test: audit boot branch baselines"
  ```

- [x] **Task 3: 审计自动配置入口与五维行为证据**（完成：`f8ae8a6b`）

  **Files:**
  - Create: `scripts/consistency/audit_auto_configuration_contracts.py`
  - Create: `scripts/consistency/test-auto-configuration-contracts.sh`
  - Create: `docs/superpowers/reports/ddd4j-boot-auto-configuration-inventory.json`
  - Modify: `config/consistency/ddd4j-boot-contract-exceptions.tsv`

  **Step 1: Write failing registration-format tests.**

  In `test-auto-configuration-contracts.sh`, create synthetic tree fixtures for an imports-only Boot 3 starter, a `spring.factories` Boot 2 starter, both registries with duplicate class entries, and an absent entry. Assert the scanner classifies them as `AUTO_CONFIGURATION`, `LEGACY_FACTORY`, `DUPLICATE_REGISTRATION`, and `MISSING` respectively.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-auto-configuration-contracts.sh`

  Expected: failure because registration classification does not exist.

  **Step 3: Implement scanner and record only verified exceptions.**

  Scan each branch through `git show` and classify registrations without assuming one registration form per JDK group. Link each discovered auto-configuration to the contract owner declared in `ddd4j-boot-logical-contracts.tsv`; unmatched registrations must be listed as `UNMAPPED`, which fails the audit. Add an exception only after recording the exact platform cause and migration document.

  **Step 4: Run test and real inventory.**

  Run:

  ```bash
  bash scripts/consistency/test-auto-configuration-contracts.sh
  python3 scripts/consistency/audit_auto_configuration_contracts.py \
    --repo . \
    --branches config/consistency/ddd4j-boot-branch-groups.tsv \
    --contracts config/consistency/ddd4j-boot-logical-contracts.tsv \
    --output docs/superpowers/reports/ddd4j-boot-auto-configuration-inventory.json
  ```

  Expected: fixture tests pass; real unmatched or duplicate results remain visible failures until resolved.

  **Step 5: Commit.**

  ```bash
  git add scripts/consistency config/consistency docs/superpowers/reports
  git commit -m "test: inventory boot auto configuration contracts"
  ```

## Phase 2：建立比较门禁与迁移基线

- [x] **Task 4: 渲染分支契约状态并阻断无证据通过**（完成：`fc32b2b5`）

  **Files:**
  - Create: `scripts/consistency/render_branch_consistency.py`
  - Create: `scripts/consistency/test-render-branch-consistency.sh`
  - Create: `docs/superpowers/reports/ddd4j-boot-logical-consistency.md`

  **Step 1: Write failing status-renderer tests.**

  Test a minimal valid branch/contract/report set, then assert failure for a required contract with no evidence, a blocked contract with no reason, and an inapplicable contract with no migration. Assert the Markdown result contains branch SHA, JDK, Boot line, contract id, status and evidence reference.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-render-branch-consistency.sh`

  Expected: failure because no status renderer exists.

  **Step 3: Implement evidence renderer.**

  Consume Phase 1 TSV and JSON outputs. Generate the report grouped by JDK group and by capability. Treat `BLOCKED` and `NOT_APPLICABLE` as non-pass states in the release summary, while retaining their reason. A `PASS` is valid only when an evidence reference points to a test command/result or a reviewed generated report.

  **Step 4: Run test and render initial report.**

  Run:

  ```bash
  bash scripts/consistency/test-render-branch-consistency.sh
  python3 scripts/consistency/render_branch_consistency.py \
    --branches config/consistency/ddd4j-boot-branch-groups.tsv \
    --contracts config/consistency/ddd4j-boot-logical-contracts.tsv \
    --exceptions config/consistency/ddd4j-boot-contract-exceptions.tsv \
    --baseline docs/superpowers/reports/ddd4j-boot-branch-baseline.json \
    --auto-config docs/superpowers/reports/ddd4j-boot-auto-configuration-inventory.json \
    --output docs/superpowers/reports/ddd4j-boot-logical-consistency.md
  ```

  Expected: report makes all missing evidence explicit; no implicit pass.

  **Step 5: Commit.**

  ```bash
  git add scripts/consistency docs/superpowers/reports
  git commit -m "test: gate boot branch consistency evidence"
  ```

- [x] **Task 5: 建立跨组配置与 API 迁移映射**（完成：`0a8a0f80`）

  **Files:**
  - Create: `config/consistency/ddd4j-boot-migration-map.tsv`
  - Create: `scripts/consistency/compare_branch_contracts.py`
  - Create: `scripts/consistency/test-compare-branch-contracts.sh`
  - Create: `docs/migration/jdk8-to-jdk17.md`
  - Create: `docs/migration/jdk17-to-jdk21.md`

  **Step 1: Write failing migration-map tests.**

  Verify comparison rejects a cross-group mapping without source/target configuration, user-visible behavior and executable validation command; verify it rejects a same-JDK-group public configuration drift not listed as deprecated; verify accepted `javax` to `jakarta` mapping stays an `ADAPTED`, not `MISSING`, result.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-compare-branch-contracts.sh`

  Expected: failure because mapping/compare code is absent.

  **Step 3: Implement migration documents and comparator.**

  Use the header `from_group\tto_group\tcontract_id\tsource_surface\ttarget_surface\tbehavior_delta\tvalidation_command`. Document `javax`/`jakarta`, Boot 2 factory/import registration, Jackson compatibility, and Java language/runtime prerequisites. The comparator must distinguish same-group public configuration/API drift from cross-group migrations and output a deterministic TSV/JSON result.

  **Step 4: Run test.**

  Run: `bash scripts/consistency/test-compare-branch-contracts.sh`

  Expected: pass; accepted migrations and rejected undocumented differences are both listed.

  **Step 5: Commit.**

  ```bash
  git add config/consistency scripts/consistency docs/migration
  git commit -m "docs: map boot cross-jdk migrations"
  ```

## Phase 3：收敛 JDK 8 历史兼容组

- [ ] **Task 6: 为 2.3.x–2.7.x 建立等价自动配置与生命周期测试**

  **Files (each branch; verify actual packages before editing):**
  - Modify: `pom.xml`
  - Modify: `ddd4j-boot-bom/pom.xml`
  - Modify: `ddd4j-boot-dependencies/pom.xml`
  - Modify: `ddd4j-boot-core/pom.xml`
  - Modify: `ddd4j-boot-cmpt/pom.xml`
  - Modify: `ddd4j-boot-core/src/main/resources/META-INF/spring.factories`
  - Create or Modify: `ddd4j-boot-core/src/test/java/**/Ddd4jCoreAutoConfigurationTest.java`
  - Create or Modify: `ddd4j-boot-core/src/test/java/**/Ddd4jRepositoryAutoConfigurationTest.java`

  **Step 1: Capture each branch baseline before source edits.**

  For each of `2.3.x`, `2.4.x`, `2.5.x`, `2.6.x`, `2.7.x`, verify remote SHA parity, switch serially, run `mvn -q -DskipTests install`, and record the precise Java/Boot/revision into the generated report. If a dependency cannot resolve, create a `BLOCKED` entry with the Maven coordinate and failure output reference; do not substitute a newer JDK.

  **Step 2: Write failing JDK 8-equivalent contract tests.**

  Use the runner available to the actual Boot version, or a minimal `AnnotationConfigApplicationContext` fixture if a runner is not available. Cover default assembly, enabled=false, critical-class absence, user bean override, and context-close/recreate cleanup for SPI and repositories. Preserve Java 8 syntax and `javax` APIs.

  **Step 3: Run focused tests to verify failure.**

  Run the exact affected module command, for example:

  ```bash
  mvn -pl ddd4j-boot-core -am -Dtest=Ddd4jCoreAutoConfigurationTest,Ddd4jRepositoryAutoConfigurationTest test
  ```

  Expected: one or more contract assertions fail before the minimal implementation adjustment.

  **Step 4: Implement only branch-native adapters.**

  Update Spring Boot 2 registration in `spring.factories`, conditional configuration, default-bean backoff and shutdown callbacks needed by the failing test. Keep existing legacy module layout; do not import Jakarta, Java 17 APIs or Boot 3 `@AutoConfiguration` solely to make code resemble newer lines.

  **Step 5: Verify focused and group regression tests.**

  Run focused tests, module tests, and the Phase 1 scanner. Mark each branch/contract `PASS` only with successful test output. Record unavailable external integration dependencies as `BLOCKED`.

  **Step 6: Commit each branch independently.**

  For each branch, stage the enumerated files and use a branch-specific commit, for example `fix: align 2.3.x boot 2 logical contracts`.

  Do not push until all four branch reports pass review and remote leases are rechecked.

## Phase 4：收敛 JDK 17 主能力组

- [ ] **Task 7: 对 3.0.x–3.5.x 执行主能力契约回归与最小修复**

  **Files (each branch; 3.4.x is the source-location reference):**
  - Modify: `pom.xml`, `ddd4j-boot-bom/pom.xml`, `ddd4j-boot-dependencies/pom.xml`
  - Modify: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/Ddd4jCoreAutoConfiguration.java`
  - Modify: `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/Ddd4jRepositoryAutoConfiguration.java`
  - Modify: `ddd4j-boot-web/ddd4j-boot-web-webmvc/src/main/java/io/ddd4j/boot/web/webmvc/Ddd4jWebMvcAutoConfiguration.java`
  - Modify: `ddd4j-boot-web/ddd4j-boot-web-webflux/src/main/java/io/ddd4j/boot/web/webflux/Ddd4jWebFluxAutoConfiguration.java`
  - Modify: `ddd4j-boot-auth/**/src/main/java/**/**AutoConfiguration.java`
  - Modify: `ddd4j-boot-data/**/src/main/java/**/**AutoConfiguration.java`
  - Modify: `ddd4j-boot-cache/**/src/main/java/**/**AutoConfiguration.java`
  - Modify: `ddd4j-boot-mq/**/src/main/java/**/**AutoConfiguration.java`
  - Modify: matching `src/test/java/**/*AutoConfigurationTest.java` and `META-INF/spring/...AutoConfiguration.imports` or `META-INF/spring.factories`

  **Step 1: Derive a branch-specific failure list.**

  Process `3.0.x` through `3.5.x`, never assuming the registration form from the version label. Run Phase 1 scanners and focused existing tests. Check each POM resolves to its declared ddd4j/Spring versions before comparing behavior.

  **Step 2: Add failing tests for only absent behavior.**

  Use `ApplicationContextRunner`, `WebApplicationContextRunner`, and `ReactiveWebApplicationContextRunner` where provided by that branch. For every discovered contract gap, first assert default creation, disable condition, missing-class fallback, user-bean override, and close/recreate cleanup as applicable. MQ tests must additionally assert broker switch, publisher/consumer/ack boundary and client shutdown ownership.

  **Step 3: Run focused tests to verify failure.**

  Examples:

  ```bash
  mvn -pl ddd4j-boot-core -am -Dtest=Ddd4jCoreAutoConfigurationTest,Ddd4jRepositoryAutoConfigurationTest test
  mvn -pl ddd4j-boot-web/ddd4j-boot-web-webmvc -am -Dtest=Ddd4jWebMvcAutoConfigurationTest test
  ```

  Expected: a missing contract yields an assertion failure, not a skipped test.

  **Step 4: Make the minimal per-branch correction.**

  Preserve Boot 2.7 `javax` vs Boot 3 `jakarta` boundary, the Jackson version defined by the effective BOM, and actual registration form. Use `@ConditionalOnMissingBean` for default beans and existing lifecycle scope abstractions for cleanup. Do not copy entire 3.4 modules into other lines.

  **Step 5: Execute group validation.**

  Run all affected module tests plus `mvn -q -DskipTests install`. Run generated branch audit and comparison, then record evidence per capability.

  **Step 6: Commit separately per maintenance line.**

  For each branch, stage only its changed root POM/module paths and use a concrete subject, for example `fix: align 3.1.x logical starter contracts`.

## Phase 5：收敛 JDK 21 / Boot 4 组

- [ ] **Task 8: 审计并适配 4.0.x 与 4.1.x 的 Boot 4 能力**

  **Files (resolve actual module paths from Phase 1 before edits):**
  - Modify: `pom.xml`
  - Modify: `ddd4j-boot-bom/pom.xml` when present
  - Modify: `ddd4j-boot-dependencies/pom.xml` when present
  - Modify: `**/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  - Create or Modify: `**/src/test/java/**/*AutoConfigurationTest.java`
  - Modify: `config/consistency/ddd4j-boot-contract-exceptions.tsv`

  **Step 1: Resolve real topology and effective dependency graph.**

  Before changing either branch, run `mvn help:effective-pom`, `mvn dependency:tree`, and the baseline scan. Specifically determine whether ddd4j 3.0.x is inherited correctly or must be declared explicitly. Treat absence of a root `<modules>` list as an audit fact; do not invent module paths.

  **Step 2: Write failing Boot 4 contract tests.**

  For every discovered starter, use its Boot 4 test infrastructure to prove the five auto-configuration dimensions and relevant serialization or Jakarta behavior. Include Java 21-only changes only when they preserve a declared external contract.

  **Step 3: Run focused tests to verify failure.**

  Run the resolved module test command from Step 1. Expected: each actual capability gap fails an assertion before source change.

  **Step 4: Implement Boot 4-native fixes.**

  Update imports registration, conditions, configuration binding, Jackson adapters and `jakarta` signatures only where tests show a gap. If an upstream platform removes a capability, record `NOT_APPLICABLE` with the exact migration document and consumer validation command instead of silently omitting it.

  **Step 5: Validate and commit line by line.**

  Run targeted tests, `mvn -q -DskipTests install`, baseline/inventory/comparator, then commit one branch at a time:

  Stage the actual paths resolved in Step 1 and commit separately, for example `fix: align 4.0.x boot 4 logical contracts`.

## Phase 6：外部依赖、API 与发布证据

- [x] **Task 9: 为 Data、Cache 与 MQ 补齐真实依赖的契约证据**（完成：Data/Cache/9 MQ，52 tests，0 failures/errors/skips）

  **Files:**
  - Create: `scripts/consistency/run_integration_contracts.sh`
  - Create: `scripts/consistency/test-run-integration-contracts.sh`
  - Create: `docs/superpowers/reports/ddd4j-boot-integration-contracts.json`
  - Modify: module-specific `src/test/java/**/*IT.java` only where the branch audit identifies a missing integration contract

  **Step 1: Write failing profile-selection tests.**

  Verify the runner refuses absent profile/JDK/branch metadata, refuses a skipped test as success, and emits a `BLOCKED` record when Docker or a compatible Testcontainers module is unavailable.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-run-integration-contracts.sh`

  Expected: failure until the integration runner implements result classification.

  **Step 3: Implement explicit real-dependency runner.**

  Run only existing supported profiles and branch-compatible Testcontainers modules. Capture Testcontainers image name, runtime/JDK, Maven coordinate, command, exit status and report link. Exercise database migration/connection, cache registration/eviction, and MQ publish/consume/ack/close semantics. A missing compatible image or Docker runtime produces `BLOCKED`, never `PASS`.

  **Step 4: Run unit test and a selected executable profile.**

  Run:

  ```bash
  bash scripts/consistency/test-run-integration-contracts.sh
  bash scripts/consistency/run_integration_contracts.sh --branch 3.4.x --profile integration
  ```

  Expected: runner unit tests pass; actual result is PASS only with executed tests, otherwise a diagnosable BLOCKED record.

  **Step 5: Commit.**

  ```bash
  git add scripts/consistency docs/superpowers/reports
  git add ddd4j-boot-data ddd4j-boot-cache ddd4j-boot-mq
  git commit -m "test: record boot integration contract evidence"
  ```

- [x] **Task 10: 建立同组 API/配置差异门禁和最终报告**（完成：10 个同组比较 PASS；发布状态保留 BLOCKED）

  **Files:**
  - Create: `scripts/consistency/verify_same_group_compatibility.sh`
  - Create: `scripts/consistency/test-verify-same-group-compatibility.sh`
  - Create: `docs/superpowers/reports/ddd4j-boot-cross-jdk-consistency-final.md`
  - Modify: `docs/superpowers/reports/ddd4j-boot-logical-consistency.md`
  - Modify: `docs/superpowers/README.md`

  **Step 1: Write failing group-diff tests.**

  Test an undocumented public configuration removal and an undocumented same-group API removal; assert the script fails. Test a documented deprecation with a migration mapping; assert it reports review-required rather than a pass. Test that cross-group `javax`/`jakarta` differences are reported through the migration map, not as same-group breakage.

  **Step 2: Run test to verify it fails.**

  Run: `bash scripts/consistency/test-verify-same-group-compatibility.sh`

  Expected: failure because no grouped API/config verifier exists.

  **Step 3: Implement verifier and final report.**

  Generate effective POM/configuration metadata/API surface evidence for each same-JDK group using branch-native tooling. Compare results to the contract and migration map. Produce a final report with per-branch SHA, JDK, Boot, ddd4j revision, commands, PASS/BLOCKED/NOT_APPLICABLE state, outstanding exceptions, and remote parity check. Link this plan and the approved design from `docs/superpowers/README.md`.

  **Step 4: Run full final gate.**

  Run:

  ```bash
  bash scripts/consistency/test-verify-same-group-compatibility.sh
  bash scripts/consistency/verify_same_group_compatibility.sh
  python3 scripts/consistency/render_branch_consistency.py --help
  git diff --check
  ```

  Expected: verifier passes only if no undocumented same-group drift remains. The final report lists any blocked external evidence as release blockers.

  **Step 5: Commit documentation and gates.**

  ```bash
  git add scripts/consistency docs/superpowers/reports docs/superpowers/README.md
  git commit -m "docs: report boot cross-jdk consistency"
  ```

## Phase 7：发布前复核与 ddd4j-cloud 输入

- [x] **Task 11: 复核双远端和向 ddd4j-cloud 提供版本组合输入**（完成：13 行输入完整；未通过完整 reactor 的行保持 BLOCKED）

  **Files:**
  - Create: `docs/superpowers/reports/ddd4j-boot-release-line-input.tsv`
  - Modify: `docs/superpowers/reports/ddd4j-boot-cross-jdk-consistency-final.md`

  **Step 1: Write a failing release-input completeness check.**

  Extend `test-contract-data.sh` or add a focused test that rejects a line lacking GitHub SHA, Codeup SHA, Java, Boot line, ddd4j line, logical state, or report reference.

  **Step 2: Run test to verify failure.**

  Run the selected test before generating the input. Expected: failure until every completed line has explicit evidence.

  **Step 3: Generate and review final input.**

  Fetch both remotes, compare exact remote heads with `git ls-remote`, then write one row per maintenance branch. Rows with unresolved evidence remain `BLOCKED`; do not promote them for cloud compatibility selection. This document is the handoff input for ddd4j-cloud planning, not a change to ddd4j-cloud itself.

  **Step 4: Run final validation.**

  Run all Phase 1/2 validators, the group verifier, targeted Maven tests for changed modules, and `git diff --check`. Record exact commands and output locations in the final report.

  **Step 5: Commit.**

  ```bash
  git add docs/superpowers/reports
  git commit -m "docs: publish boot release line evidence"
  ```

## Execution Order and Stop Conditions

1. Execute Tasks 1–5 on the currently authorized `3.4.x` checkout. They create the evidence system and do not mutate other maintenance lines.
2. Before Tasks 6–8, obtain confirmation to switch and modify the specific maintenance branches; execute lines serially and preserve uncommitted work.
3. Execute Tasks 9–10 after the relevant branch-native unit contracts pass. Docker/Testcontainers failures create evidence-backed `BLOCKED` records, not skipped successes.
4. Execute Task 11 only after all eligible lines have a reviewed final report. Cloud integration consumes the generated TSV as input and has its own change/verification cycle.

Stop and request direction if remote heads diverge, an effective POM resolves an unexpected ddd4j/Spring version, a test requires a dependency unavailable for that JDK, or a required capability needs a behavior change not represented by the approved design.
