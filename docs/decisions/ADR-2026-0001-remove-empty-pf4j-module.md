# ADR-2026-0001：移除空壳模块 ddd4j-boot-extension-pf4j

- 日期：2026-10-06
- 分支：4.0.x
- 关联：家族任务 7.1（pf4j 空壳裁定）/ 家族调研缺陷 F-B3；change：`establish-boot-observability` 同批卫生清理
- 状态：已采纳

## 背景

`ddd4j-boot-extensions/ddd4j-boot-extension-pf4j` 自引入以来始终是空壳模块：

- 目录内仅一个 `pom.xml`， **无任何 `src/` 源码、资源或自动装配文件**；
- 其声明的依赖为上游 `io.ddd4j:ddd4j-extension-pf4j`（底座扩展，独立维护）与 `org.pf4j:pf4j-spring`；
- jar 打包配置 `skipIfEmpty=false`，实际发布了一个 **空 jar**构件 `io.ddd4j.boot:ddd4j-boot-extension-pf4j`。

## 调查（只读证据）

全仓 grep（`--include=*.java --include=*.imports --include=*.factories` 与 `--include=pom.xml`）结果：

- 源码/资源引用： **0 处**（无任何 Java/imports/factories 引用 pf4j）；
- POM 引用：仅 2 处登记（`ddd4j-boot-extensions/pom.xml` 的 subprojects 聚合、`ddd4j-boot-bom/pom.xml` 的
  dependencyManagement），加上其自身 pom，无任何消费方。

## 决策

**删除该模块**（而非补实现），理由：

1. 无 src、无引用、无消费方——补实现属"无需求驱动的臆测性开发"，违背适配线"上游纯 Java + 条件装配薄适配"范式：只有当业务确需
   PF4J 插件体系的 Boot 自动装配时，再按需求新建 change 实现；
2. 空壳 jar 的发布污染依赖树与 BOM（消费方引入后得不到任何类）；
3. 底座 `io.ddd4j:ddd4j-extension-pf4j`（真正实现）不受影响，boot 线未来需要时按需建模块。

## 变更内容

1. 删除目录 `ddd4j-boot-extensions/ddd4j-boot-extension-pf4j/`（仅含 pom.xml）；
2. `ddd4j-boot-extensions/pom.xml`：subprojects 移除该条目（并修正其错位缩进）；
3. `ddd4j-boot-bom/pom.xml`：dependencyManagement 移除该条目（保留注释指向本记录）。

## 影响

- 4.0.x 聚合构建不再产出空 jar；
- 若外部消费方曾依赖 `io.ddd4j.boot:ddd4j-boot-extension-pf4j`（空 jar，无 API 面），升级到本版本后将解析失败——按空构件语义评估无实际影响；如需兼容可改引底座
  `io.ddd4j:ddd4j-extension-pf4j`；
- 上游 `spring-boot-starter-pf4j*`（easy4j 组件）的版本管理不受影响。

## 验证

- `mvn -f ddd4j-boot-extensions/pom.xml -N install`（聚合 POM 校验）通过；
- 受影响模块构建与测试输出见对应任务的证据记录（最终验证一节）。
