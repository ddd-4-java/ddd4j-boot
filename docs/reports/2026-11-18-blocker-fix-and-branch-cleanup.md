# 构建阻塞修复与临时分支清理

日期：2026-11-18（北京时间）

## GitHub 发布与日志规范更新

- master、13 条维护线、3 个历史分支的代码已实际发布到 GitHub；revision-shadow 完整源码另存于远程 `archive/20261118/fix-2.7.x-revision-shadow` 分支。18 个远程源码树及分支头均逐一核对。
- GitHub 的 12 个 `fix/*` 分支已全部删除，随后通过 GitHub API 查询确认余量为 0。发布回执见 `2026-11-18-github-publish-receipt.md`。
- 因终端 DNS 限制，采用 GitHub API 发布同步提交。原始本地提交历史保存在 `refs/archive/github-sync-20261118/`，本地工作分支与远程同步提交对齐，文件内容未丢失。
- 本轮按用户给出的 Repository 自动配置示例统一日志：`@Slf4j`、清理 Logger 导入、`logger` 改为 `log`、消除逐次 getLogger、生产诊断输出改为参数化日志、MQ 异常保留 Throwable 堆栈。静态内部类保留自己的日志类别，非静态监听器复用外部类日志。
- master 的 19 个受影响模块及其依赖 reactor 编译通过，Repository 自动配置现有 5 项测试通过。其他版本的完整构建仍受前述离线依赖条件限制；不宣称所有版本 CI 通过。
- 历史归档分支保持原样，不对归档源码继续重写。

## 本轮继续推进结果

- 本地 `fix/*` 分支已全部清理（共 12 个：上一轮 10 个，本轮 2 个）。远程未删除。
- `fix/feature-REQ-20261015-wandl-dependency-ci-javadoc-20261031` 及待同步副本的全部提交已通过真实合并进入 `3.4.x`，提交 `b376c8f4ec02488340aeaf05e27ac6c3011ab4c5`；验证祖先关系后删除这两个冗余本地分支。
- 合并后新增验证：5 项 Java 回归、4 项许可策略检查、4 项 feature/维护线矩阵单元测试，全部通过。
- `fix/2.7.x-revision-shadow` 的实际修复（删除子 POM 遮蔽根 revision 的属性）已存在于当前 `2.7.x`。JDK 8 + Maven `help:evaluate -Dexpression=project.version` 实测为 `2.7.x.20260630-SNAPSHOT`，4 项内嵌 Shell 回归和 2.7.x 构建矩阵门禁通过。
- revision-shadow 分支还包含旧模块结构和历史代码。其完整末提交 `271260180aacf54f77266cbd8c49c2be852adda1` 已保存为注解标签 `archive/20261118/fix-2.7.x-revision-shadow`，确认标签解引用一致后删除本地 fix 分支。此操作是“归档旧历史 + 验证当前修复”，不宣称整支旧架构已经合入 2.7.x。
- 仍保留 4 个非 fix 分支：backup、两个历史 2.7 feature，以及被外部 worktree 占用的 dependency-ci feature。最后一个的全部修改现已进入 3.4.x，仅其原分支名称因仍被检出而保留。

## 上一阶段结果（历史记录）

- 之前“分别应用 API 修改”不等于“修复分支已合入维护分支”。本次实际完成 10 个修复分支到目标维护分支的合并，再验证祖先关系并删除对应本地分支。
- 另一个 feature 文档修复已合入待同步分支，但原 feature 分支位于不可写的外部 worktree，因此原分支和其修复分支均保留。
- 13 条维护线的 WebFlux 与 DataScope POM 均已使用对应 ddd4j BOM 管理的 `io.github.easy4j:spring-extension`；清理旧 spring-biz 坐标和缺失版本声明。没有升级或降级各分支的 Spring Boot/JDK/ddd4j 版本。
- 修正跨版本构建矩阵中过期的 Boot 4.0.7 基线为该分支实际使用的 4.0.8，13 条维护线矩阵验证通过。
- 4.0/4.1 的 Model 4.1 / subprojects 是合法 Maven 4 配置。前次用 Maven 3 产生的 malformed POM 是工具选择错误，本次使用已有 Maven 4.0.0-rc-6 + JDK 21 验证，不改回 Maven 3 格式。
- Maven 4 在本环境使用 `-Daether.syncContext.named.factory=rwlock-local`，避免文件锁目录写入导致解析器出现次生 ClassCastException；随后已取得真实依赖解析错误。

## 验证

- master 合并后 6 项定向回归通过：MyBatis、COLA、Dubbo、业务响应与国际化隔离。
- 3.4.x 合并后 5 项定向回归通过。
- 其余 12 条维护线实际重跑，均停在离线依赖解析；没有将未编译的分支报告为构建通过。
- 13 条维护线均通过 no-record 扫描；master 扫描器 5 项自测通过；全 13 线构建矩阵门禁通过。
- DataScope 后续仅改依赖坐标，未运行其完整独立构建；受同一依赖与网络条件限制。

