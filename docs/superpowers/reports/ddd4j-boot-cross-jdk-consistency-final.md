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
- 阶段六 Redistpl 解耦和 13 线完整 reactor 仍未完成，因此总体发布状态仍为 `BLOCKED`。

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

## 双远端状态

| 分支 | Codeup | GitHub | 状态 |
|---|---|---|---|
| 2.3.x | 7e5e3bae5335 | 7e5e3bae5335 | MATCH |
| 2.4.x | 4fa5cc54ded2 | 4fa5cc54ded2 | MATCH |
| 2.5.x | fef7d48b1380 | fef7d48b1380 | MATCH |
| 2.6.x | 1ca18a4facac | 1ca18a4facac | MATCH |
| 2.7.x | 60a07ce5aac7 | 60a07ce5aac7 | MATCH |
| 3.0.x | e2094e55b9c5 | e2094e55b9c5 | MATCH |
| 3.1.x | ed8bf1c47520 | ed8bf1c47520 | MATCH |
| 3.2.x | 142363ad2112 | 142363ad2112 | MATCH |
| 3.3.x | af5d3553f2a4 | af5d3553f2a4 | MATCH |
| 3.4.x | 3c5e1022cc96 | 3c5e1022cc96 | MATCH |
| 3.5.x | b2c96bbce581 | b2c96bbce581 | MATCH |
| 4.0.x | 69e8ec5b026e | 69e8ec5b026e | MATCH |
| 4.1.x | 8376107b138b | 8376107b138b | MATCH |

4.0.x 的 `69e8ec5b fix(bom): manage all Boot production modules` 已通过 BOM alignment 与 Maven 4
聚焦 verify，并同步到 GitHub。release-line TSV 中的 SHA 是本报告生成前实际验证的代码基线，
不把包含报告自身的证据提交伪装成已重新执行的代码验证。

## 剩余门禁

- `ddd4j-boot-data-external` 仍依赖未发布的 `redistpl-plus-spring-boot-starter`，2.3.x 空缓存完整
  reactor 在此停止。
- 阶段六规格尚待书面审阅确认后实施。
- 13 条线完整 reactor、独立审查、阿里云 Maven deploy 与发布后空缓存回拉均未完成。
- GitHub Actions 仅在组织 Secret `MAVEN_SETTINGS_XML` 和 Actions 账户门禁可用时单独执行。
