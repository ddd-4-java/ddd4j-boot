# Auth 集成设计

- 日期：2026-08-12
- 状态：已完成
- 作者：AI 代理
- 涉及模块：`ddd4j-boot-auth/ddd4j-boot-auth-satoken`、`ddd4j-boot-auth/ddd4j-boot-auth-security`、`ddd4j-boot-auth/ddd4j-boot-auth-shiro`、`ddd4j-boot-auth/ddd4j-boot-auth-license`

## 1. 目标与范围

分别条件装配 Spring Security、Sa-Token、Shiro 的 `SubjectProvider`/`SubjectDataProvider`，保证同一上下文不会隐式产生多个默认 Provider。注册到统一 `SpiKeys`，覆盖登录态、权限、关闭清理及用户自定义 Bean backoff 测试。

## 2. 总体架构

```
ddd4j-boot-auth/
  ddd4j-boot-auth-satoken/   → SaTokenEnhanceAutoConfiguration
  ddd4j-boot-auth-security/  → SecurityEnhanceAutoConfiguration
  ddd4j-boot-auth-shiro/     → ShiroEnhanceAutoConfiguration
  ddd4j-boot-auth-license/   → DefaultLicenseAutoConfiguration
```

每个模块独立 `@AutoConfiguration`，通过 `@ConditionalOnClass` 判断 classpath 中是否存在对应框架。

## 3. 模块结构

| 模块 | 自动配置类 | 测试 |
|------|-----------|------|
| auth-satoken | `SaTokenEnhanceAutoConfiguration` | `SaTokenEnhanceAutoConfigurationTest` |
| auth-security | `SecurityEnhanceAutoConfiguration` | `SecurityEnhanceAutoConfigurationTest` |
| auth-shiro | `ShiroEnhanceAutoConfiguration` | `ShiroEnhanceAutoConfigurationTest` |
| auth-license | `DefaultLicenseAutoConfiguration` | `DefaultLicenseAutoConfigurationTest` |

## 4. 核心抽象

| 类 | 职责 |
|----|------|
| `SaTokenEnhanceAutoConfiguration` | Sa-Token SubjectProvider 注册、StpKit 扩展 |
| `SecurityEnhanceAutoConfiguration` | Spring Security SubjectProvider 注册 |
| `ShiroEnhanceAutoConfiguration` | Shiro SubjectProvider 注册 |
| `DefaultLicenseAutoConfiguration` | License 校验安装 |

## 5. 配置结构

| 前缀 | 属性 | 默认值 | 说明 |
|------|------|--------|------|
| `license.*` | - | - | License 配置（subject/alias/storePass/路径） |

## 6. 测试策略

- ApplicationContextRunner：默认装配、缺类回退、用户覆盖、关闭无残留
- 验证同一上下文不会隐式产生多个默认 Provider

## 7. 文档与示例

- `docs/guides/auth-satoken.md`

## 8. 实施风险与缓解

| 风险 | 缓解 |
|------|------|
| 多个 Provider 冲突 | @ConditionalOnClass + @ConditionalOnMissingBean 保证唯一 |

## 9. 交付物清单

- [x] 4 个 Auth 模块自动配置
- [x] 4 个契约测试

## 10. 未决事项

- 无
