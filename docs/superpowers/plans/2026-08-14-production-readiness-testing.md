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

- [x] **Step 1.1: 前置检查 jackson-databind**
  - 确认模块测试 classpath 是否有 jackson-databind（render/batch 的 JSON 请求体需要）
  - 若缺失：pom 加 `test` scope `com.fasterxml.jackson.core:jackson-databind`（版本走依赖管理，不写死）
- [x] **Step 1.2: 新增 `QrCodeControllerTest.java`**
  - 方式：standalone MockMvc——`MockMvcBuilders.standaloneSetup(new QrCodeController(service, props)).setControllerAdvice(new QrCodeExceptionHandler()).build()`；`service` 用真实 `new DefaultQrCodeService()`（AutoCloseable，try-with-resources 关闭）
  - 用例 1：`POST /qrcodes/render` JSON（content/width/height）→ 200 + `image/png` + body 非空
  - 用例 2：`POST /qrcodes/batch` 2 项（1 有效 + 1 空 content）→ 成功项 dataUri 以 `data:image/png;base64,` 开头；失败项 `success=false` 带 `QRCODE_RENDER_FAILED`
  - 用例 3：`POST /qrcodes/decode` multipart round-trip——先用 render 产物构造 `MockMultipartFile` → 解码回原文
  - 用例 4：decode 超限（把 `QrCodeProperties.maxUploadBytes` 调小）→ 400 `QRCODE_INVALID_ARGUMENT`
- [x] **Step 1.3: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-qrcode test` 全绿

## Task 2: ExcelHttpKit 单元测试（ddd4j-boot-extension-excel）

- [x] **Step 2.1: 新增 `ExcelHttpKitTest.java`（纯 JUnit5，无 Spring）**
  - 用例 1：`download(MockHttpServletResponse, "订单.xlsx", bytes)` → Content-Type 为 xlsx MIME、`Content-Disposition` RFC 5987 编码（`utf-8''%E8%AE%A2%E5%8D%95.xlsx`）、`Access-Control-Expose-Headers` 存在、响应字节数一致
  - 用例 2：`write(response, "users.xlsx", UserVO.class, List.of(...))` 端到端——定义 `@ExcelProperty` DTO 写出，再用上游 `ExcelKit.importExcel` 读回断言 round-trip
  - 用例 3：`upload`/`validate`——合法文件 round-trip 返回行数据；超大（maxMB 调小）与坏扩展名 → `BizRuntimeException`（`excel.upload.too.large` / `excel.upload.invalid.extension`）；垃圾字节 upload → `ImportResult.empty()`（异常被吞）
- [x] **Step 2.2: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-excel test` 全绿

## Task 3: RuleServiceTest.update 守卫分支（ddd4j-boot-extension-qlexpress）

- [x] **Step 3.1: 扩展现有 `RuleServiceTest.java`**（沿用 InMemoryRuleRepository/InMemoryRuleCache/lambda publisher 风格），新增测试覆盖 7 个分支：
  1. blank id → `IllegalArgumentException("id 不能为空")`
  2. 不存在 id → `"规则不存在: ..."` 且无事件发布
  3. changes 为 null → NPE
  4. 尝试改 code → `"规则编码不允许修改: ..."`
  5. 无效表达式（如 `"if ("`）→ `"规则表达式无效: ..."`
  6. 成功部分更新：enabled/priority 为 null 回落既有值、updatedAt 刷新、blank code 回填
  7. 事件与缓存：恰一个 `RuleChangedEvent`（`Operation.UPDATED`，断言 ruleId/ruleCode）+ `findByCode` 更新前后返回新旧表达式 + `clearCache()` 后仍能从 repository 读到新值
- [x] **Step 3.2: 模块测试通过**
  - `mvn -pl ddd4j-boot-extensions/ddd4j-boot-extension-qlexpress test` 全绿

## Task 4: RedisStreamMQClientIntegrationTest（ddd4j-boot-mq-redis-stream）

- [x] **Step 4.1: pom 加 test 依赖**（旧 id 坐标，同 rocketmq pom）：`org.testcontainers:testcontainers` + `org.testcontainers:junit-jupiter`
- [x] **Step 4.2: 新增 IT**
  - 容器：`GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))`，exposedPort 6379，`Wait.forListeningPort()`（redis 无官方 Testcontainers 模块）
  - 属性：`ddd4j.mq.enabled=true`、`ddd4j.mq.broker=redisStream`、`ddd4j.mq.redis-stream.url=redis://<host>:<mappedPort>`（凭据全在 URL）
  - 骨架：复制 RocketMQ IT——`AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class, Ddd4jMQAutoConfiguration.class, RedisStreamMQBootAutoConfiguration.class)` + public static listener/event + await 30s
  - 断言：`RedisStreamMQProperties.url` 绑定 + 发布→消费→orderId/topic/tag 三断言
  - 已知行为：consumer 自动建 stream + group；stream key `order:created`（concat `:`）
- [x] **Step 4.3: IT 真实跑通**（本机有 Docker）：日志可见 `Publish MQ` + `Consume MQ`，`Tests run: 1, Failures: 0, Errors: 0`

