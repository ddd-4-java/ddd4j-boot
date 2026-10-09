## 项目概述

- Ddd4j Boot 是基于 Spring Boot 3.5.x 的快速开发脚手架，聚合通用依赖、基础 API 与多种自动配置能力，支持 WebMVC 与 WebFlux
  两种风格，提供丰富的演示样例与打包部署支持。

## 模块结构

- 管理模块：`ddd4j-boot-bom`、`ddd4j-boot-dependencies`、`ddd4j-boot-parent`
- 核心模块：`ddd4j-boot-core`（基础响应、异常、Service/Mapper/Controller 基类、工具集、主题与上下文等）
- 组件模块：`ddd4j-boot-cmpt-*`（webmvc、webflux、jackson、crypto、kafka、license、satoken、datascope、external、logs、akka 等）
- 示例模块：`ddd4j-boot-samples`（Druid/HikariCP + MQ/Kafka/RocketMQ/MQTT/WebFlux 等组合示例）

## 技术栈与版本

- 构建：Maven 多模块；父 POM 继承 `spring-boot-starter-parent:3.5.6`
- Java：17；Spring Framework：6.2.x；Spring Security、Reactor、Jackson 等通过 BOM 统一管理
- 依赖统一版本来源：`ddd4j-boot-dependencies` 广泛声明第三方版本属性

## 自动配置与基础能力

- 组件以 AutoConfiguration 注入（兼容 `spring.factories` 与 Boot 3 的 `AutoConfiguration.imports`）
- WebMVC/WebFlux：本地化、主题、拦截器、全局异常处理；Jackson：序列化配置；Kafka/Sa-Token/License/Crypto/DataScope/External
  等均有开箱集成
- 核心响应规范：`ApiRestResponse` + `ApiCode`；统一异常映射与国际化消息输出
- 数据访问：MyBatis-Plus 基类 `BaseMapper`/`BaseServiceImpl`；分页与排序、通用统计接口
- 实用能力：幂等控制注解与工具、MDC 日志链路、Subject 线程上下文、雪花号/序列等

## 示例工程与运行

- `ddd4j-boot-samples` 提供不同数据源/消息中间件的组合示例，包含 `application*.yaml`、`log4j2*.xml`、模板与 i18n
  资源，便于快速本地验证
- 父 `ddd4j-boot-parent` 集成 Appassembler 与 Docker 打包插件，支持脚本/服务/镜像三种分发模式

## 下一步计划（待确认）

1. 输出一份架构可视化图（模块关系与依赖管理），便于团队共享认知
2. 梳理两条典型链路：
    - WebMVC 请求→拦截器→Controller→Service→异常→统一响应
    - WebFlux 响应式链路与统一异常映射
3. 编写 Sa-Token 认证授权快速接入指南，并在示例中演示登录/鉴权/扩展载荷读取
4. 汇总核心通用能力实战示例：分页、幂等（注解/Key 生成）、国际化消息、MDC 链路日志
5. 选择一个示例模块进行本地编译运行，验证配置与自动装配的实际效果（含 Postman/HTTPie 调试脚本）
6. 演示打包与部署：生成可执行脚本、服务化守护进程与 Docker 镜像（本地仓库）
7. 提出风险与改进建议：Boot 3 机制统一、`spring.factories` 清理、依赖裁剪与安全扫描、配置规范化

请确认以上计划，确认后我将按步骤产出文档与演示并在仓库中提交必要的说明与脚本（不进行代码侵入型改动）。