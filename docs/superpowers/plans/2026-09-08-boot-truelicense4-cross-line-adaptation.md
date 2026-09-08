# ddd4j-boot 与 ddd4j 跨版本适配实施计划

对应规格：`docs/superpowers/specs/2026-09-08-boot-truelicense4-cross-line-adaptation.md`

## 1. 固化上游 ddd4j 基线

- 审查 `feature/1.0.x` 当前 TrueLicense 4 在途差异及相关证据文档。
- 运行依赖矩阵、目标模块测试、运行时依赖树和差异检查。
- 提交 `feature/1.0.x`，随后顺序处理 `feature/2.0.x`、`feature/3.0.x` 的旧坐标清理与同等验证。
- 每条线提交前确认本地分支、跟踪分支和未提交文件归属。

## 2. 完成 Boot 2.7 基线

- 保留当前 2.7.x 依赖与样例兼容改动，确认没有 Spring 6.1 API 或 Boot 3 starter 泄漏。
- 以失败测试锁定 TrueLicense 新坐标、旧坐标移除和 `signatureAlgorithm` 透传。
- 运行自动配置、依赖边界、分支基线和目标 reactor 测试。
- 将本规格、计划与基线代码作为可传播提交提交到 2.7.x。

## 3. 顺序传播 2.x 维护线

处理顺序：`2.6.x`、`2.5.x`、`2.4.x`、`2.3.x`。

- 在干净工作区切换既有分支。
- 以 2.7.x 提交为参考，逐文件应用共同契约；只在同构文件上使用选择性提交传播。
- 保留每条 Spring Boot 版本和已有分支差异。
- 使用 JDK 8 / Maven 3.9.16 运行目标测试与矩阵检查。

## 4. 顺序传播 3.x 维护线

处理顺序：`3.0.x`、`3.1.x`、`3.2.x`、`3.3.x`、`3.4.x`、`3.5.x`。

- 传播 TrueLicense 共同契约和依赖边界测试。
- 保留 Spring 6、Jakarta 和 Boot 3 对应 starter/API；不传播 2.7 的 `RestTemplate` 降级或 Boot 2 MyBatis 坐标。
- 使用 JDK 17 / Maven 3.9.16 完成验证。

## 5. 顺序传播 4.x 维护线

处理顺序：`4.0.x`、`4.1.x`。

- 使用 ddd4j 3.0.x TrueLicense 4 API。
- 核验根 POM Model 4.1.0 与 `<subprojects>`，禁止 `<modules>` 回归。
- 使用 JDK 21 / Maven 4.0.0-rc-6 完成验证。

## 6. Testcontainers 与 CI 核验

- 对照 Testcontainers 官方模块目录审计现有九类容器；记录官方模块、GenericContainer 和镜像版本选择。
- 运行静态镜像/等待策略/生命周期检查；Docker 可用时执行真实往返集成测试。
- 核对 Verify 的 `workflow_dispatch`、Deploy 的 `MAVEN_SETTINGS_XML` 引用和 4.x 发布桥。

## 7. 收敛与发布证据

- 汇总 13 条线的版本矩阵、提交 SHA、目标测试、受影响回归、依赖树和 Testcontainers 结果。
- 提交和双远端推送分开验证；发布、空缓存消费和 Actions 作为独立状态。
- Actions 若受 GitHub Free Billing 阻塞，保留外部阻塞证据并改用已授权的本地发布命令，不将其描述为 CI 通过。

