# ddd4j-boot 跨 JDK 逻辑一致性最终报告

日期：2026-09-10。事实源：跨 JDK 逻辑一致性设计与实施计划。

## 结论

- 13 条维护线的机器矩阵、POM 版本、自动配置注册结构均通过现有一致性门禁。
- framework-only surface 门禁排除 `ddd4j-boot-samples`，比较公开 Java 类型/方法与 Spring
  configuration metadata；10 个同组比较全部通过。
- JDK 8 组每线 423 个 surface；JDK 17 和 JDK 21 组每线 421 个 surface。
- 4.1.x 的 Data/Cache/MQ 真实依赖矩阵执行 52 个测试，0 failures、0 errors、0 skipped。
- 2.3.x–2.7.x Core/Repository 契约分别为每线 6/6 与 5/5；3.0.x–3.5.x 和 4.0.x/4.1.x
  主能力 clean reactor 全部通过。
- 阶段六 Redistpl 解耦、样例修复和 13 线完整 `clean verify` reactor 均已完成。
- 13 线均从隔离 Maven 仓库消费阿里云上游制品；Boot 4.0 与 4.1 的全新仓库均确认消费
  `ddd4j-dependencies:3.0.x` 时间戳 `20260910.113831-20`。
- Task 20 独立审查已执行，Critical/Important 代码项已闭环并完成 13 线重验。
- Codeup URL 内嵌凭据已从本地 Git config 移除；当前无可用 Codeup SSH/Keychain 凭据，
  因此新提交仅已同步 GitHub，Task 20 远端对齐与 Task 21 仍为 `BLOCKED`。

## 同组比较

| 基线 | 候选 | JDK 组 | 结果 |
|---|---|---|---|
| 2.3.x | 2.4.x | jdk8 | PASS |
| 2.3.x | 2.5.x | jdk8 | PASS |
| 2.3.x | 2.6.x | jdk8 | PASS |
| 2.3.x | 2.7.x | jdk8 | PASS |
| 3.0.x | 3.1.x | jdk17 | PASS |
| 3.0.x | 3.2.x | jdk17 | PASS |
| 3.0.x | 3.3.x | jdk17 | PASS |
| 3.0.x | 3.4.x | jdk17 | PASS |
| 3.0.x | 3.5.x | jdk17 | PASS |
| 4.0.x | 4.1.x | jdk21 | PASS |

复现：

```bash
bash scripts/consistency/test-verify-same-group-compatibility.sh
bash scripts/consistency/verify_same_group_compatibility.sh
```

## 集成契约

`docs/superpowers/reports/ddd4j-boot-integration-contracts.json` 记录 4.1.x 的真实执行：

- MySQL 8.4：连接、建表、插入、查询和 MyBatis-Plus 拦截器装配；
- Caffeine：CacheKit 注册、读取、evict 和 unregister；
- ActiveMQ Artemis、Kafka、Mosquitto、NATS、Pulsar、RabbitMQ、Redis、RocketMQ、LocalStack SQS；
- 合计 52 tests，0 failures/errors/skips，状态 PASS。

Pulsar 测试在 Spring 上下文前按条件等待并创建 topic，避免 standalone namespace 初始化竞态。
RocketMQ 测试动态选择 broker/VIP 端口对，并使用 `listenPort` 同步容器监听和 namesrv 公告。

## 阶段六完整 reactor 证据

| 分支 | 工具链 | reactor | `clean verify` | SHA |
|---|---|---:|---:|---|
| 2.3.x | JDK 8 / Maven 3 | 72/72 | 04:19 | `96aac92ee8f5` |
| 2.4.x | JDK 8 / Maven 3 | 72/72 | 04:28 | `ccf2d92b8d15` |
| 2.5.x | JDK 8 / Maven 3 | 72/72 | 03:55 | `ff2f932c59ae` |
| 2.6.x | JDK 8 / Maven 3 | 72/72 | 03:34 | `dd02c9722135` |
| 2.7.x | JDK 8 / Maven 3 | 72/72 | 03:43 | `ec28a3dcad3b` |
| 3.0.x | JDK 17 / Maven 3 | 73/73 | 03:08 | `17bafd215f16` |
| 3.1.x | JDK 17 / Maven 3 | 73/73 | 03:23 | `fda3e8bc8c7a` |
| 3.2.x | JDK 17 / Maven 3 | 73/73 | 03:20 | `84a57b525471` |
| 3.3.x | JDK 17 / Maven 3 | 73/73 | 03:18 | `dd12d9b4b4f2` |
| 3.4.x | JDK 17 / Maven 3 | 73/73 | 03:21 | `6940899d645e` |
| 3.5.x | JDK 17 / Maven 3 | 73/73 | 03:21 | `79418e7d3ee5` |
| 4.0.x | JDK 21 / Maven 4 | 73/73 | 03:03 | `122a76650f86` |
| 4.1.x | JDK 21 / Maven 4 | 73/73 | 03:10 | `39f84ef7c45f` |

13/13 负向扫描通过：POM、Java 源码和 generated consumer POM 中均无
`RedisOperationTemplate` 或 `redistpl-plus-spring-boot-starter` 残留。RocketMQ Testcontainers
已按分支 JDK 语法改为动态 broker/VIP 端口对，并在宿主机 10911 被占用时通过聚焦测试。

## 双远端状态

| 分支 | GitHub | Codeup | 状态 |
|---|---|---|---|
| 2.3.x | 96aac92ee8f5 | bc130cd10361 | DRIFT: Codeup credential required |
| 2.4.x | ccf2d92b8d15 | fd043cb4532e | DRIFT: Codeup credential required |
| 2.5.x | ff2f932c59ae | a838d5c6b6bc | DRIFT: Codeup credential required |
| 2.6.x | dd02c9722135 | 5ceae4487be9 | DRIFT: Codeup credential required |
| 2.7.x | ec28a3dcad3b | 0efc5ffdd7e6 | DRIFT: Codeup credential required |
| 3.0.x | 17bafd215f16 | eda53010509c | DRIFT: Codeup credential required |
| 3.1.x | fda3e8bc8c7a | 7ba17973e507 | DRIFT: Codeup credential required |
| 3.2.x | 84a57b525471 | a26089a4620c | DRIFT: Codeup credential required |
| 3.3.x | dd12d9b4b4f2 | dccea9dcbc55 | DRIFT: Codeup credential required |
| 3.4.x | 6940899d645e | 3ac7e4a8144a | DRIFT: Codeup credential required |
| 3.5.x | 79418e7d3ee5 | 032bc72d403e | DRIFT: Codeup credential required |
| 4.0.x | 122a76650f86 | 2d47f31b6930 | DRIFT: Codeup credential required |
| 4.1.x | 39f84ef7c45f | d8c120b0aaad | DRIFT: Codeup credential required |

release-line TSV 中的 SHA 是本报告生成前实际执行 `clean verify` 的代码基线，
不把后续仅修改报告的提交伪装成已重新执行的代码验证。

## 剩余门禁

- Task 20 代码审查与 Critical/Important 闭环已完成；Codeup 凭据缺失使双远端验证仍未完成。
- Task 21 的 13 线阿里云 Maven deploy 与发布后新空缓存回拉尚未执行。
- GitHub Actions 仅在组织 Secret `MAVEN_SETTINGS_XML` 和 Actions 账户门禁可用时单独执行。
