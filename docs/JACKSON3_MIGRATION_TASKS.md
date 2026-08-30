# Jackson 3.x 迁移阻塞清单与任务计划

**触发 commit**：c044a8c8（fix(deps): align Jackson BOM to tools.jackson:3.2.1）
**状态**：BOM 与 import 已切换到 `tools.jackson.*`；运行时编译被以下阻塞
**生成日期**：2026-08-31

---

## 已完成的替换

### Java import（13 文件，16 处）

| 文件 | 替换内容 |
|------|----------|
| `ddd4j-boot-web/ddd4j-boot-web-webflux/.../Ddd4jWebFluxAutoConfiguration.java` | `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.ObjectMapper` |
| `ddd4j-boot-web/ddd4j-boot-web-webflux/.../Ddd4jWebFluxAutoConfigurationTest.java` | 同上 |
| `ddd4j-boot-data/ddd4j-boot-data-crypto/.../Ddd4jCryptoAutoConfiguration.java` | 同上 |
| `ddd4j-boot-data/.../Ddd4jCryptoAutoConfiguration.java` | 同上 |
| `ddd4j-boot-data/.../DecryptRequestBodyAdvice.java` | 同上 |
| `ddd4j-boot-data/.../EncryptResponseBodyAdvice.java` | 同上 |
| `ddd4j-boot-samples/ddd4j-boot-sample-order/.../OrderPostgresInfrastructureConfiguration.java` | 同上 |
| `ddd4j-boot-samples/ddd4j-boot-sample-order/.../OrderPostgresInfrastructureConfigurationTest.java` | 同上 |
| `ddd4j-boot-extensions/ddd4j-boot-extension-jackson/.../DefaultJacksonAutoConfiguration.java` | `com.fasterxml.jackson.databind.{ObjectMapper,MapperFeature}` → `tools.jackson.databind.*`；`com.fasterxml.jackson.datatype.jsr310.ser.*` → `tools.jackson.databind.ext.javatime.ser.*` |
| `ddd4j-boot-extensions/ddd4j-boot-extension-jackson/.../DefaultJacksonAutoConfigurationTest.java` | `com.fasterxml.jackson.databind.ObjectMapper` → `tools.jackson.databind.ObjectMapper` |
| `ddd4j-boot-extensions/ddd4j-boot-extension-qrcode/.../QrCodeControllerTest.java` | `com.fasterxml.jackson.databind.{JsonNode,ObjectMapper}` → `tools.jackson.databind.*` |

注：`User.java` 中的两处 "com.fasterxml.jackson" 字符串是注释文案，**不是 import**，无需替换。

### pom 依赖（9 处 + 父 pom 2 处原有）

| pom 文件 | 行号 | 原 groupId | 新 groupId |
|---------|------|-----------|-----------|
| `ddd4j-boot-extensions/ddd4j-boot-extension-jackson/pom.xml` | 45 | `com.fasterxml.jackson.core` (jackson-databind) | `tools.jackson.core` |
| 同上 | 48 | `com.fasterxml.jackson.datatype` (jackson-datatype-jdk8) | `tools.jackson.datatype` |
| 同上 | 52 | `com.fasterxml.jackson.datatype` (jackson-datatype-jsr310) | `tools.jackson.datatype` |
| 同上 | 56 | `com.fasterxml.jackson.module` (jackson-module-parameter-names) | `tools.jackson.module` |
| 同上 | 60 | `com.fasterxml.jackson.core` (jackson-annotations) | `tools.jackson.core` |
| `ddd4j-boot-extensions/ddd4j-boot-extension-qrcode/pom.xml` | 42 | `com.fasterxml.jackson.core` | `tools.jackson.core` |
| `ddd4j-boot-samples/ddd4j-boot-sample-client/pom.xml` | 45 | `com.fasterxml.jackson.core` | `tools.jackson.core` |
| `ddd4j-boot-dependencies/pom.xml` | 422 | `com.fasterxml.jackson.core` (jackson-databind) | `tools.jackson.core` |
| 同上 | 732 | `com.fasterxml.jackson.core` (jackson-databind) | `tools.jackson.core` |

