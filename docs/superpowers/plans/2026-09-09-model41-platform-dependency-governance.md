# Maven Model 4.1 And Platform Dependency Governance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完成 ddd4j-boot 4.0/4.1 的 Model 4.1、平台依赖权威、Boot 4 样例兼容和完整 73 模块 reactor 闭环，并治理上游 ddd4j 的 233 个 BOM 冲突。

**Architecture:** `ddd4j-dependencies` 作为平台基础 BOM，Boot/Javalin/Quarkus/Cloud 依赖 BOM 只提供各自生态增量。通过 Model 4.1 结构检查、样例源码静态契约、坐标级归属清单、有效模型版本断言和精确 BOM 冲突白名单形成分层门禁；先在 4.1 验证，再传播至 4.0，最后执行独立审查。

**Tech Stack:** Maven 4.0.0-rc-6, Maven POM Model 4.1, Python 3 `unittest`/`xml.etree.ElementTree`, JDK 21, GitHub maintenance branches.

**Spec:** `docs/superpowers/specs/2026-09-09-model41-platform-dependency-governance.md`

## Global Constraints

- `ddd4j-dependencies` 是普通组件与 `io.ddd4j:*` 的唯一版本权威。
- `ddd4j-boot-dependencies` 只主动管理 Spring Boot BOM、Boot Starter 和 Boot 专属集成。
- 不得为消除告警而删除 ddd4j 对 Jackson、SLF4J、Logback、Netty、Hibernate、Micrometer、JAXB、数据库驱动和 MQ 客户端的源头管理。
- 4.0/4.1 使用 Maven Model 4.1 与 `<subprojects>`，JDK 21，Maven 4.0.0-rc-6。
- 不创建、使用或移除 Git worktree；不 force push；不重写已推送历史。
- `ddd4j feature/3.0.x` 只在 `/Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j-v3.0.x-independent` 独立 clone 中修改；不得操作既有 worktree。
- commit、push、Maven deploy、空缓存消费和 Actions 运行是互相独立的证据层。

---

### Task 1: Model 4.1 Parent Structure Gate

**Files:**
- Create: `scripts/verify_model41_parent_contract.py`
- Create: `scripts/test_model41_parent_contract.py`

**Interfaces:**
- Produces: `verify(root: Path) -> list[str]`, 返回每个违规 POM 的精确路径和原因。
- Consumes: Maven 4.1 POM 中的 `modelVersion` 和 `parent` 节点。

- [x] **Step 1: Write failing tests for external and reactor parents**

```python
def test_reactor_parent_rejects_coordinates_with_relative_path(self):
    errors = verify(self.fixture("reactor-parent-with-gav.xml"))
    self.assertTrue(any("reactor parent must use relativePath only" in error for error in errors))

def test_external_parent_rejects_relative_path(self):
    errors = verify(self.fixture("external-parent-with-relative-path.xml"))
    self.assertTrue(any("external parent must use coordinates only" in error for error in errors))
```

- [x] **Step 2: Run the tests and verify RED**

Run: `python3 scripts/test_model41_parent_contract.py`

Expected: FAIL because `verify_model41_parent_contract` does not exist.

- [x] **Step 3: Implement the structure verifier**

```python
def verify(root: Path) -> list[str]:
    errors = []
    for pom in sorted(root.rglob("pom.xml")):
        project = ElementTree.parse(pom).getroot()
        if local_name(project.find("{*}modelVersion")) != "4.1.0":
            continue
        parent = project.find("{*}parent")
        if parent is None:
            continue
        relative_path = text(parent, "relativePath")
        coordinates = [text(parent, name) for name in ("groupId", "artifactId", "version")]
        target = (pom.parent / relative_path).resolve() if relative_path else None
        if relative_path and all(coordinates):
            errors.append(f"{pom}: reactor parent must use relativePath only")
        elif relative_path and not target.exists():
            errors.append(f"{pom}: relativePath does not exist: {relative_path}")
        elif not relative_path and not all(coordinates):
            errors.append(f"{pom}: external parent must use coordinates only")
    return errors
```

- [x] **Step 4: Verify the unit tests pass and current 4.1 tree fails**

Run:

```bash
python3 scripts/test_model41_parent_contract.py
python3 scripts/verify_model41_parent_contract.py .
```

Expected: unit tests PASS; repository verification FAILS and reports the current Model 4.1 parent violations.

- [x] **Step 5: Commit the gate**

```bash
git add scripts/verify_model41_parent_contract.py scripts/test_model41_parent_contract.py
git commit -m "test(model): enforce Maven 4.1 parent contracts"
```

### Task 2: Normalize All 4.1 Parent Declarations

**Files:**
- Modify: `pom.xml`
- Modify: `ddd4j-boot-bom/pom.xml`
- Modify: every Model 4.1 child `pom.xml` reported by Task 1
- Test: `scripts/test_model41_parent_contract.py`

**Interfaces:**
- Consumes: `verify(root: Path) -> list[str]` from Task 1.
- Produces: a Model 4.1 reactor in which all parents use full GAV without `relativePath`.

- [x] **Step 1: Capture the exact RED count**

Run: `python3 scripts/verify_model41_parent_contract.py .`

Expected: FAIL with one error per invalid parent declaration; save the count in the commit message notes.

- [x] **Step 2: Convert the root external parent**

Change the root parent to coordinates only:

```xml
<parent>
    <groupId>io.ddd4j</groupId>
    <artifactId>ddd4j-parent</artifactId>
    <version>3.0.x.20260630-SNAPSHOT</version>
</parent>
```

- [x] **Step 3: Convert every internal parent to coordinate-only form**

For example:

```xml
<parent>
    <groupId>io.ddd4j.boot</groupId>
    <artifactId>ddd4j-boot-dependencies</artifactId>
    <version>${revision}</version>
</parent>
```

Do not change `artifactId`, `${revision}`, packaging, coordinates, dependency declarations or `<subprojects>`.

- [x] **Step 4: Verify GREEN and Maven warning reduction**

Run:

```bash
python3 scripts/test_model41_parent_contract.py
python3 scripts/verify_model41_parent_contract.py .
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -e -pl ddd4j-boot-auth/ddd4j-boot-auth-license -am validate
```

Expected: both Python commands PASS; Maven output contains zero `parent.relativePath` warnings.

- [x] **Step 5: Run focused compilation**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-boot-auth/ddd4j-boot-auth-license -am -DskipTests compile
```

Expected: reactor SUCCESS; existing BOM conflict warnings remain separately measurable.

- [x] **Step 6: Commit the 4.1 parent normalization**

```bash
git add -u -- ':(glob)**/pom.xml'
git commit -m "fix(model): normalize Maven 4.1 parent references"
```

### Task 3: Propagate Parent Contract To 4.0

**Files:**
- Modify: Model 4.1 `pom.xml` files on branch `4.0.x`
- Add/modify: `scripts/verify_model41_parent_contract.py`
- Add/modify: `scripts/test_model41_parent_contract.py`

**Interfaces:**
- Consumes: the tested verifier and transformation contract from Tasks 1-2.
- Produces: the same parent invariant on `4.0.x` without changing Spring Boot `4.0.7`.

- [x] **Step 1: Switch to the existing 4.0 branch after a clean status check**

```bash
git status --short
git switch 4.0.x
git fetch github 4.0.x
git rev-list --left-right --count github/4.0.x...4.0.x
```

- [x] **Step 2: Apply the verified parent-only change**

Apply Task 2's exact invariant; preserve `4.0.x.20260630-SNAPSHOT`, Spring Boot `4.0.7`, JDK 21 and `<subprojects>`.

- [x] **Step 3: Run structure and Maven verification**

```bash
python3 scripts/test_model41_parent_contract.py
python3 scripts/verify_model41_parent_contract.py .
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-boot-auth/ddd4j-boot-auth-license -am -DskipTests compile
```

Expected: structure PASS, zero `parent.relativePath` warnings, focused reactor SUCCESS.

- [x] **Step 4: Commit 4.0 independently**

```bash
git add -u -- ':(glob)**/pom.xml'
git commit -m "fix(model): normalize Maven 4.1 parent references"
```

### Task 4: Dependency Ownership Manifest And Gate

**Files:**
- Create: `config/consistency/dependency-ownership.tsv`
- Modify: `scripts/verify_dependency_bom_boundary.py`
- Modify: `scripts/test_dependency_bom_boundary.py`

**Interfaces:**
- Produces: `OwnershipRule(scope: str, group_id: str, artifact_id: str)` and `verify_ownership(boot_pom: Path, rules: list[OwnershipRule]) -> list[str]`.
- Consumes: explicit properties, direct dependencyManagement entries and BOM imports from an ecosystem POM.

- [x] **Step 1: Add failing ownership tests**

```python
def test_boot_rejects_platform_property_and_direct_version(self):
    rules = [OwnershipRule("platform", "org.slf4j", "slf4j-api")]
    errors = verify_ownership(self.write_pom("slf4j.version", "org.slf4j", "slf4j-api", "2.0.18"), rules)
    self.assertIn("Boot BOM directly versions platform component org.slf4j:slf4j-api", errors)

def test_boot_accepts_boot_starter(self):
    rules = [OwnershipRule("spring-boot", "org.springframework.boot", "spring-boot-starter-web")]
    self.assertEqual(verify_ownership(self.boot_starter_pom(), rules), [])
```

- [x] **Step 2: Verify RED**

Run: `python3 scripts/test_dependency_bom_boundary.py`

Expected: FAIL because `OwnershipRule` and `verify_ownership` do not exist.

- [x] **Step 3: Add the coordinate-level manifest**

```tsv
scope\tgroup_id\tartifact_id
platform\ttools.jackson.core\tjackson-core
platform\torg.slf4j\tslf4j-api
platform\tch.qos.logback\tlogback-classic
platform\tio.netty\tnetty-common
platform\torg.hibernate.orm\thibernate-core
platform\tio.micrometer\tmicrometer-core
platform\torg.apache.activemq\tactivemq-client
platform\torg.glassfish.jaxb\tjaxb-runtime
spring-boot\torg.springframework.boot\tspring-boot-dependencies
spring-boot\torg.springframework.boot\tspring-boot-starter-web
```

Expand the manifest only with coordinates observed in the current POM audit; do not infer ownership from groupId alone.

- [x] **Step 4: Implement manifest loading and explicit-management checks**

```python
@dataclass(frozen=True)
class OwnershipRule:
    scope: str
    group_id: str
    artifact_id: str

def verify_ownership(boot_pom: Path, rules: list[OwnershipRule]) -> list[str]:
    platform = {(rule.group_id, rule.artifact_id) for rule in rules if rule.scope == "platform"}
    properties, dependencies = model(boot_pom)
    errors = []
    for coordinate in sorted(platform):
        if dependencies.get(coordinate) is not None:
            errors.append("Boot BOM directly versions platform component " + ":".join(coordinate))
    return errors