## Task 5: PulsarMQClientIntegrationTest（ddd4j-boot-mq-pulsar）

- [x] **Step 5.1: pom 加 test 依赖**（同 Task 4）
- [x] **Step 5.2: 新增 IT**
  - 容器：`GenericContainer<>(DockerImageName.parse("apachepulsar/pulsar:3.2.0"))`，command `bin/pulsar standalone`，exposedPort 6650，`Wait.forLogMessage(".*PulsarService started.*", 1)`（官方 Testcontainers 模块只在新 2.x id 存在，必须走 GenericContainer）
  - 属性：`ddd4j.mq.enabled=true`、`ddd4j.mq.broker=pulsar`、**`ddd4j.mq.namespace=default`**（namespace 默认空串会拼出 `public//order` 非法 topic）、`ddd4j.mq.pulsar.service-url=pulsar://<host>:<mappedPort>`（standalone 默认允许自动建 topic）
  - await 超时放宽到 60s（standalone 启动 30-40s）
  - 注意：boot 侧 `PulsarMQClient` bean 无 destroyMethod——若 JVM 因 Pulsar 客户端非守护线程挂起，在 runner.run 末尾显式取 bean 调 `close()`
- [x] **Step 5.3: IT 真实跑通**：同 Task 4.3 验收标准

## Task 6: 收尾

- [x] **Step 6.1: 生产模块 verify（EXIT=0）**（42 模块全绿；全量被环境死构件阻塞，范围与根因见"执行记录"）
- [x] **Step 6.2: 文档更新**（规格表 + 上游问题记录 + 本执行记录）
- [x] **Step 6.3: 推送 `github/3.4.x` 与 `origin/3.4.x`**

---

## 完成校验

- 3 个热点模块测试全绿，无 src/main 改动（git diff 验证）
- 2 个新 IT 在有 Docker 环境真实跑通发布→消费链路；无 Docker 时显式跳过
- 全量 verify 通过；broker IT 覆盖 3 → 5（kafka/rabbit/rocket + redisStream/pulsar）
- 提交序列：`docs: 新增生产就绪测试加固计划` → `test: 补齐 QrCodeController/ExcelHttpKit/RuleService 热点测试` → `test: 为 redis-stream/pulsar broker 添加 Testcontainers 集成测试` → `docs: 更新 superpowers 计划进度与 broker 覆盖状态`

---

## 执行记录（2026-08-14）

### 完成提交

| 切片 | 提交 | 内容 | 测试证据 |
|---|---|---|---|
| 0 | `a440daa2` | 本计划文件 | — |
| B（Task 1-3） | `92c96bb9` | QrCodeController 6 用例 + ExcelHttpKit 6 用例 + RuleService.update 7 分支 | qrcode 12/12、excel 9/9、qlexpress 12/12 全绿 |
| C（Task 4-5） | `f68974c0` | RedisStreamMQClientIntegrationTest + PulsarMQClientIntegrationTest | 5 broker IT 全部真实消费（见下） |

### Task 6（收尾）结果

- **生产模块 verify 全绿（EXIT=0）**：42 个模块（全部生产模块，`-pl` 显式列表 + `-U`）。
  证据：Kafka `Consume MQ [_order_created]`(O-001)、RabbitMQ `[.order.created]`(R-001)、
  RocketMQ `[.order.created]`(R-001)、Redis Stream `[:order:created]`(R-001)、
  Pulsar `[.order]`(P-001)——五条真实消费日志，`Tests run: 1, Failures: 0, Errors: 0, Skipped: 0` × 5。
- **范围外排除（环境阻塞，非本次改动引起，详见"环境取证"）**：
  - `ddd4j-boot-data-external` + 全部 `ddd4j-boot-samples`（27 样例）：依赖已从私有仓库消失的
    `com.github.hiwepy:redistpl-plus-spring-boot-starter:2.3.x.20241003-SNAPSHOT` 等死构件
  - `ddd4j-boot-extension-jackson`：`dbd39263` 把 import 改为从未发布过的 `io.github.hiwepy.jackson.*` 包
    （远端 `io.github.hiwepy:jackson-extension:2.0.x.20260630-SNAPSHOT` 里实际是 `io.github.easy4j.jackson` 包）

### 执行偏差与发现

1. **QrCode batch 语义修正**：blank content 在 batch 中导致整批 400（`QrCodeRequest` 构造器在
   controller 循环内即抛出），并非计划预期的 per-item 失败；per-item 失败改用 blank itemId 触发，
   并另加用例固化 blank-content-400 行为。
2. **Pulsar 等待日志**：实际就绪日志是 `messaging service is ready`（非 `PulsarService started`）。
3. **Pulsar namespace 绑定路径**：`PulsarProperties` 以 `ddd4j.mq.pulsar` 为前缀，继承自
   `MQProperties` 的 namespace 随子类前缀绑定——必须设 `ddd4j.mq.pulsar.namespace=default`
   （全局 `ddd4j.mq.namespace` 无效），否则拼出非法 topic `public//order`。
