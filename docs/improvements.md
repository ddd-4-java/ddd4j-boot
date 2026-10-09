# 风险与改进建议

## 注册机制统一

- 建议逐步切换至 Boot 3 `AutoConfiguration.imports`，保留 `spring.factories` 兼容期后清理，降低重复维护成本

## 依赖裁剪与安全

- `ddd4j-boot-dependencies` 涵盖依赖广泛，建议按需引入并启用依赖扫描（OWASP），在父 POM 默认关闭的情况下为业务仓库开启

## 配置规范

- 统一 `application*.yaml` 命名与分层，敏感信息通过外部化配置与环境变量注入，避免硬编码
- 统一日志格式与 MDC 字段（traceId、spanId、userId 等），便于跨服务检索

## 文档与示例

- 为每个组件模块补充 README 与最小可运行示例，尤其是 `datascope`、`external`、`logs` 等

## 测试与质量

- 开启单元测试与覆盖率统计（Jacoco）并设定合理阈值；对 WebFlux 异常映射补充响应式测试