```

Property checks must map `${name.version}` usages back to the dependency coordinate so that generic names such as
`jackson.version` are rejected only when Boot actively uses them for a platform coordinate.

- [x] **Step 5: Verify GREEN and current-repository RED**

```bash
python3 scripts/test_dependency_bom_boundary.py
python3 scripts/verify_dependency_bom_boundary.py \
  --boot-pom ddd4j-boot-dependencies/pom.xml \
  --ownership config/consistency/dependency-ownership.tsv
```

Expected: unit tests PASS; current repository verification reports every Boot-layer platform ownership violation.

- [x] **Step 6: Commit the ownership gate**

```bash
git add config/consistency/dependency-ownership.tsv scripts/verify_dependency_bom_boundary.py scripts/test_dependency_bom_boundary.py
git commit -m "test(deps): enforce ecosystem BOM ownership"
```

### Task 5: Audit And Classify Boot Dependency Management

**Files:**
- Create: `docs/superpowers/reports/2026-09-09-boot41-dependency-ownership-audit.md`
- Modify: `config/consistency/dependency-ownership.tsv`

**Interfaces:**
- Consumes: Task 4 verifier output and current 4.1 effective model.
- Produces: an exact ledger with coordinate, current owner, required owner, current version, upstream availability and action.

- [x] **Step 1: Generate the explicit-management inventory**

Run a structured XML audit over `ddd4j-boot-dependencies/pom.xml`; record each property-backed dependency and imported BOM. Do not derive the inventory from comments or `rg` counts.

- [x] **Step 2: Classify every violation**

Use this exact table schema:

```markdown
| Coordinate | Current owner | Required owner | Current version | Present upstream | Action |
|---|---|---|---|---|---|
| org.slf4j:slf4j-api | ddd4j-boot-dependencies | ddd4j-dependencies | 2.0.18 | yes | remove downstream duplicate |
```

`Present upstream` must be proven from the effective `ddd4j-dependencies:3.0.x` POM, not the source property name.

- [x] **Step 3: Record Maven warning baseline by coordinate**

Capture total model problems, `parent.relativePath`, `Ignored POM import`, and top conflicting groups for 4.1 after Task 2.

- [x] **Step 4: Verify the report contains no unresolved classification**

Run: `rg -n 'TBD|TODO|unknown|unclassified' docs/superpowers/reports/2026-09-09-boot41-dependency-ownership-audit.md`

Expected: no output.

- [x] **Step 5: Commit the audit**

```bash
git add config/consistency/dependency-ownership.tsv docs/superpowers/reports/2026-09-09-boot41-dependency-ownership-audit.md
git commit -m "docs(deps): classify Boot dependency ownership"
```

### Task 6: Close Platform Dependency Gaps In ddd4j 3.0

**Files:**
- Modify in ddd4j repository: `ddd4j-dependencies/pom.xml`
- Modify in ddd4j repository: dependency alignment tests selected by current source

**Interfaces:**
- Consumes: Task 5 rows whose `Required owner` is `ddd4j-dependencies` and `Present upstream` is `no`.
- Produces: effective platform management for every ordinary coordinate required by Boot 4.x.

- [x] **Step 1: Enforce the branch-availability gate**

```bash
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j status --short
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j worktree list
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j switch feature/3.0.x
```

Expected: proceed only if the primary checkout can switch normally. If Git reports that the branch is already checked out by another worktree, stop Task 6 without bypassing or removing it.

- [x] **Step 2: Add failing effective-version assertions**

For each missing coordinate, extend the existing dependency alignment test with the exact expected version chosen by the platform baseline.

- [x] **Step 3: Verify RED**

Run the repository's dependency alignment test under JDK 21.

Expected: FAIL only for coordinates absent from the current upstream effective model.

- [x] **Step 4: Add missing platform management**

Add the version property and dependencyManagement entry to `ddd4j-dependencies/pom.xml`. Do not modify Boot/Javalin/Quarkus/Cloud POMs in this task.

- [x] **Step 5: Verify GREEN and effective POM**

Run the dependency alignment test and generate the effective POM with Maven 4. Assert every Task 5 platform coordinate resolves to the expected version.

- [x] **Step 6: Commit and publish only to the local Maven repository**

```bash
git add ddd4j-dependencies/pom.xml scripts
git commit -m "fix(deps): centralize platform dependency versions"
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-dependencies -am -DskipTests install
```

Expected: local install SUCCESS. Remote Maven deploy remains unauthorized.

### Task 7: Remove Boot-Layer Platform Duplicates

**Files:**
- Modify: `ddd4j-boot-dependencies/pom.xml`
- Modify: root `pom.xml` only where a platform coordinate is explicitly duplicated outside the official Boot compatibility import
- Test: `scripts/test_dependency_bom_boundary.py`

**Interfaces:**
- Consumes: Task 5 ownership ledger and Task 6 effective upstream POM.
- Produces: a Boot ecosystem BOM with no active platform-coordinate versions outside approved imports.

- [x] **Step 1: Verify the ownership gate is RED against current 4.1**

Run Task 4's repository verification command.

- [x] **Step 2: Remove only rows proven available upstream**

For each report row with `Present upstream=yes`, remove its Boot property and direct versioned dependencyManagement entry. Keep Spring Boot BOMs, Boot starters and Boot-specific integrations.

- [x] **Step 3: Verify GREEN**

Run:

```bash
python3 scripts/test_dependency_bom_boundary.py
python3 scripts/verify_dependency_bom_boundary.py \
  --boot-pom ddd4j-boot-dependencies/pom.xml \
  --ownership config/consistency/dependency-ownership.tsv
