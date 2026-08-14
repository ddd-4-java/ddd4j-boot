# 生产就绪测试加固实施计划（热点测试 + redis-stream/pulsar 集成测试）

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补齐 3 个无测试生产热点（`QrCodeController` / `ExcelHttpKit` / `RuleService.update`）的功能测试，并为 redis-stream 与 pulsar 两个 broker 添加真实端到端 Testcontainers 集成测试，使 MQ broker 真实环境覆盖从 3 个提升到 5 个。

**Architecture:** 全部工作只新增 `src/test/**` 与模块 pom 的 test scope 依赖，不改任何 `src/main` 代码。功能测试用最小依赖方案（standalone MockMvc / 纯 JUnit5 / 扩展现有测试类）；集成测试完全沿用 RocketMQ IT 的 `GenericContainer` 模式，避开 Testcontainers 双坐标版本地雷。

**Tech Stack:** Java 17、Spring Boot 3.4.13、JUnit 5、AssertJ、MockMvc（spring-test）、EasyExcel round-trip、Testcontainers 1.20.6（旧 id 坐标 `org.testcontainers:testcontainers` + `org.testcontainers:junit-jupiter`）、`redis:7.4-alpine`、`apachepulsar/pulsar:3.2.0`。

## Global Constraints

- 只允许新增/修改 `src/test/**` 与模块 pom 的 test scope 依赖；**禁止改动任何 `src/main` 代码**。
- 新 Testcontainers 依赖一律用旧 id 坐标（`org.testcontainers:testcontainers` / `org.testcontainers:junit-jupiter`），**禁止引入 `testcontainers-pulsar` 等新 id**——上游 ddd4j-dependencies 的 testcontainers-bom 2.0.5 与 Spring Boot BOM 管理的 1.20.6 混用会冲突。
- IT 沿用兄弟测试风格：`@Testcontainers(disabledWithoutDocker = true)` + `ApplicationContextRunner` + public static listener/event 类（上游反射 invoke 无 setAccessible，包级私有类会 IllegalAccessException）+ `MQEvent` 子类**不覆写 `match()`**（默认 `*`，覆写会被 `supports=["*"]` 精确匹配静默跳过）。
- 测试失败必须修复到真实通过，禁止 `@Disabled`/伪跳过（无 Docker 时 `disabledWithoutDocker` 自动跳过除外）。
- Runner 必须显式加入 `ConfigurationPropertiesAutoConfiguration.class`，否则 broker POJO 的 `@ConfigurationProperties` 绑定不生效（Kafka IT 踩过的坑）。
- 每切片独立提交，智能体完成后报告测试运行证据（`Tests run:` 统计行）。

**范围外（用户确认）**：幽灵 data 模块清理（P1）、hiwepy 样例清理（P4）另立计划。

---

## Task 1: QrCodeController 端点测试（ddd4j-boot-extension-qrcode）

- [ ] **Step 1.1: 前置检查 jackson-databind**
  - 确认模块测试 classpath 是否有 jackson-databind（render/batch 的 JSON 请求体需要）
  - 若缺失：pom 加 `test` scope `com.fasterxml.jackson.core:jackson-databind`（版本走依赖管理，不写死）
- [ ] **Step 1.2: 新增 `QrCodeControllerTest.java`**
  - 方式：standalone MockMvc——`MockMvcBuilders.standaloneSetup(new QrCodeController(service, props)).setControllerAdvice(new QrCodeExceptionHandler()).build()`；`service` 用真实 `new DefaultQrCodeService()`（AutoCloseable，try-with-resources 关闭）
  - 用例 1：`POST /qrcodes/render` JSON（content/width/height）→ 200 + `image/png` + body 非空
  - 用例 2：`POST /qrcodes/batch` 2 项（1 有效 + 1 空 content）→ 成功项 dataUri 以 `data:image/png;base64,` 开头；失败项 `success=false` 带 `QRCODE_RENDER_FAILED`
  - 用例 3：`POST /qrcodes/decode` multipart round-trip——先用 render 产物构造 `MockMultipartFile` → 解码回原文
  - 用例 4：decode 超限（把 `QrCodeProperties.maxUploadBytes` 调小）→ 400 `QRCODE_INVALID_ARGUMENT`
- [ ] **Step 1.3: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-qrcode test` 全绿

## Task 2: ExcelHttpKit 单元测试（ddd4j-boot-extension-excel）

- [ ] **Step 2.1: 新增 `ExcelHttpKitTest.java`（纯 JUnit5，无 Spring）**
  - 用例 1：`download(MockHttpServletResponse, "订单.xlsx", bytes)` → Content-Type 为 xlsx MIME、`Content-Disposition` RFC 5987 编码（`utf-8''%E8%AE%A2%E5%8D%95.xlsx`）、`Access-Control-Expose-Headers` 存在、响应字节数一致
  - 用例 2：`write(response, "users.xlsx", UserVO.class, List.of(...))` 端到端——定义 `@ExcelProperty` DTO 写出，再用上游 `ExcelKit.importExcel` 读回断言 round-trip
  - 用例 3：`upload`/`validate`——合法文件 round-trip 返回行数据；超大（maxMB 调小）与坏扩展名 → `BizRuntimeException`（`excel.upload.too.large` / `excel.upload.invalid.extension`）；垃圾字节 upload → `ImportResult.empty()`（异常被吞）
- [ ] **Step 2.2: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-excel test` 全绿

