# jeebiz-boot-sample-druid-kafka

> 基于 [Spring Boot 2.x](https://docs.spring.io/spring-boot/index.html) 、[Apache Kafka](https://kafka.apache.org/)、[Mybatis Plus](https://baomidou.com/introduce/)、[Druid](https://github.com/alibaba/druid) 技术为主的 Demo 功能示例。

### 技术栈

- [Spring Boot 2.x](https://docs.spring.io/spring-boot/index.html)
- [Apache Kafka](https://kafka.apache.org/)
- [Mybatis Plus](https://baomidou.com/introduce/)
- [Druid](https://github.com/alibaba/druid)
- [Embed Undertow](https://undertow.io/)

### 先决条件

您首先需要一个 Kafka 服务端。请参阅官方 [本地部署 Apache Kafka](https://kafka.apache.org/quickstart "本地部署 Apache Kafka")，开始在本地计算机上运行 Kafka 服务。

**注意**: 开发调试，推荐使用 Docker 部署 RocketMQ。

#### 添加存储库和 BOM

#### 自动配置

Apache Kafka 提供 Spring Boot 自动配置。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
 <!-- For Kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
<groupId>org.apache.kafka</groupId>
<artifactId>kafka-clients</artifactId>
<version>${kafka.version}</version>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
    implementation 'org.springframework.kafka:spring-kafka'
}
```

### 示例代码

在已创建的Java工程中，创建发送普通消息程序并运行，示例代码如下：
 
```shell
 
```