```

Expected: both commands PASS.

- [x] **Step 4: Verify critical effective versions and focused reactor**

Generate the 4.1 effective model with Maven 4 and assert the Task 5 versions for Jackson, SLF4J, Logback, Hibernate, Micrometer, ActiveMQ, JAXB, Netty and database drivers. Then compile the license reactor.

- [x] **Step 5: Commit Boot cleanup**

```bash
git add pom.xml ddd4j-boot-dependencies/pom.xml config/consistency scripts
git commit -m "fix(deps): restore platform dependency ownership"
```

### Task 8: Propagate Dependency Governance To 4.0

**Files:**
- Modify on branch `4.0.x`: `ddd4j-boot-dependencies/pom.xml`
- Modify on branch `4.0.x`: `config/consistency/dependency-ownership.tsv`
- Modify on branch `4.0.x`: dependency boundary scripts

**Interfaces:**
- Consumes: Task 7's ownership invariant.
- Produces: the same invariant with Spring Boot `4.0.7` and revision `4.0.x.20260630-SNAPSHOT` preserved.

- [x] **Step 1: Apply the tested governance commit to 4.0**

Resolve only branch-specific Spring Boot differences; do not copy 4.1 dependency versions into 4.0.

- [x] **Step 2: Run ownership, effective-version and focused Maven checks**

Use Maven 4/JDK 21 and the same commands as Task 7 against branch 4.0.

- [x] **Step 3: Commit independently**

```bash
git add ddd4j-boot-dependencies/pom.xml config/consistency scripts
git commit -m "fix(deps): restore platform dependency ownership"
```

### Task 9: Final Verification, Warning Ledger And Push

**Files:**
- Modify: `docs/superpowers/reports/2026-09-09-boot41-dependency-ownership-audit.md`
- Modify: `docs/superpowers/specs/2026-09-09-model41-platform-dependency-governance.md`

**Interfaces:**
- Consumes: all prior task outputs.
- Produces: branch-specific evidence for source, tests, model warnings, Git and publication state.

- [x] **Step 1: Run all static and focused gates on 4.0 and 4.1**

```bash
python3 scripts/test_model41_parent_contract.py
python3 scripts/verify_model41_parent_contract.py .
python3 scripts/test_dependency_bom_boundary.py
bash scripts/consistency/test-maintenance-build-matrix.sh
```

Run the Maven 4/JDK 21 license tests on each branch with `<skipTests>false</skipTests>` where the parent defaults skip tests.

- [x] **Step 2: Capture warning deltas**

Record before/after values for total model problems, parent warnings, ignored imports and conflicting coordinate groups. Do not report an unchanged or whitelisted warning as fixed.

- [x] **Step 3: Run the largest safe reactor**

Run the full 4.x reactor when all required local artifacts resolve. If it stops, record the exact first failing module, command, error and passed module count.

- [x] **Step 4: Update status documents**

Set the specification status to `implemented` only when all unblocked acceptance criteria pass. List Task 6 as blocked if `feature/3.0.x` remains occupied.

- [x] **Step 5: Verify Git state and push without rewriting history**

For each changed branch:

```bash
git status --short
git fetch github <branch>
git rev-list --left-right --count github/<branch>...<branch>
git push github <branch>
git rev-parse HEAD
git ls-remote github refs/heads/<branch>
```

Expected: clean worktree and identical local/remote SHA after push. Do not deploy Maven artifacts or run `workflow_dispatch` in this task.

### Task 10: Boot 4 Sample Compatibility Contract

**Files:**
- Create: `scripts/verify_boot4_sample_compatibility.py`
- Create: `scripts/test_boot4_sample_compatibility.py`
- Scan: `ddd4j-boot-samples/**/pom.xml`
- Scan: `ddd4j-boot-samples/**/src/main/java/**/*.java`

**Interfaces:**
- Produces: `verify(root: Path) -> list[str]`, reporting the exact file and forbidden Boot 3, empty Dozer converter, or old MyBatis-Plus reference.
- Consumes: the full samples source tree; it does not depend on Maven reaching the next reactor module.

- [x] **Step 1: Write failing fixture tests**

```python
def test_rejects_all_three_boot4_incompatibilities(self):
    errors = verify(self.fixture_root)
    self.assertTrue(any("old MeterRegistryCustomizer package" in item for item in errors))
    self.assertTrue(any("empty dozer-extra-converters dependency" in item for item in errors))
    self.assertTrue(any("old MyBatis-Plus service package" in item for item in errors))

def test_accepts_boot4_packages_without_empty_converter(self):
    self.assertEqual(verify(self.compatible_fixture_root), [])
```

- [x] **Step 2: Run RED**

Run: `python3 scripts/test_boot4_sample_compatibility.py`

Expected: FAIL because `verify_boot4_sample_compatibility` does not exist.

- [x] **Step 3: Implement the exhaustive scanner**

```python
FORBIDDEN = {
    "org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer": "old MeterRegistryCustomizer package",
    "com.github.dozermapper.extra.converters": "empty Dozer converter API",
    "com.baomidou.mybatisplus.extension.service": "old MyBatis-Plus service package",
}