## Task 3: RuleServiceTest.update 守卫分支（ddd4j-boot-extension-qlexpress）

- [ ] **Step 3.1: 扩展现有 `RuleServiceTest.java`**（沿用 InMemoryRuleRepository/InMemoryRuleCache/lambda publisher 风格），新增测试覆盖 7 个分支：
  1. blank id → `IllegalArgumentException("id 不能为空")`
  2. 不存在 id → `"规则不存在: ..."` 且无事件发布
  3. changes 为 null → NPE
  4. 尝试改 code → `"规则编码不允许修改: ..."`
  5. 无效表达式（如 `"if ("`）→ `"规则表达式无效: ..."`
  6. 成功部分更新：enabled/priority 为 null 回落既有值、updatedAt 刷新、blank code 回填
  7. 事件与缓存：恰一个 `RuleChangedEvent`（`Operation.UPDATED`，断言 ruleId/ruleCode）+ `findByCode` 更新前后返回新旧表达式 + `clearCache()` 后仍能从 repository 读到新值
- [ ] **Step 3.2: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-qlexpress test` 全绿

## Task 4: RedisStreamMQClientIntegrationTest（ddd4j-boot-mq-redis-stream）

- [ ] **Step 4.1: pom 加 test 依赖**（旧 id 坐标，同 rocketmq pom）：`org.testcontainers:testcontainers` + `org.testcontainers:junit-jupiter`
- [ ] **Step 4.2: 新增 IT**
  - 容器：`GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))`，exposedPort 6379，`Wait.forListeningPort()`（redis 无官方 Testcontainers 模块）
  - 属性：`ddd4j.mq.enabled=true`、`ddd4j.mq.broker=redisStream`、`ddd4j.mq.redis-stream.url=redis://<host>:<mappedPort>`（凭据全在 URL）
  - 骨架：复制 RocketMQ IT——`AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class, Ddd4jMQAutoConfiguration.class, RedisStreamMQBootAutoConfiguration.class)` + public static listener/event + await 30s
  - 断言：`RedisStreamMQProperties.url` 绑定 + 发布→消费→orderId/topic/tag 三断言
  - 已知行为：consumer 自动建 stream + group；stream key `order:created`（concat `:`）
- [ ] **Step 4.3: IT 真实跑通**（本机有 Docker）：日志可见 `Publish MQ` + `Consume MQ`，`Tests run: 1, Failures: 0, Errors: 0`

## Task 5: PulsarMQClientIntegrationTest（ddd4j-boot-mq-pulsar）

- [ ] **Step 5.1: pom 加 test 依赖**（同 Task 4）
- [ ] **Step 5.2: 新增 IT**
  - 容器：`GenericContainer<>(DockerImageName.parse("apachepulsar/pulsar:3.2.0"))`，command `bin/pulsar standalone`，exposedPort 6650，`Wait.forLogMessage(".*PulsarService started.*", 1)`（官方 Testcontainers 模块只在新 2.x id 存在，必须走 GenericContainer）
  - 属性：`ddd4j.mq.enabled=true`、`ddd4j.mq.broker=pulsar`、**`ddd4j.mq.namespace=default`**（namespace 默认空串会拼出 `public//order` 非法 topic）、`ddd4j.mq.pulsar.service-url=pulsar://<host>:<mappedPort>`（standalone 默认允许自动建 topic）
  - await 超时放宽到 60s（standalone 启动 30-40s）
  - 注意：boot 侧 `PulsarMQClient` bean 无 destroyMethod——若 JVM 因 Pulsar 客户端非守护线程挂起，在 runner.run 末尾显式取 bean 调 `close()`
- [ ] **Step 5.3: IT 真实跑通**：同 Task 4.3 验收标准

## Task 6: 收尾

- [ ] **Step 6.1: 全量 `mvn verify`（EXIT=0）**
- [ ] **Step 6.2: 更新本计划勾选 + 附提交 SHA；更新 `2026-08-12-mq-broker-coverage-expansion-design.md`（redis-stream/pulsar 标记已覆盖）；主计划 Task 1 追加进度注记（3 → 5 broker）**
- [ ] **Step 6.3: 推送 `github/3.4.x` 与 `origin/3.4.x`**

---

## 完成校验

- 3 个热点模块测试全绿，无 src/main 改动（git diff 验证）
- 2 个新 IT 在有 Docker 环境真实跑通发布→消费链路；无 Docker 时显式跳过
- 全量 verify 通过；broker IT 覆盖 3 → 5（kafka/rabbit/rocket + redisStream/pulsar）
- 提交序列：`docs: 新增生产就绪测试加固计划` → `test: 补齐 QrCodeController/ExcelHttpKit/RuleService 热点测试` → `test: 为 redis-stream/pulsar broker 添加 Testcontainers 集成测试` → `docs: 更新 superpowers 计划进度与 broker 覆盖状态`
