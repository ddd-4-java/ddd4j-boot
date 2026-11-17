# 统一响应 API 迁移与分支同步验收

日期：2026-11-17（北京时间）

> 此文是 11 月 17 日的历史快照。后续合并、分支删除和修正后的构建结论见 [11 月 18 日清理报告](2026-11-18-blocker-fix-and-branch-cleanup.md)。

## 修改范围

- 延续用户原有 51 个文件的修改：统一使用 `io.ddd4j.core.api.R`、`ApiCode`、`CustomApiCode`；移除旧 `SC_` 枚举名。
- 保留各分支模块布局、版本和依赖契约；异常处理器改为构造器注入，COLA/Dubbo 使用 Lombok 日志，Dubbo 使用兼容 Java 8 的 switch 标签。
- 12 份业务错误码枚举不再修改共享枚举中的国际化消息；每次响应使用当前语言的局部消息。客户端使用 `isOk()` 和 `getMsg()`。
- 更新文档、架构图及 POM 中的旧接口引用；历史分支同步修复仍使用无参异常处理器构造器的测试。

## 验证结论

- master：19 个受影响模块及依赖 reactor 编译通过；5 项新增回归测试通过。
- 其他 29 个分支：5 个分支的定向构建/测试通过；24 个被原有 POM 或离线依赖阻塞。未将静态扫描通过标记为完整构建通过。
- 含 master 共 30 个准备完成的源码版本均未发现旧 Java 响应 API 引用，未发现 record 声明；master 的扫描器 5 项自测通过。
- JSON 使用 Spring MVC/RestClient 默认的 Jackson 消息转换器验证成功与失败响应往返，核对 code/msg/data。原生严格 ObjectMapper 会拒绝 R 序列化产生的只读 success 等属性；该行为来自上游 R，本次没有修改上游仓库。
- Git diff 空白检查通过。

## 同步状态

共识别 30 个原有分支名（包含远程跟踪中独有的 fix 分支及 backup 分支）。29 个本地分支已更新；1 个被外部 worktree 占用的分支保存为独立待同步分支。

远程 GitHub DNS 解析失败，未能刷新远程引用，未推送；基线取本机已有引用。仅在原本地提交为远程缓存提交祖先时采用较新缓存基线，没有重写或丢弃原有提交。

| 分支 | 修复提交 | 本地状态 | 定向构建/测试 |
| --- | --- | --- | --- |
| master | f0e928c22 | 已更新 | 5 项通过；19 模块编译通过 |
| 2.3.x | fef05727d0cb | 已更新 | 阻塞，见下表 |
| 2.4.x | 2c50cf8c6d5c | 已更新 | 阻塞，见下表 |
| 2.5.x | 77e9cc9bc36b | 已更新 | 阻塞，见下表 |
| 2.6.x | 690408b5fa65 | 已更新 | 阻塞，见下表 |
| 2.7.x | 5ee4dbe98134 | 已更新 | 阻塞，见下表 |
| 3.0.x | 0a75ef6fe586 | 已更新 | 阻塞，见下表 |
| 3.1.x | b149b32d188e | 已更新 | 阻塞，见下表 |
| 3.2.x | 8a44fc24ef46 | 已更新 | 阻塞，见下表 |
| 3.3.x | c3fb317d801c | 已更新 | 阻塞，见下表 |
| 3.4.x | 5e50718c4159 | 已更新 | 通过 |
| 3.5.x | 9c00d8635c3b | 已更新 | 阻塞，见下表 |
| 4.0.x | 4035afebae0e | 已更新 | 阻塞，见下表 |
| 4.1.x | d9abc9449d65 | 已更新 | 阻塞，见下表 |
| backup/2.3.x-before-origin-alignment-20260831 | ff584a7b076b | 已更新 | 阻塞，见下表 |
| feature/2.7.x-20260128 | 227241afb42b | 已更新 | 阻塞，见下表 |
| feature/2.7.x-module-restructure | 8f2c953e162c | 已更新 | 阻塞，见下表 |
| feature/REQ-20261015-wandl-dependency-ci | 0ba6de7bae70 | 已保存待同步提交，原指针未移动 | 通过 |
| fix/2-3-x-spring-biz-version-20261031 | caa9fb08275b | 已更新 | 阻塞，见下表 |
| fix/2-4-x-javadoc-20261031 | 307544fbe8cb | 已更新 | 阻塞，见下表 |
| fix/2-5-x-spring-biz-version-20261031 | bb5af489c62e | 已更新 | 阻塞，见下表 |
| fix/2-6-x-spring-biz-version-20261031 | ea27d874b824 | 已更新 | 阻塞，见下表 |
| fix/2-7-x-spring-biz-version-20261031 | 6e755146347c | 已更新 | 阻塞，见下表 |
| fix/2.7.x-revision-shadow | 271260180aac | 已更新 | 阻塞，见下表 |
| fix/3-2-x-spring-biz-version-20261031 | 3aa6c3b9006a | 已更新 | 阻塞，见下表 |
| fix/3-3-x-spring-biz-version-20261031 | dcded189b8d2 | 已更新 | 阻塞，见下表 |
| fix/3-4-x-javadoc-20261031 | b64d86a3b6f2 | 已更新 | 通过 |
| fix/3-5-x-spring-biz-version-20261031 | dcc07c001fac | 已更新 | 阻塞，见下表 |
| fix/feature-REQ-20261015-wandl-dependency-ci-javadoc-20261031 | 05c448aca3cf | 已更新 | 通过 |
| fix/master-javadoc-20261031 | eb3b2a3557b9 | 已更新 | 通过 |