def verify(root: Path) -> list[str]:
    errors = []
    for source in sorted(root.glob("ddd4j-boot-samples/**/src/main/java/**/*.java")):
        content = source.read_text(encoding="utf-8")
        for token, reason in FORBIDDEN.items():
            if token in content:
                errors.append(f"{source}: {reason}: {token}")
    for pom in sorted(root.glob("ddd4j-boot-samples/**/pom.xml")):
        content = pom.read_text(encoding="utf-8")
        if "<artifactId>dozer-extra-converters</artifactId>" in content:
            errors.append(f"{pom}: empty dozer-extra-converters dependency")
    return errors
```

- [x] **Step 4: Prove unit GREEN and repository RED**

```bash
python3 scripts/test_boot4_sample_compatibility.py
python3 scripts/verify_boot4_sample_compatibility.py .
```

Expected: unit tests PASS; repository scan FAILS with 15 old MeterRegistry imports, 14 Dozer source users, 14 converter dependencies, and 24 old MyBatis-Plus service-package files (67 findings total).

- [x] **Step 5: Commit the contract**

```bash
git add scripts/verify_boot4_sample_compatibility.py scripts/test_boot4_sample_compatibility.py
git commit -m "test(samples): enforce Boot 4 source compatibility"
```

### Task 11: Repair All Boot 4.1 Samples And Reach 73/73

**Files:**
- Modify: the 15 Java files reported for the old `MeterRegistryCustomizer` import
- Delete: the 14 `ddd4j-boot-samples/**/DozerMapperConfiguration.java` files reported by Task 10
- Modify: the 14 sample POMs reported for `dozer-extra-converters`
- Modify: the 24 service Java files reported for `com.baomidou.mybatisplus.extension.service`
- Test: `scripts/test_boot4_sample_compatibility.py`

**Interfaces:**
- Consumes: Task 10's exact repository scan.
- Produces: a source tree with no known Boot 3 metrics import, empty converter use, or pre-3.5.17 MyBatis-Plus service import.

- [x] **Step 1: Preserve the RED evidence**

Run: `python3 scripts/verify_boot4_sample_compatibility.py .`

Expected: non-zero exit with the exact 67 baseline findings described in Task 10.

- [x] **Step 2: Apply the minimal source migrations**

Use these exact replacements:

```java
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import com.baomidou.mybatisplus.spring.service.IService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
```

Delete each reported `DozerMapperConfiguration.java` rather than recreating unavailable converter behavior. Remove only the complete dependency block whose artifactId is `dozer-extra-converters`; retain the Dozer Spring Boot starter.

- [x] **Step 3: Run the static contract GREEN**

```bash
python3 scripts/test_boot4_sample_compatibility.py
python3 scripts/verify_boot4_sample_compatibility.py .
git diff --check
```

Expected: all commands PASS and each forbidden reference count is zero.

- [x] **Step 4: Run focused sample reactors**

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-boot-samples/ddd4j-boot-sample-starter-druid -am clean test
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-boot-samples -am clean test
```

Expected: both reactors SUCCESS; tests are not skipped by the command line.

- [x] **Step 5: Run the full 4.1 reactor**

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp clean verify
```

Expected: reactor summary reports 73 successful projects and no failed or skipped project.

- [x] **Step 6: Commit and push 4.1**

```bash
git add -u ddd4j-boot-samples
git add scripts/verify_boot4_sample_compatibility.py scripts/test_boot4_sample_compatibility.py
git commit -m "fix(samples): restore Boot 4 compatibility"
git fetch origin 4.1.x
git rev-list --left-right --count HEAD...origin/4.1.x
git push origin 4.1.x
```

Expected: fetch comparison is `0 0` before push except for the local commits being published; no force push.

### Task 12: Propagate The Proven Sample Repair To 4.0

**Files:**
- Modify/delete on `4.0.x`: the same paths selected by Task 10's scanner
- Add on `4.0.x`: `scripts/verify_boot4_sample_compatibility.py`
- Add on `4.0.x`: `scripts/test_boot4_sample_compatibility.py`

**Interfaces:**
- Consumes: Task 11's tested changes, preserving Spring Boot `4.0.7` and revision `4.0.x.20260630-SNAPSHOT`.
- Produces: the same zero-finding sample contract and 73/73 reactor result on 4.0.

- [x] **Step 1: Switch only after a clean-tree and divergence check**

```bash
git status --short
git fetch origin 4.0.x
git switch 4.0.x
git rev-list --left-right --count HEAD...origin/4.0.x
```

- [x] **Step 2: Apply the 4.1 sample commit without copying branch metadata**

Resolve and apply the tested commit without a hand-written hash:

```bash
sample_fix_commit=$(git log 4.1.x -1 --format=%H --grep='fix(samples): restore Boot 4 compatibility')
test -n "$sample_fix_commit"
git cherry-pick "$sample_fix_commit"
```

Resolve only genuine 4.0 sample-source differences. Verify `pom.xml` still declares Spring Boot `4.0.7` and revision `4.0.x.20260630-SNAPSHOT`.

- [x] **Step 3: Run static, focused, and full verification**

Run the four Task 11 commands under JDK 21/Maven 4.

Expected: static contract PASS, focused sample reactors SUCCESS, and full reactor 73/73.

- [x] **Step 4: Push 4.0 after remote reconciliation**

```bash
git fetch origin 4.0.x
git rev-list --left-right --count HEAD...origin/4.0.x
git push origin 4.0.x
```

### Task 13: Create A Coordinate-Level Maven BOM Conflict Contract

**Files:**
- Create in the independent ddd4j clone: `scripts/verify_bom_import_conflicts.py`
- Create in the independent ddd4j clone: `scripts/test_bom_import_conflicts.py`
- Create in the independent ddd4j clone: `config/dependencies/bom-conflict-allowlist.tsv`
- Modify in the independent ddd4j clone: `scripts/test_platform_version_contract.py`
- Modify in the independent ddd4j clone: `scripts/verify_platform_version_contract.py`

**Interfaces:**
- Produces: `Conflict(imported_by, group_id, artifact_id, current_version, ignored_version)` and `verify(log: Path, allowlist: Path) -> list[str]`.
- The allowlist schema is `group_id<TAB>artifact_id<TAB>current_version<TAB>ignored_version<TAB>final_version<TAB>authority<TAB>reason`.

- [x] **Step 1: Write RED parser and allowlist tests**

```python
def test_unlisted_conflict_fails(self):
    errors = verify(self.maven_log("org.slf4j:slf4j-api:2.0.18", "2.0.17"), self.empty_allowlist)
    self.assertEqual(len(errors), 1)