并修复了 `ddd4j-boot-extension-jackson/pom.xml` 中 5 个 jackson 依赖缺失版本号的问题（`${jackson-bom.version}`）。

`ddd4j-boot-dependencies/pom.xml:188` 的 jackson-bom import 已切到 `tools.jackson:jackson-bom:3.2.1`。

---

## 阻塞 API 与运行时问题清单

### 阻塞 #1：Spring Boot Jackson2 API 兼容

| 文件 | 行 | 阻塞符号 | 说明 |
|------|---|---------|------|
| `DefaultJacksonAutoConfiguration.java` | 15, 21, 39, 57, 70 | `Jackson2ObjectMapperBuilder`, `Jackson2ObjectMapperBuilderCustomizer` | Spring Boot 3.4.x 的 Jackson 自动装配基于 Jackson 2.x。`Jackson3ObjectMapperBuilder` 仅在 Spring Boot 3.5+ 才有。**Spring Boot 必须先升级到 3.5+** |
| `DefaultJacksonAutoConfiguration.java` | 16 | `JacksonAutoConfiguration` | 同上，需 Spring Boot 3.5+ |

### 阻塞 #2：Jackson 3.x API 删除/重构

| 文件 | 行 | 阻塞符号 | 说明 |
|------|---|---------|------|
| `DefaultJacksonAutoConfiguration.java` | 80 | `objectMapper.getSerializerFactory()` | Jackson 3.x **删除**此方法 |
| `DefaultJacksonAutoConfiguration.java` | 80 | `objectMapper.setSerializerFactory(...)` | Jackson 3.x **删除**此方法 |
| `DefaultJacksonAutoConfiguration.java` | 62 | `MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS` | Jackson 3.x 仍存在此常量（已验证），但语义调整，需人工 review |

**Jackson 3.x 配置模式变更**：原 `objectMapper.setSerializerFactory(factory.withSerializerModifier(modifier))` 改为 `JsonMapper.builder().addModule(new SimpleModule().setSerializerModifier(modifier)).build()`。

### 阻塞 #3：第三方 easy4j 扩展不兼容 Jackson 3

| 依赖 | 当前版本 | 状态 |
|------|---------|------|
| `io.github.easy4j:jackson-extension:3.0.x.SNAPSHOT` | 不可用 | 本地仓库不存在；离线无法下载；API 仍基于 Jackson 2.x 的 `JavaTimeModule`、`MyBeanSerializerModifier` |

Jackson 3.x 已内置 `tools.jackson.databind.JacksonModule` + `tools.jackson.databind.module.SimpleModule`，**easy4j 整个模块需要重写**（或换用 jackson-modules-java8 或类似 Jackson 3.x 原生库）。

### 阻塞 #4：jackson-datatype-jsr310 包路径变更（已修复）

| 原 2.x | 新 3.x | 状态 |
|--------|--------|------|
| `com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer` | `tools.jackson.databind.ext.javatime.ser.LocalDateSerializer` | ✅ 已修复（`DefaultJacksonAutoConfiguration.java:6-8`） |

注：Jackson 3.x 把 jsr310 支持并入 databind 主 artifact，独立的 `jackson-datatype-jsr310` jar 在 3.x 中仍存在但为兼容产物。

### 阻塞 #5：ddd4j-boot 既有项目缺陷（与 Jackson 无关）

`ddd4j-boot-dependencies/pom.xml` 的 BOM 缺失以下依赖版本声明：