## 构建阻塞（未更改这些分支的版本或依赖来掩盖失败）

| 分支 | 实际阻塞 |
| --- | --- |
| 2.3.x | 离线缓存缺失：de.codecentric:spring-boot-admin-dependencies:pom:2.3.1 |
| 2.4.x | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:2.4.13 |
| 2.5.x | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:2.5.15 |
| 2.6.x | 离线缓存缺失：de.codecentric:spring-boot-admin-dependencies:pom:2.6.11 |
| 2.7.x | 原 POM 缺少 spring-biz 依赖版本 |
| 3.0.x | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:3.0.13 |
| 3.1.x | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:3.1.12 |
| 3.2.x | 原 POM 缺少 spring-biz 依赖版本 |
| 3.3.x | 原 POM 缺少 spring-biz 依赖版本 |
| 3.5.x | 原 POM 缺少 spring-biz 依赖版本 |
| 4.0.x | 原根 POM 含 Maven 3 不识别的 subprojects 标签 |
| 4.1.x | 原根 POM 含 Maven 3 不识别的 subprojects 标签 |
| backup/2.3.x-before-origin-alignment-20260831 | 离线缓存缺失：org.springframework.security:spring-security-bom:pom:6.4.5 |
| feature/2.7.x-20260128 | 离线缓存缺失：com.github.hiwepy:docx4j-template-bom:pom:1.0.1.RELEASE |
| feature/2.7.x-module-restructure | 离线缓存缺失：com.github.hiwepy:docx4j-template-bom:pom:1.0.1.RELEASE |
| fix/2-3-x-spring-biz-version-20261031 | 离线缓存缺失：de.codecentric:spring-boot-admin-dependencies:pom:2.3.1 |
| fix/2-4-x-javadoc-20261031 | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:2.4.13 |
| fix/2-5-x-spring-biz-version-20261031 | 离线缓存缺失：org.springframework.boot:spring-boot-dependencies:pom:2.5.15 |
| fix/2-6-x-spring-biz-version-20261031 | 离线缓存缺失：de.codecentric:spring-boot-admin-dependencies:pom:2.6.11 |
| fix/2-7-x-spring-biz-version-20261031 | Failed to execute goal on project ddd4j-boot-data-mybatis: Could not resolve dependencies for project io.ddd4j.boot:ddd4j-boot-data-mybatis:jar:2.7.x.20260630-SNAPSHOT |
| fix/2.7.x-revision-shadow | 离线缓存缺失：com.github.hiwepy:docx4j-template-bom:pom:1.0.1.RELEASE |
| fix/3-2-x-spring-biz-version-20261031 | Failed to execute goal on project ddd4j-boot-dependencies: Could not resolve dependencies for project io.ddd4j.boot:ddd4j-boot-dependencies:pom:3.2.x.20260630-SNAPSHOT |
| fix/3-3-x-spring-biz-version-20261031 | Failed to execute goal on project ddd4j-boot-data-mybatis: Could not resolve dependencies for project io.ddd4j.boot:ddd4j-boot-data-mybatis:jar:3.3.x.20260630-SNAPSHOT |
| fix/3-5-x-spring-biz-version-20261031 | Failed to execute goal on project ddd4j-boot: Could not resolve dependencies for project io.ddd4j.boot:ddd4j-boot:pom:3.5.x.20260630-SNAPSHOT |

## 待同步的外部 worktree 分支

- 原分支：`feature/REQ-20261015-wandl-dependency-ci`。
- 检出目录：`/Users/wandl/.codex/ci-repair-evidence/2026-10-14/historical-coordinates/boot34-feature`，位于本次文件写入授权范围外，未修改其文件、索引或原分支指针。
- 已准备并通过 5 项定向回归测试的提交：`0ba6de7bae705758da06b27eeef4be61c5cfe9c8`。
- 持久保存的本地分支：`sync/feature_REQ-20261015-wandl-dependency-ci-unified-api`。
- 后续在该 worktree 可写且工作区适合更新时执行：

```sh
git -C /Users/wandl/.codex/ci-repair-evidence/2026-10-14/historical-coordinates/boot34-feature merge --ff-only sync/feature_REQ-20261015-wandl-dependency-ci-unified-api
```

## 工作区保留项

验证期间 master 索引出现非本任务的删除：`ddd4j-boot-data/src/main/resources/scripts/redis-snowflake-batch.lua`。未恢复、未纳入本次提交。