def test_exact_allowlist_entry_passes_but_version_drift_fails(self):
    self.assertEqual(verify(self.maven_log("org.slf4j:slf4j-api:2.0.18", "2.0.17"), self.allowlist), [])
    self.assertNotEqual(verify(self.maven_log("org.slf4j:slf4j-api:2.0.19", "2.0.17"), self.allowlist), [])
```

- [x] **Step 2: Run RED, implement exact matching, and run GREEN**

```bash
python3 scripts/test_bom_import_conflicts.py
```

Expected before implementation: import or assertion failure. Expected after implementation: PASS; matching never falls back to group-only or artifact-only rules.

- [x] **Step 3: Capture the current 233-conflict baseline**

Generate `ddd4j-dependencies` effective POM under JDK 21/Maven 4, save Maven diagnostics outside Git, and feed the log to the verifier with an empty allowlist.

Expected: 7,286 expanded warnings normalize to 233 unique conflict tuples.

- [x] **Step 4: Commit the RED governance contract**

```bash
git add scripts/verify_bom_import_conflicts.py scripts/test_bom_import_conflicts.py \
  scripts/test_platform_version_contract.py scripts/verify_platform_version_contract.py \
  config/dependencies/bom-conflict-allowlist.tsv
git commit -m "test(deps): enforce exact BOM conflict governance"
```

### Task 14: Resolve ddd4j BOM Conflicts By Authority Family

**Files:**
- Modify in the independent ddd4j clone: `ddd4j-dependencies/pom.xml`
- Modify in the independent ddd4j clone: `scripts/test_platform_version_contract.py`
- Modify in the independent ddd4j clone: `scripts/verify_platform_version_contract.py`
- Modify only for irreducible conflicts: `config/dependencies/bom-conflict-allowlist.tsv`

**Interfaces:**
- Consumes: Task 13's 233-tuple baseline.
- Produces: one final platform-owned version per coordinate, with no unlisted import conflict.

- [x] **Step 1: Resolve ActiveMQ, then Micrometer, Hibernate, SLF4J and JAXB**

For each family, first add exact expected-version assertions to `test_platform_version_contract.py`, run them RED, then change `ddd4j-dependencies/pom.xml`. Remove an imported BOM only after comparing its managed coordinate set with the remaining effective model; otherwise add direct dependencyManagement entries using the platform property.

- [x] **Step 2: Resolve the remaining named families in order**

Repeat the RED/GREEN cycle for Oracle JDBC, Brave, gRPC, GraphQL, Ehcache, and Elasticsearch. Do not reorder imports as the sole fix.

- [x] **Step 3: Classify any irreducible residue exactly**

Add one TSV row per remaining tuple with both observed versions, the effective final version, authority `ddd4j-dependencies`, and a concrete compatibility reason. The verifier must fail if any field or version changes.

- [x] **Step 4: Verify after every family and finally clean-install**

```bash
python3 scripts/test_platform_version_contract.py
python3 scripts/test_bom_import_conflicts.py
JAVA_HOME=$(/usr/libexec/java_home -v 21) /Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn \
  -nsu -B -ntp -pl ddd4j-dependencies -am clean install