| 文件 | 行 | 缺失依赖 |
|------|---|----------|
| `ddd4j-boot-auth/ddd4j-boot-auth-license/pom.xml` | 26 | `de.schlichtherle.truelicense:truelicense-core` |
| 同上 | 30 | `de.schlichtherle.truelicense:truelicense-xml` |
| `ddd4j-boot-extensions/ddd4j-boot-extension-qrcode/pom.xml` | 24 | `io.github.hiwepy:zxing-extension` |
| `ddd4j-boot-samples/ddd4j-boot-sample-starter-druid/pom.xml` | 162 | `com.baomidou:mybatis-plus-spring` |
| `ddd4j-boot-samples/ddd4j-boot-sample-starter-r2dbc-webflux/pom.xml` | 237 | `io.github.resilience4j:resilience4j-spring-boot2` |

这些缺失导致即使 Jackson 完美切换，整个 ddd4j-boot 仍无法编译。

### 阻塞 #6：本地仓库缺 Jackson 3.x 完整 jar（仅本地环境问题）

- `tools.jackson.datatype:jackson-datatype-jdk8:3.2.1` 本地无
- `tools.jackson.datatype:jackson-datatype-jsr310:3.2.1` 本地无
- `tools.jackson.module:jackson-module-parameter-names:3.2.1` 本地无
- `tools.jackson.core:jackson-annotations:3.2.1` 本地无
- `io.github.easy4j:jackson-extension:3.0.x.SNAPSHOT` 本地无

**CI/在线环境**应能下载这些 jar；**当前离线环境**无法验证。生产 CI 必须能访问 maven 仓库。

---

## 迁移任务清单（按执行顺序）

### P0（编译阻断）

- [ ] **T1**：升级 Spring Boot 到 3.5+ 以获得 `Jackson3ObjectMapperBuilder` 支持（阻塞 #1）
- [ ] **T2**：在 `DefaultJacksonAutoConfiguration` 中重写 `setSerializerFactory/getSerializerFactory` 为 Jackson 3.x 的 `JsonMapper.builder().addModule(new SimpleModule().setSerializerModifier(...))` 模式（阻塞 #2）
- [ ] **T3**：解除 `ddd4j-boot-dependencies/pom.xml` 中 5 个缺失版本依赖（阻塞 #5）
- [ ] **T4**：决定 `easy4j:jackson-extension` 升级或替换方案（阻塞 #3）

### P1（运行时验证）

- [ ] **T5**：在 ddd4j-boot-data 集成测试中验证 encrypt/decrypt advice 行为（替换了 6 处 import）
- [ ] **T6**：在 ddd4j-boot-web-webflux 集成测试中验证 Jackson 序列化
- [ ] **T7**：在 ddd4j-boot-extension-qrcode 集成测试中验证 QR 编码响应序列化

### P2（迁移文档与兼容性矩阵）

- [ ] **T8**：生成 Jackson 2 → 3 的 API 映射表（MapperFeature、SerializationFeature、JsonInclude 等）
- [ ] **T9**：在 README 中标注 ddd4j-boot 3.x 仅支持 Jackson 3.x；下层 ddd4j-core 3.x 同要求

---

## 验证证据（本轮已完成）

| 命令 | 结果 |
|------|------|
| `grep -rn "com\.fasterxml\.jackson" --include="*.java"` | 0 处残留（仅 User.java 注释中 2 处保留） |
| `grep -rn "com\.fasterxml\.jackson" --include="*.xml"` | 0 处残留 |
| `grep -rn "^import tools\.jackson" --include="*.java"` | 16 处新增 |
| `grep -rn "tools\.jackson" --include="*.xml"` | 11 处新增 |
| 直接 javac（绕开依赖解析）`DefaultJacksonAutoConfiguration.java` | 40 个错误，逐条对应阻塞 #1/#2/#3 |

**JVM 验证命令**（CI 环境运行）：
```bash
/Users/wandl/tools/apache-maven-4.0.0-rc-6/bin/mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-jackson compile
```
预期：完成 T1-T4 后通过编译；当前失败原因是阻塞 #1 + 阻塞 #3（easy4j snapshot 不可用）。
