> 2026-10-14 更新：以下内容保留为旧实现记录。当前执行契约、脚本和配置以 `docs/weekly-dependencies.md`、`.github/workflows/weekly-dependencies.yml`、`.github/dependency-policy.json` 与 `scripts/dependencies/check_dependencies.py` 为准；每个分支均保留 CI，读取目标分支策略。下文已退役的 Renovate/旧脚本路径不再作为运行指引。

# 每周依赖维护实施任务

**Goal:** 每周覆盖全部 GitHub 分支，支持固定/排除策略、最新稳定版本升级 PR 和独立漏洞门禁。

**规格事实源：** [设计规格](../specs/2026-10-14-weekly-dependencies-design.md)。不改变 Maven 业务 POM，不自动合并依赖更新，不切换分支，不提交推送。

- [x] 实现分页分支发现、中央策略和 fail-fast=false 扫描矩阵。
- [x] 实现并测试策略生成、最新版本默认行为及 Maven 4.1 提取兼容。
- [x] 增加 Dependency-Check 门禁、缓存、报告及手动 dry_run。
- [x] 完成操作指南和本地脚本测试、工作流静态检查。
- [ ] 将交付物提交推送到 GitHub 默认分支（尚未授权提交推送）。
- [ ] 配置所需 Secrets，实际运行 Renovate 配置验证与 dry_run。
- [ ] 验证全部分支扫描、报告、升级 PR 和对应 PR CI。未完成前不标记远端验收通过。