```

Expected: tests PASS, install SUCCESS, and the conflict verifier reports zero unlisted or drifted tuple.

- [x] **Step 5: Commit and push the upstream authority changes**

```bash
git add ddd4j-dependencies/pom.xml scripts config/dependencies/bom-conflict-allowlist.tsv
git commit -m "fix(deps): converge platform BOM imports"
git fetch origin feature/3.0.x
git rev-list --left-right --count HEAD...origin/feature/3.0.x
git push origin feature/3.0.x
```

### Task 15: Revalidate Boot 4.1 And 4.0 Against The Governed Upstream BOM

**Files:**
- Modify: `docs/superpowers/reports/2026-09-09-boot41-dependency-ownership-audit.md`
- Modify: `docs/superpowers/specs/2026-09-09-model41-platform-dependency-governance.md`

**Interfaces:**
- Consumes: Task 14's locally installed `ddd4j-dependencies:3.0.x.20260630-SNAPSHOT`.
- Produces: branch-specific final model, reactor, Git, and unpublished-publication evidence.

- [x] **Step 1: Run both Boot branches against the new local upstream**

On each branch run parent, ownership, sample compatibility, platform version, license, focused sample, and full `clean verify` gates under JDK 21/Maven 4.

- [x] **Step 2: Record exact warning and reactor deltas**

Update the report with old and final totals for model problems, expanded ignored imports, unique conflict tuples, allowlisted tuples, and reactor successes. Do not describe allowlisted diagnostics as eliminated.

- [x] **Step 3: Update formal status only from evidence**

Set the specification to implemented only if both reactors are 73/73 and Task 13 reports no unlisted conflict. Explicitly retain Maven deploy, empty-cache consumption, and Actions as unexecuted evidence layers.

- [x] **Step 4: Commit and push evidence independently on each Boot branch**

Use `git diff --check`, fetch/divergence checks, normal push, and local/remote SHA comparison. Do not force push or deploy.

### Task 16: Independent Review And Completion Gate

**Files:**
- Review: all commits produced by Tasks 10-15 in the Boot and ddd4j repositories
- Modify only when findings require a correction: affected source, test, POM, or evidence file

**Interfaces:**
- Consumes: final Git diffs and command evidence.
- Produces: an independent severity-ranked review with no unresolved Critical or Important finding.

- [x] **Step 1: Dispatch one independent code reviewer after implementation**

The reviewer checks behavioral correctness, dependency authority, exact allowlist safety, Boot 4.0/4.1 compatibility, consumer-facing effective POMs, and whether 73/73 evidence is reproducible.

- [x] **Step 2: Fix every Critical or Important finding with a focused RED/GREEN cycle**

Run the smallest reproducer first, implement the minimal correction, then rerun the affected branch's full gate. Commit corrections separately.

- [x] **Step 3: Run completion verification**

```bash
git diff --check
git status --short
git rev-parse HEAD
current_branch=$(git branch --show-current)
test -n "$current_branch"
git ls-remote origin "refs/heads/$current_branch"
```

Expected: clean trees, matching local/remote SHAs, Boot 4.1 and 4.0 at 73/73, no unlisted BOM conflict, and no unresolved Critical/Important review finding.

### Task 17: Replace Redistpl With Spring Data Redis On 2.3.x

**Files:**
- Delete: `ddd4j-boot-data/ddd4j-boot-data-external/src/main/java/io/ddd4j/boot/data/external/adapter/RedisOperationRegionCache.java`
- Create: `ddd4j-boot-data/ddd4j-boot-data-external/src/main/java/io/ddd4j/boot/data/external/adapter/RedisTemplateRegionCache.java`
- Modify: `ddd4j-boot-data/ddd4j-boot-data-external/src/main/java/io/ddd4j/boot/data/external/config/Ddd4jExternalAutoConfiguration.java`
- Modify: `ddd4j-boot-data/ddd4j-boot-data-external/pom.xml`
- Modify: `ddd4j-boot-dependencies/pom.xml`
- Create: `ddd4j-boot-data/ddd4j-boot-data-external/src/test/java/io/ddd4j/boot/data/external/adapter/RedisTemplateRegionCacheTest.java`
- Create: `ddd4j-boot-data/ddd4j-boot-data-external/src/test/java/io/ddd4j/boot/data/external/adapter/RedisTemplateRegionCacheIntegrationTest.java`
- Modify: `ddd4j-boot-data/ddd4j-boot-data-external/src/test/java/io/ddd4j/boot/data/external/config/Ddd4jExternalAutoConfigurationTest.java`

**Interfaces:**
- Produces: `RedisTemplateRegionCache(StringRedisTemplate)` implementing `RegionCache`.
- Removes: compile/runtime dependency on `RedisOperationTemplate` and `redistpl-plus-spring-boot-starter`.

- [x] **Step 1: Write unit RED tests**

Assert null-constructor rejection, `opsForValue().get`, TTL-aware `set`, and auto-configuration fallback to
`RegionCache.none()` when no `StringRedisTemplate` is present.

- [x] **Step 2: Run unit RED**

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 1.8) ./mvnw -B -ntp \
  -pl ddd4j-boot-data/ddd4j-boot-data-external -am \
  -Dtest=RedisTemplateRegionCacheTest,Ddd4jExternalAutoConfigurationTest \
  -Dsurefire.failIfNoSpecifiedTests=false test
```

Expected: compilation fails because `RedisTemplateRegionCache` does not exist.

- [x] **Step 3: Implement the minimal adapter and dependency change**

Use `StringRedisTemplate.opsForValue()` for string get/set, retain `Objects.requireNonNull`, replace the
auto-configuration provider type, add `spring-boot-starter-data-redis`, and remove the Redistpl dependency and
version property/management entry.

- [x] **Step 4: Run unit GREEN and Redis Testcontainers RED/GREEN**

Add `testcontainers-junit-jupiter` and a `GenericContainer` using `redis:7.4-alpine`. Prove write/read and TTL
expiry against the real container; Docker skip is not accepted as PASS.

- [x] **Step 5: Run 2.3.x focused and empty-cache full reactor**

Use JDK 8/Maven 3.9.16 and the existing isolated repository `/tmp/ddd4j-boot23-remote.JK74bt`. Run external
module tests, samples smoke targets, then `clean verify`. Record the next first failure rather than excluding it.

- [x] **Step 6: Commit and push 2.3.x**

