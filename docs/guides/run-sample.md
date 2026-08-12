# 示例模块本地运行指南

## 环境准备

- JDK：17；Maven：3.6+；
- 网络：可访问公共与私有仓库（根 POM 配置了 `maven.aliyun` 与 `coding.net` 仓库）

## 选择示例

- 推荐：`ddd4j-boot-samples/ddd4j-boot-sample-druid`
    - 包含 Thymeleaf、Ehcache、MyBatis、Flyway、Druid、Log4j2 组合（`README.md`）

## 构建与运行

```bash
# 1. 在仓库根目录构建指定示例（同时构建依赖）
mvn -q -DskipTests package -pl ddd4j-boot-samples/ddd4j-boot-sample-druid -am

# 2. 进入示例目录运行
cd ddd4j-boot-samples/ddd4j-boot-sample-druid
mvn spring-boot:run
```

## 配置说明

- `src/main/resources/application.yaml` 及 `application-*.yaml`：数据源、缓存、日志等
- 日志：`src/main/resources/conf/log4j2*.xml`
- i18n：`src/main/resources/i18n/messages*.properties`
- 模板：`src/main/resources/templates/html/*`

## 验证

- 启动后访问根路径与示例页面；检查日志输出与数据库连接
- 若使用外部中间件（如 MQ/Kafka），按对应示例模块选择运行并配置地址

