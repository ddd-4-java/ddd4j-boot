# Maven Model 4.1 And Platform Dependency Governance Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修正 ddd4j-boot 4.0/4.1 的 Maven Model 4.1 父引用，并建立“普通组件由 ddd4j-dependencies 统一管理、生态 BOM 只管生态增量”的可执行门禁。

**Architecture:** `ddd4j-dependencies` 作为平台基础 BOM，Boot/Javalin/Quarkus/Cloud 依赖 BOM 只提供各自生态增量。通过 Maven Model 4.1 结构检查、坐标级归属清单和有效模型版本契约防止下游重复管理。

**Tech Stack:** Maven 4.0.0-rc-6, Maven POM Model 4.1, Python 3 `unittest`/`xml.etree.ElementTree`, JDK 21, GitHub maintenance branches.

**Spec:** `docs/superpowers/specs/2026-09-09-model41-platform-dependency-governance.md`

## Global Constraints

- `ddd4j-dependencies` 是普通组件与 `io.ddd4j:*` 的唯一版本权威。
- `ddd4j-boot-dependencies` 只主动管理 Spring Boot BOM、Boot Starter 和 Boot 专属集成。
- 不得为消除告警而删除 ddd4j 对 Jackson、SLF4J、Logback、Netty、Hibernate、Micrometer、JAXB、数据库驱动和 MQ 客户端的源头管理。
- 4.0/4.1 使用 Maven Model 4.1 与 `<subprojects>`，JDK 21，Maven 4.0.0-rc-6。
- 不创建、使用或移除 Git worktree；不 force push；不重写已推送历史。
- `ddd4j feature/3.0.x` 被既有 worktree 占用时，上游 POM 修改必须停止在授权边界。
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

- [ ] **Step 1: Enforce the branch-availability gate**

```bash
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j status --short
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j worktree list
git -C /Users/wandl/workspaces/workspace-ddd4j/workspace-ddd4j-boot/ddd4j switch feature/3.0.x
```

Expected: proceed only if the primary checkout can switch normally. If Git reports that the branch is already checked out by another worktree, stop Task 6 without bypassing or removing it.

- [ ] **Step 2: Add failing effective-version assertions**

For each missing coordinate, extend the existing dependency alignment test with the exact expected version chosen by the platform baseline.

- [ ] **Step 3: Verify RED**

Run the repository's dependency alignment test under JDK 21.

Expected: FAIL only for coordinates absent from the current upstream effective model.

- [ ] **Step 4: Add missing platform management**

Add the version property and dependencyManagement entry to `ddd4j-dependencies/pom.xml`. Do not modify Boot/Javalin/Quarkus/Cloud POMs in this task.

- [ ] **Step 5: Verify GREEN and effective POM**

Run the dependency alignment test and generate the effective POM with Maven 4. Assert every Task 5 platform coordinate resolves to the expected version.

- [ ] **Step 6: Commit and publish only to the local Maven repository**

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

- [ ] **Step 1: Verify the ownership gate is RED against current 4.1**

Run Task 4's repository verification command.

- [ ] **Step 2: Remove only rows proven available upstream**

For each report row with `Present upstream=yes`, remove its Boot property and direct versioned dependencyManagement entry. Keep Spring Boot BOMs, Boot starters and Boot-specific integrations.

- [ ] **Step 3: Verify GREEN**

Run:

```bash
python3 scripts/test_dependency_bom_boundary.py
python3 scripts/verify_dependency_bom_boundary.py \
  --boot-pom ddd4j-boot-dependencies/pom.xml \
  --ownership config/consistency/dependency-ownership.tsv
```

Expected: both commands PASS.

- [ ] **Step 4: Verify critical effective versions and focused reactor**

Generate the 4.1 effective model with Maven 4 and assert the Task 5 versions for Jackson, SLF4J, Logback, Hibernate, Micrometer, ActiveMQ, JAXB, Netty and database drivers. Then compile the license reactor.

- [ ] **Step 5: Commit Boot cleanup**

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

- [ ] **Step 1: Apply the tested governance commit to 4.0**

Resolve only branch-specific Spring Boot differences; do not copy 4.1 dependency versions into 4.0.

- [ ] **Step 2: Run ownership, effective-version and focused Maven checks**

Use Maven 4/JDK 21 and the same commands as Task 7 against branch 4.0.

- [ ] **Step 3: Commit independently**

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

- [ ] **Step 1: Run all static and focused gates on 4.0 and 4.1**

```bash
python3 scripts/test_model41_parent_contract.py
python3 scripts/verify_model41_parent_contract.py .
python3 scripts/test_dependency_bom_boundary.py
bash scripts/consistency/test-maintenance-build-matrix.sh
```

Run the Maven 4/JDK 21 license tests on each branch with `<skipTests>false</skipTests>` where the parent defaults skip tests.

- [ ] **Step 2: Capture warning deltas**

Record before/after values for total model problems, parent warnings, ignored imports and conflicting coordinate groups. Do not report an unchanged or whitelisted warning as fixed.

- [ ] **Step 3: Run the largest safe reactor**

Run the full 4.x reactor when all required local artifacts resolve. If it stops, record the exact first failing module, command, error and passed module count.

- [ ] **Step 4: Update status documents**

Set the specification status to `implemented` only when all unblocked acceptance criteria pass. List Task 6 as blocked if `feature/3.0.x` remains occupied.

- [ ] **Step 5: Verify Git state and push without rewriting history**

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