4. **Pulsar topic 不对称（上游设计缺陷）**：见 broker-coverage 规格"已知上游问题"。
5. **Redis Stream 收尾竞态**：同上，外观问题不影响测试判定。

### 环境取证（阻塞全量 verify 的根因，均非本次改动引起）

1. **上游漂移**：切片 C 智能体按当时上游 HEAD（`613b8d5b`，含 `3ae1205e` web-core 拆 6 子包）
   全量 install，覆盖了 boot 依赖的旧扁平包 → 已从 `14082fde`（08-03 21:00，拆分前 + 含
   2 参 `DefaultQrCodeService` 构造 = 8 月 5 日绿色 verify 的实际状态）git archive 到 /tmp 重建
   恢复（补了该提交缺失的 monitor jakarta.annotation 依赖、排除上游 samples）。**注意：~/.m2
   上游 SNAPSHOT 现为 14082fde 状态；boot 适配 `3ae1205e+` 的拆包上游属新任务。**
2. **私有仓库死构件**：aliyun 私仓已无 `redistpl-plus:2.3.x.20241003-SNAPSHOT`（8 月 7 日
   `d8f09fa` 版本线对齐后旧快照消失，新版本亦未发布），且本地源码该提交缺 redistpl-core
   依赖声明无法重建。需用户重新发布或改 boot 版本钉。
3. **Maven 缓存毒害**：一次瞬时解析失败被 `.lastUpdated` 缓存后持续阻断（jackson-extension
   实际远端可解析，`-U` 即恢复）。
4. **Docker 中途崩溃**：verify 期间 Docker Desktop 守护进程掉线导致 IT 集体 skip；
   重启后五模块重跑全部真实执行。

---

## 第二轮优化记录（2026-08-16）：全量 verify 从"范围化"推进到"零排除"

### 成果

**全量 reactor `mvn verify` EXIT=0（零排除）**：70 个模块全部构建（含此前被环境阻塞的
`ddd4j-boot-data-external`、`ddd4j-boot-extension-jackson`、全部 27 个 samples），
五个 broker IT 真实消费链路齐全。

### 本轮修复

1. **extension-jackson 解锁**（仓库改动，见提交）：
   - pom 补 surefire `<skip>false</skip>` 覆盖（对齐 excel/qlexpress 模式，父 pom 默认 skipTests=true）
   - 契约测试修复：默认装配用例的 runner 补入 Boot 的 `JacksonAutoConfiguration`
     （`Jackson2ObjectMapperBuilder` 由其提供；`@AutoConfigureBefore` 下两配置同载才对齐真实应用装配）
2. **jackson-extension 本地重建**：boot 的 `dbd39263` import `io.github.hiwepy.jackson.*`，
   该包名从未发布（远端 jar 实为 `io.github.easy4j.jackson`）。从 jackson-extension 仓库
   `21486a7^`（Jackson 3 迁移前）git archive 草稿构建，包名/坐标机械重命名为 hiwepy 后安装。
3. **validation-mimetypes 复活**：从 starters 仓库 `0e9b79e^`（2.3.x.20250430 版本点）JDK 8
   构建（JDK 21 下旧 Lombok 注解处理失效），坐标改回 `com.github.hiwepy` 安装。
4. **redistpl shim**：源码该版本点缺 redistpl-core 依赖声明无法重建；boot 活代码实际只用
   `RedisOperationTemplate`（真正提供者 = spring-data-redis-extension）。制作空 jar + pom
   shim（`com.github.hiwepy:redistpl-plus-spring-boot-starter:2.3.x.20241003-SNAPSHOT`，
   传递 sdre 3.0.x + starter-data-redis 3.4.13）。
5. **构建环境隔离**：发现 IntelliJ Maven 守护进程（IDEA import）与 ~/.m2 状态互相踩踏
   （io.ddd4j SNAPSHOT 反复被翻新/毒害），且 aliyun 私仓 `ddd4j-dependencies` metadata
   指向已清除的时间戳文件（远端损坏）。建立 `/tmp/boot-repo` 私有仓库（14G 拷贝 + 上游
   14082fde 重装 + 上述自建构件 + 缺失第三方补拉 + 清 `_remote.repositories` 记账），
   全程 `mvn -o -Dmaven.repo.local=/tmp/boot-repo` 离线构建，彻底与 IDEA 解耦。

### 遗留（用户决策项）

- **aliyun 私仓 metadata 损坏**：`io.ddd4j:ddd4j-dependencies` SNAPSHOT metadata 指向已删除
  的 `20260807.180512-55`——任何在线 `-U` 构建都会失败。需重新 deploy 一次上游快照修复。
- **死构件需重新发布**：redistpl 20241003（或改 boot 版本钉指向可发布版本线）、
  jackson-extension 需按 hiwepy 包名正式发版（当前为本地重建 shim）。
- **上游适配任务**：boot 仍消费 web-core 拆包前（14082fde）的上游；适配 `3ae1205e+`
  拆包上游是独立计划。
