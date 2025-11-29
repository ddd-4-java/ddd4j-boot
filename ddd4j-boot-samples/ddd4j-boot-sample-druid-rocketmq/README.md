# jeebiz-boot-sample-druid-rocketmq

>
基于 [Spring Boot 2.x](https://docs.spring.io/spring-boot/index.html) 、[Apache RocketMQ](https://rocketmq.apache.org/zh/)、[Mybatis Plus](https://baomidou.com/introduce/)、[Druid](https://github.com/alibaba/druid)
技术为主的 Demo 功能示例。

### 技术栈

- [Spring Boot 2.x](https://docs.spring.io/spring-boot/index.html)
- [Apache RocketMQ](https://rocketmq.apache.org/zh/)
- [Mybatis Plus](https://baomidou.com/introduce/)
- [Druid](https://github.com/alibaba/druid)
- [Embed Undertow](https://undertow.io/)

### 先决条件

您首先需要一个 RocketMQ
服务端。请参阅官方 [本地部署 RocketMQ](https://rocketmq.apache.org/zh/docs/quickStart/01quickstart "本地部署 RocketMQ")
，开始在本地计算机上运行 RocketMQ 服务。

**注意**: 开发调试，推荐使用 Docker 部署 RocketMQ。

- [Docker 部署 RocketMQ](https://rocketmq.apache.org/zh/docs/quickStart/02quickstartWithDocker)
- [Docker Compose 部署 RocketMQ](https://rocketmq.apache.org/zh/docs/quickStart/03quickstartWithDockercompose)
- [Kubernetes 部署 RocketMQ](https://rocketmq.apache.org/zh/docs/quickStart/04quickstartWithHelmInKubernetes)

#### 添加存储库和 BOM

#### 自动配置

Apache RocketMQ 提供 Spring Boot 自动配置。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
<!-- https://mvnrepository.com/artifact/org.apache.rocketmq/rocketmq-spring-boot-starter -->
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.3.3</version>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
    implementation 'org.apache.rocketmq:rocketmq-spring-boot-starter:2.3.3'
}
```

### 示例代码

在已创建的Java工程中，创建发送普通消息程序并运行，示例代码如下：

```shell
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerExample {
    private static final Logger logger = LoggerFactory.getLogger(ProducerExample.class);

    public static void main(String[] args) throws ClientException {
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8080;xxx:8081
        // 此处为示例，实际使用时请替换为真实的 Proxy 地址和端口
        String endpoint = "localhost:8081";
        // 消息发送的目标Topic名称，需要提前创建。
        String topic = "TestTopic";
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(endpoint);
        ClientConfiguration configuration = builder.build();
        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
        Producer producer = provider.newProducerBuilder()
            .setTopics(topic)
            .setClientConfiguration(configuration)
            .build();
        // 普通消息发送。
        Message message = provider.newMessageBuilder()
            .setTopic(topic)
            // 设置消息索引键，可根据关键字精确查找某条消息。
            .setKeys("messageKey")
            // 设置消息Tag，用于消费端根据指定Tag过滤消息。
            .setTag("messageTag")
            // 消息体。
            .setBody("messageBody".getBytes())
            .build();
        try {
            // 发送消息，需要关注发送结果，并捕获失败等异常。
            SendReceipt sendReceipt = producer.send(message);
            logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            logger.error("Failed to send message", e);
        }
        // producer.close();
    }
}
```