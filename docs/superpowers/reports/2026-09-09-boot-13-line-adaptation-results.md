# ddd4j-boot 13 条维护线适配结果

日期：2026-09-09。状态：Boot 侧本地提交与目标验证完成；尚未推送、发布或触发 GitHub Actions。

## 本地提交矩阵

| 分支 | Spring Boot | ddd4j | 本地提交 |
|---|---|---|---|
| `2.3.x` | `2.3.12.RELEASE` | `1.0.x` | `23778be96acb` |
| `2.4.x` | `2.4.13` | `1.0.x` | `34776f6adf83` |
| `2.5.x` | `2.5.15` | `1.0.x` | `5b04e57af471` |
| `2.6.x` | `2.6.15` | `1.0.x` | `5f6a4c3abb45` |
| `2.7.x` | `2.7.18` | `1.0.x` | `3933fbbc913e` |
| `3.0.x` | `3.0.13` | `2.0.x` | `3857a599ff85` |
| `3.1.x` | `3.1.12` | `2.0.x` | `d4ea46304f73` |
| `3.2.x` | `3.2.12` | `2.0.x` | `1338346af264` |
| `3.3.x` | `3.3.13` | `2.0.x` | `f69479c77807` |
| `3.4.x` | `3.4.13` | `2.0.x` | `4eddab7f9aae` |
| `3.5.x` | `3.5.16` | `2.0.x` | `568990352afe` |
| `4.0.x` | `4.0.7` | `3.0.x` | `2639b056090a` |
| `4.1.x` | `4.1.0` | `3.0.x` | `3ace373f2e64` |

上述适配提交相对各自 `github/<branch>` 均为 `ahead 1`、`behind 0`；`4.1.x` 在提交本报告后将为 `ahead 2`、`behind 0`。

## 已闭合契约

- 13 条线的 Boot 版本、制品 revision、ddd4j 版本和 Maven 模型与维护矩阵一致。
- Boot license 叶模块不再直接声明 `de.schlichtherle.truelicense:*`。
- 13 条线均使用六参数 `LicenseVerify`，并用测试验证 `license.signature-algorithm` 透传。
- license 叶模块已进入 auth 聚合器。
- 2.x 使用 JDK 8，3.x 使用 JDK 17；4.x 使用 JDK 21 + Maven 4.0.0-rc-6。
- 4.x 正式项目 POM 使用 Model 4.1.0 与 `<subprojects>`，没有 `<modules>`；测试夹具保留其他 JDK 线的 Model 4.0 示例。
- 4.x 显式覆盖历史 `skipTests=true`，目标测试实际执行 3 项，而非仅编译。
- 3.x/4.x 样例中的 Dozer 扩展坐标改为上游 BOM 管理的 `io.github.easy4j:dozer-extra-converters`。
- 所有 Verify/Deploy workflow 都声明 `workflow_dispatch` 并引用组织级 `MAVEN_SETTINGS_XML`；4.x Deploy 检出 Maven 4 发布桥脚本。

## 测试证据

- 2.3–2.7：每条线依赖边界 4 项、维护矩阵、license 自动配置 3 项和 JDK 8 域样例编译均通过。
- 3.0–3.5：每条线依赖边界 4 项、维护矩阵和 license 自动配置 3 项均通过。
- 4.0–4.1：每条线依赖边界 4 项、维护矩阵、Model 4.1 聚合标签审计和 license 自动配置 3 项均通过。
- 2.7 完整构建实测九类固定镜像消息往返；核心、Web、Auth、MQ、扩展和分层样例共 55/72 模块通过。

## 未完成与外部阻塞

- ddd4j `feature/1.0.x` 已本地提交 TrueLicense 旧坐标清理和 Java 8 日志基线修复：`962cb127`、`5c93c313`，相对 GitHub ahead 2。
- ddd4j `feature/2.0.x`、`feature/3.0.x` 仍管理两项旧 TrueLicense 坐标。两个分支被既有 worktree 占用；按项目禁用 worktree 的约束，本轮未使用或删除这些目录，也未修改对应分支。
- 2.7 完整样例 reactor 仍依赖三个仓库中不存在的 Easy4J Starter，详见 `2026-09-09-boot-2.7-adaptation-baseline.md`。
- 4.x Maven 4 目标测试通过，但有效模型会报告大量上游 Model 4.1 兼容性告警；不能把目标测试通过等同于整仓模型治理完成。
- 本轮未执行 push、deploy、空缓存消费或 Actions；这些仍是独立门禁。
