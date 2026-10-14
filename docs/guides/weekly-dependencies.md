> 2026-10-14 更新：以下内容保留为旧实现记录。当前执行契约、脚本和配置以 `docs/weekly-dependencies.md`、`.github/workflows/weekly-dependencies.yml`、`.github/dependency-policy.json` 与 `scripts/dependencies/check_dependencies.py` 为准；每个分支均保留 CI，读取目标分支策略。下文已退役的 Renovate/旧脚本路径不再作为运行指引。

# 每周依赖检查与更新

工作流：`.github/workflows/dependency-weekly.yml`。每周一北京时间 **10:17** 执行（GitHub 调度可能延迟），也可以在 Actions 中手动运行。

工作流必须发布到 **GitHub 默认分支** 才会定期运行。每次通过分页 API 枚举当前全部远端分支，包含版本、feature、fix、backup 分支，自动排除自己的 `weekly-deps/` 更新分支。无需给历史分支逐个添加工作流。

## 配置升级规则

统一修改默认分支的 `.github/dependency-policy.json`。使用 Renovate `packageRules`，Maven 坐标为 `groupId:artifactId`，支持通配符；`matchBaseBranches` 限定分支，省略时适用全部分支。

现有默认规则保留各分支的 Spring、Jackson 和内部 ddd4j 版本。其他 Maven 依赖、BOM、插件查询最新稳定版本，允许跨大版本更新，创建目标分支的升级 PR。PR 不自动合并，合并前检查对应分支的 CI 和 JDK/API 兼容性。

例如向 `packageRules` 末尾追加如下配置（版本仅作规则示例，按对应分支基线填写）：

```json
{
  "description": "3.4.x 分支 Jackson BOM 精确版本约束",
  "matchPackageNames": ["com.fasterxml.jackson:jackson-bom"],
  "matchBaseBranches": ["3.4.x"],
  "enabled": true,
  "allowedVersions": "/^2\\.18\\.4$/"
}
```

这条规则会覆盖前面的 Jackson 排除规则，仅允许 `2.18.4`。固定版本依赖已处于该版本时不再更新；低于该版本时可提出升级。它不会自动强制降级已经超过固定值的依赖，也不会创造不存在的版本。协调版本优先固定 BOM，避免同一版本属性的多个组件互相冲突。

排除升级示例：

```json
{
  "matchPackageNames": ["org.springframework*:*", "com.example:legacy-client"],
  "enabled": false
}
```

排除只限制自动更新，**仍执行漏洞扫描**。共享属性优先用 BOM 或代表组件查询版本；Maven 4.1 的版本属性无法映射到坐标时发现阶段失败，以免漏检。传递依赖的升级通过上游 BOM/直接依赖推进，漏洞扫描覆盖实际解析的传递依赖。

## 仓库 Secrets

| Secret | 用途 |
|---|---|
| `RENOVATE_TOKEN` | 必需；专用机器人 PAT，限定本仓库，允许读取及写入 Contents、Pull requests、Issues，读取 Checks/Commit statuses，用于升级 PR 和 Dashboard。机器人创建的 PR 才能正常触发现有验证 CI。也可以配置 GitHub App token 生成步骤替代 PAT。 |
| `NVD_API_KEY` | 必需；NVD 数据库更新，缺失时明确失败。 |
| `MAVEN_SETTINGS_XML` | 需要私服时使用；沿用现有 CI 的 **base64 编码** Maven settings.xml。 |
| `RENOVATE_HOST_RULES` | 需要私服元数据认证时使用；JSON 数组形式的 Renovate hostRules。 |

私服 hostRules 的格式示例：

```json
[{"hostType":"maven","matchHost":"packages.aliyun.com","username":"BOT_USER","password":"BOT_PASSWORD"}]
```

凭据只放 Secrets。Maven settings 与 Renovate hostRules 是两套独立配置；设置前者不会自动为后者认证。若 Maven 4 自定义提取需从私服查新版本，可在策略中增加坐标规则的 `registryUrls`，列出 Maven Central 及所需私服 release/snapshot 地址。

## 漏洞门禁和验收

每个分支按自身 `java.version` 和 Maven Wrapper 构建 reactor，再用 Java 21 运行 OWASP Dependency-Check。直接、传递、测试和 provided 依赖均检查；CVSS >= 7 使 CI 失败，扫描或依赖解析失败也使 CI 失败。报告在 Actions artifacts 中保留 30 天，摘要记录分支、源码 SHA 和任务状态。一个分支失败不取消其他分支。关闭需要另一套认证的 OSS Index 分析器，使用 NVD/CISA 等默认数据源。

发布到默认分支并配置 Secrets 后，首次手动执行选择 `dry_run=true`。检查配置校验、全部分支结果和报告；dry_run 不生成升级 PR，但仍执行漏洞扫描。确认后正常运行，检查实际 PR 和对应 CI，再合并版本更新。最新版本及扫描通过都不能保证不存在未知漏洞。

本地检查（无需安装 Node/Python 依赖）：

```bash
node --test scripts/test_dependency_config.cjs
python3 -m unittest discover -s scripts -p 'test_*.py'
actionlint .github/workflows/dependency-weekly.yml
```

参考：[Renovate 配置](https://docs.renovatebot.com/configuration-options/)、[自托管 Action](https://github.com/renovatebot/github-action)、[Dependency-Check 参数](https://jeremylong.github.io/DependencyCheck/dependency-check-maven/aggregate-mojo.html)。