## 已合并并删除的本地分支

| 已删除分支 | 原分支末提交 | 目标维护分支 | 当前目标提交 |
| --- | --- | --- | --- |
| fix/2-3-x-spring-biz-version-20261031 | caa9fb08275b | 2.3.x | deb6c64733f4 |
| fix/2-4-x-javadoc-20261031 | 307544fbe8cb | 2.4.x | 26133146845e |
| fix/2-5-x-spring-biz-version-20261031 | bb5af489c62e | 2.5.x | e043a3d39c1c |
| fix/2-6-x-spring-biz-version-20261031 | ea27d874b824 | 2.6.x | 7f12462fa64a |
| fix/2-7-x-spring-biz-version-20261031 | 6e755146347c | 2.7.x | 879ac3126b96 |
| fix/3-2-x-spring-biz-version-20261031 | 3aa6c3b9006a | 3.2.x | 5fdee467412a |
| fix/3-3-x-spring-biz-version-20261031 | dcded189b8d2 | 3.3.x | 4e3fb3100b38 |
| fix/3-4-x-javadoc-20261031 | b64d86a3b6f2 | 3.4.x | 56c9b222922b |
| fix/3-5-x-spring-biz-version-20261031 | dcc07c001fac | 3.5.x | de92323b63df |
| fix/master-javadoc-20261031 | eb3b2a3557b9 | master | d1a5efcebae5 |

## 上一阶段保留清单（以本轮结果为准）

| 分支 | 保留原因 |
| --- | --- |
| backup/2.3.x-before-origin-alignment-20260831 | 与 2.3.x 存在历史分叉和独有提交，未完成整支合并 |
| feature/2.7.x-20260128 | 与 2.7.x 模块和历史差异较大，存在独有提交 |
| feature/2.7.x-module-restructure | 模块重构布局与 2.7.x 不同，未完成整支合并 |
| feature/REQ-20261015-wandl-dependency-ci | 有独有修复，且被外部 worktree 检出，原指针尚未同步 |
| fix/2.7.x-revision-shadow | 与当前 2.7.x 存在大量历史分叉，未完成整支合并 |
| fix/feature-REQ-20261015-wandl-dependency-ci-javadoc-20261031 | 已合入待同步副本，尚未进入原 feature 分支 |

上一阶段的待同步副本 `sync/feature_REQ-20261015-wandl-dependency-ci-unified-api` 已于本轮完整合入 3.4.x 并删除；原 feature 分支仍被外部 worktree 检出，暂保留该名称。

## 仍需外部条件的阻塞

DNS 实测无法解析 `repo.maven.apache.org`，前次 GitHub 也无法解析；无法下载缺失的 BOM/JAR，无法刷新、推送或删除远程分支。本次只删除本地分支，保留远程跟踪引用，不将本地清理冒充远程清理。

| 维护分支 | 首个实际缺失依赖或结果 | 当前提交 |
| --- | --- | --- |
| 2.3.x | de.codecentric:spring-boot-admin-dependencies:pom:2.3.1 | deb6c64733f4 |
| 2.4.x | org.springframework.boot:spring-boot-dependencies:pom:2.4.13 | 26133146845e |
| 2.5.x | org.springframework.boot:spring-boot-dependencies:pom:2.5.15 | e043a3d39c1c |
| 2.6.x | de.codecentric:spring-boot-admin-dependencies:pom:2.6.11 | 7f12462fa64a |
| 2.7.x | io.ddd4j:ddd4j-data-mybatisplus:jar:1.0.x.20260630-SNAPSHOT | 879ac3126b96 |
| 3.0.x | org.springframework.boot:spring-boot-dependencies:pom:3.0.13 | a84a990997e5 |
| 3.1.x | org.springframework.boot:spring-boot-dependencies:pom:3.1.12 | 59a0147834b8 |
| 3.2.x | io.micrometer:micrometer-registry-prometheus:jar:1.12.12 | 5fdee467412a |
| 3.3.x | org.springframework.boot:spring-boot-starter-logging:jar:3.3.13 | 4e3fb3100b38 |
| 3.4.x | 5 项定向测试通过 | 56c9b222922b |
| 3.5.x | io.micrometer:micrometer-registry-prometheus:jar:1.15.12 | de92323b63df |
| 4.0.x | io.micrometer:micrometer-registry-prometheus:jar:1.16.7 | 6c38d3d903f7 |
| 4.1.x | de.codecentric:spring-boot-admin-dependencies:pom:4.1.2 | 176e79f850d6 |

用户原有 `ddd4j-boot-data/src/main/resources/scripts/redis-snowflake-batch.lua` 的暂存删除已按原状态保留，未纳入本次合并或修复提交。