```bash
git add ddd4j-boot-data ddd4j-boot-dependencies/pom.xml
git commit -m "fix(data): replace unavailable Redistpl adapter"
git push origin 2.3.x
git push github 2.3.x
```

### Task 18: Propagate The Redis Adapter Across The Remaining 12 Lines

**Files:**
- Modify/delete the same external-data adapter, POM and tests on `2.4.x`–`2.7.x`, `3.0.x`–`3.5.x`, `4.0.x`, `4.1.x`.

**Interfaces:**
- Consumes: Task 17 behavior contract.
- Produces: branch-native Spring Data Redis integration with no Redistpl coordinate or type reference.

- [x] **Step 1: Propagate 2.4.x–2.7.x under JDK 8**

Apply tests before implementation on each branch, preserve Java 8/`javax`, run external module unit and container
tests, commit independently and push both remotes.

- [x] **Step 2: Propagate 3.0.x–3.5.x under JDK 17**

Use Boot 3/Jakarta branch-native sources, run the same observable contract, commit independently and push.

- [x] **Step 3: Propagate 4.0.x–4.1.x under JDK 21/Maven 4**

Preserve Model 4.1 `<subprojects>`, Boot 4.0.8/4.1.0 and branch-specific conflict allowlists. Run parent,
ownership, sample-compatibility and external-data tests before push.

- [x] **Step 4: Run the negative cross-line scan**

For all 13 refs, fail if either `redistpl-plus-spring-boot-starter` or `RedisOperationTemplate` remains in a POM,
Java source or generated consumer POM.

### Task 19: Complete Samples Smoke And Thirteen Full Reactors

**Files:**
- Modify: current representative sample tests only where startup cannot run without an external service.
- Modify: `docs/superpowers/reports/ddd4j-boot-cross-jdk-consistency-final.md`
- Modify: `docs/superpowers/reports/ddd4j-boot-release-line-input.tsv`

**Interfaces:**
- Produces: one exact per-line result for remote resolution, compile, tests, Testcontainers, sample smoke and full reactor.

- [x] **Step 1: Execute representative samples per JDK group**

Run order/layered and the available WebMVC/WebFlux starter samples with branch-native test infrastructure.
Replace unconditional `@Disabled` only when a Testcontainers or in-memory fixture supplies every dependency;
never enable a test that still reaches an unmanaged external system.

- [x] **Step 2: Run 13 isolated `clean verify` reactors**

Use each line's already-created `/tmp/ddd4j-boot*-remote.*` repository and Aliyun settings. JDK8/JDK17 use
Maven 3.9.16; JDK21 uses Maven 4.0.0-rc-6. Zero tests or skipped required Testcontainers tests cannot pass.

- [x] **Step 3: Repair each new first failure by RED/GREEN**

Classify missing private artifacts, source compatibility, container provisioning and behavior failures separately;
make only branch-native fixes and restart from the failed module before the final clean run.

- [x] **Step 4: Update evidence and task states**

Promote a release-line row from `BLOCKED` to `PASS` only after its full reactor and required container/smoke tests
pass. Mark the Boot 3.4 sample-smoke/full-verify plan items complete only from this evidence.

### Task 20: Independent Review And Final Pre-Publish Gate

**Files:**
- Review all Task 17–19 commits and generated consumer POMs.
- Modify only files required by Critical/Important findings.

- [x] **Step 1: Dispatch an independent reviewer**

Review API compatibility, Redis serialization/TTL semantics, branch-native Java compatibility, Testcontainers
isolation, Maven Model 4.1, remote-consumer evidence and task-status truthfulness.

- [x] **Step 2: Resolve all Critical/Important findings**

Use focused RED/GREEN tests and rerun each affected line's full reactor.

- [ ] **Step 3: Verify all repositories and remotes**

Run every consistency script, validate 13 PASS release rows, `git diff --check`, clean status, and exact
Codeup/GitHub SHA equality on every branch.

### Task 21: Deploy All Thirteen Lines To Aliyun And Reconsume

**Files:**
- Modify: final evidence report and plan checkboxes only after publication succeeds.

**Interfaces:**
- Consumes: Task 20 clean, reviewed, pushed branch SHAs.
- Produces: Aliyun snapshot publication plus new empty-cache consumption evidence per line.

- [x] **Step 1: Run the publication preflight**

Verify `~/.m2/settings.xml` contains the `2624322-snapshot-3EoOv3` server without printing credentials. Check
each branch revision remains `X.Y.x.20260630-SNAPSHOT` and distributionManagement targets the snapshot repository.

- [ ] **Step 2: Deploy lines serially with branch-native toolchains**

For each branch run `clean deploy -DskipTests` only after its full verified build. Use JDK 8 for 2.x, JDK 17 for
3.x, and JDK 21/Maven 4 plus the existing publication bridge for 4.x. Stop on the first non-zero deploy; do not
retry blindly or force metadata.

- [ ] **Step 3: Reconsume every published line from a newly empty repository**

Create a new local repository per branch after deploy, resolve the timestamped parent/dependencies/BOM and compile
a representative consumer. Verify `_remote.repositories` identifies the Aliyun snapshot repository.

- [ ] **Step 4: Record publication evidence and close the plans**

Record branch SHA, deployed revision, timestamped snapshot, deploy result, consumer result and log path. Mark
Task 21 and the overall stage complete only when all 13 lines pass; Actions remains a separate optional gate.
