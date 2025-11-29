# jeebiz-boot-sample-druid-mqtt

>
基于 [Spring Boot 3.x](https://docs.spring.io/spring-boot/index.html) 、[Mybatis Plus](https://baomidou.com/introduce/)、[Druid](https://github.com/alibaba/druid)
技术为主的 Demo 功能示例。

#### 为什么选择 MQTT ？

MQTT（消息队列遥测传输）是一种轻量级的消息协议，专为低带宽、高延迟和不可靠的网络环境设计。它支持三种消息服务质量（QoS）级别，能够灵活应对不同的业务需求。在工业物联网场景中，设备数量众多且分布广泛，网络环境复杂多变。MQTT
的这些特性，使其成为工业物联网通信的首选协议。

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

Spring MQTT 提供 Spring Boot 自动配置。要启用它，请将以下依赖项添加到项目的 Maven `pom.xml` 文件中：

```xml
<!-- For MQTT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-integration</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.integration</groupId>
  <artifactId>spring-integration-mqtt</artifactId>
</dependency>
```

或者，在你的 Gradle 构建文件 `build.gradle` 中添加：

```groovy
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-integration'
    implementation 'org.springframework.integration:spring-integration-mqtt'
}
```

### 快速开始

在已创建的Java工程中，创建发送普通消息程序并运行，示例代码如下：

#### 基于 Spring Boot 的 MQTT 客户端

##### 配置 MQTT 客户端

在application.yml文件中配置 MQTT 客户端连接参数：

```yaml
spring:
  mqtt:
    url: tcp://localhost:1883
    client-id: gateway-${random.uuid}
    username: mqtt_user
    password: mqtt_password
    default-topic: /sensor/data
```

##### 创建 MQTT 配置类

创建一个 MQTT 配置类，用于初始化 MQTT 客户端并配置消息通道：

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setServerURIs(new String[]{"tcp://localhost:1883"});
        factory.setUsername("mqtt_user");
        factory.setPassword("mqtt_password".toCharArray());
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "gateway-${random.uuid}", mqttClientFactory(), "/sensor/data");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttMessageHandler() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("gateway-${random.uuid}", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("/sensor/data");
        return messageHandler;
    }
}
```

##### 创建消息处理器

创建一个消息处理器类，用于处理接收到的 MQTT 消息：

```java
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

@MessageEndpoint
public class MqttMessageHandler {

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        System.out.println("Received message: " + message.getPayload());
        // 在这里处理接收到的消息
    }
}
```

##### 实现消息发布

创建一个消息发布类，用于向设备发送消息：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {

    @Autowired
    private MessageChannel mqttInputChannel;

    public void publishMessage(String topic, String message) {
        mqttInputChannel.send(MessageBuilder.withPayload(message).setHeader("mqtt_topic", topic).build());
    }
}
```

##### 测试连接与消息传输

创建一个测试控制器，用于手动测试 MQTT 连接和消息传输：

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MqttTestController {

    @Autowired
    private MqttPublisher mqttPublisher;

    @GetMapping("/publish")
    public String publishMessage(@RequestParam String message) {
        mqttPublisher.publishMessage("/sensor/data", message);
        return "Message published successfully!";
    }
}
```

### 基于 Mica-MQTT 的 MQTT 客户端

#### 一、添加依赖

```xml
<dependency>
    <groupId>org.dromara.mica-mqtt</groupId>
    <artifactId>mica-mqtt-client-spring-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

#### 二、mqtt 客户端

##### 2.1 配置项示例

```yaml
mqtt:
  client:
    enabled: true               # 是否开启客户端，默认：true
    ip: 127.0.0.1               # 连接的服务端 ip ，默认：127.0.0.1
    port: 1883                  # 端口：默认：1883
    name: Mica-Mqtt-Client      # 名称，默认：Mica-Mqtt-Client
    client-id: 000001           # 客户端Id（非常重要，一般为设备 sn，不可重复）
    user-name: mica             # 认证的用户名
    password: 123456            # 认证的密码
    global-subscribe:           # 全局订阅的 topic，可被全局监听到，保留 session 停机重启，依然可以接受到消息。（2.2.9开始支持）
    timeout: 5                  # 超时时间，单位：秒，默认：5秒
    reconnect: true             # 是否重连，默认：true
    re-interval: 5000           # 重连时间，默认 5000 毫秒
    version: mqtt_3_1_1         # mqtt 协议版本，可选 MQTT_3_1、mqtt_3_1_1、mqtt_5，默认：mqtt_3_1_1
    read-buffer-size: 8KB       # 接收数据的 buffer size，默认：8k
    max-bytes-in-message: 10MB  # 消息解析最大 bytes 长度，默认：10M
    keep-alive-secs: 60         # keep-alive 时间，单位：秒
    heartbeat-mode: LAST_REQ    # 心跳模式，支持最后发送或接收心跳时间来计算心跳，默认：最后发送心跳的时间。（2.4.3 开始支持）
    heartbeat-timeout-strategy: PING # 心跳超时策略，支持发送 PING 和 CLOSE 断开连接，默认：最大努力发送 PING。（2.4.3 开始支持）
    clean-session: true         # mqtt clean session，默认：true
    session-expiry-interval-secs: 0 # 开启保留 session 时，session 的有效期，默认：0（2.4.2 开始支持）
    biz-thread-pool-size: 2     # mqtt 工作线程数，默认：2，如果消息量比较大，处理较慢，例如做 emqx 的转发消息处理，可以调大此参数（2.4.2 开始支持）
    ssl:
      enabled: false            # 是否开启 ssl 认证，2.1.0 开始支持双向认证
      keystore-path:            # 可选参数：ssl 双向认证 keystore 目录，支持 classpath:/ 路径。
      keystore-pass:            # 可选参数：ssl 双向认证 keystore 密码
      truststore-path:          # 可选参数：ssl 双向认证 truststore 目录，支持 classpath:/ 路径。
      truststore-pass:          # 可选参数：ssl 双向认证 truststore 密码
```

注意：**ssl** 存在三种情况

| 服务端开启ssl                        | 客户端                                 |
|---------------------------------|-------------------------------------|
| ClientAuth 为 NONE（不需要客户端验证）     | 仅仅需要开启 ssl 即可不用配置证书                 |
| ClientAuth 为 OPTIONAL（与客户端协商）   | 需开启 ssl 并且配置 truststore 证书          |
| ClientAuth 为 REQUIRE (必须的客户端验证) | 需开启 ssl 并且配置 truststore、 keystore证书 |

##### 2.2 可实现接口（注册成 Spring Bean 即可）

| 接口                               | 是否必须 | 说明                             |
|----------------------------------|------|--------------------------------|
| IMqttClientConnectListener       | 否    | 客户端连接成功监听                      |
| IMqttClientGlobalMessageListener | 否    | 全局消息监听，可以监听到所有订阅消息。（2.2.9开始支持） |

##### 2.3 客户端上下线监听

使用 Spring event 解耦客户端上下线监听，注意： `1.3.4` 开始支持。会跟自定义的 `IMqttClientConnectListener` 实现冲突，取一即可。

```java
/**
 * 示例：客户端连接状态监听
 *
 * @author L.cm
 */
@Service
public class MqttClientConnectListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientConnectListener.class);

    @Autowired
    private MqttClientCreator mqttClientCreator;

    @EventListener
    public void onConnected(MqttConnectedEvent event) {
        logger.info("MqttConnectedEvent:{}", event);
    }

    @EventListener
    public void onDisconnect(MqttDisconnectEvent event) {
        // 离线时更新重连时的密码，适用于类似阿里云 mqtt clientId 连接带时间戳的方式 
        logger.info("MqttDisconnectEvent:{}", event);
        // 在断线时更新 clientId、username、password
        mqttClientCreator.clientId("newClient" + System.currentTimeMillis())
            .username("newUserName")
            .password("newPassword");
    }

}
```

##### 2.4 自定义 java 配置（可选）

```java
@Configuration(proxyBeanMethods = false)
public class MqttClientCustomizerConfiguration {

	@Bean
	public MqttClientCustomizer mqttClientCustomizer() {
		return new MqttClientCustomizer() {
			@Override
			public void customize(MqttClientCreator creator) {
				// 此处可自定义配置 creator，会覆盖 yml 中的配置
				System.out.println("----------------MqttServerCustomizer-----------------");
			}
		};
	}

}
```

##### 2.5 订阅示例

```java
@Service
public class MqttClientSubscribeListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientSubscribeListener.class);

	@MqttClientSubscribe("/test/#")
	public void subQos0(String topic, byte[] payload) {
		logger.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
	}

	@MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.QOS1)
	public void subQos1(String topic, byte[] payload) {
		logger.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
	}

	@MqttClientSubscribe("/sys/${productKey}/${deviceName}/thing/sub/register")
	public void thingSubRegister(String topic, byte[] payload) {
		// 1.3.8 开始支持，@MqttClientSubscribe 注解支持 ${} 变量替换，会默认替换成 +
		// 注意：mica-mqtt 会先从 Spring boot 配置中替换参数 ${}，如果存在配置会优先被替换。
		logger.info("topic:{} payload:{}", topic, new String(payload, StandardCharsets.UTF_8));
	}

}
```

##### 2.6 共享订阅 topic 说明

mica-mqtt 支持两种**共享订阅**方式：

1. 共享订阅：订阅前缀 `$queue/`，多个客户端订阅了 `$queue/topic`，发布者发布到 `topic`，则只有一个客户端会接收到消息。
2. 分组订阅：订阅前缀 `$share/<group>/`，组客户端订阅了 `$share/group1/topic`、`$share/group2/topic`..，发布者发布到 `topic`
   ，则消息会发布到每个 **group** 中，但是每个 **group** 中只有一个客户端会接收到消息。

**注意：** 如果发布的 `topic` 以 `/` 开头，例如：`/topic/test`，需要订阅 `$share/group1//topic/test`，另外 mica-mqtt
默认随机消息路由，共享订阅的多个客户端会随机收到消息。

##### 2.7 MqttClientTemplate 使用示例

```java

import org.dromara.mica.mqtt.spring.client.MqttClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Service
public class MainService {
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    @Autowired
    private MqttClientTemplate client;

    public boolean publish() {
        client.publish("/test/client", "mica最牛皮".getBytes(StandardCharsets.UTF_8));
        return true;
    }

    public boolean sub() {
        client.subQos0("/test/#", (context, topic, message, payload) -> {
            logger.info(topic + '\t' + new String(payload, StandardCharsets.UTF_8));
        });
        return true;
    }

}
```

#### 3. 多个 mqtt client 客户端

##### 3.1 自定义 MqttClientTemplate bean 2.2.11 开始已简化，老版本建议先升级。

```java
@Configuration
public class OtherMqttClientConfiguration {

	@Bean("mqttClientTemplate1")
	public MqttClientTemplate mqttClientTemplate1() {
		MqttClientCreator mqttClientCreator1 = MqttClient.create()
			.ip("mqtt.dreamlu.net")
			.username("mica")
			.password("mica");
		return new MqttClientTemplate(mqttClientCreator1);
	}

}
```

##### 3.2 修改 starter 自带的 MqttClientTemplate Bean 引入

由于现在加入了一个新的名为 `mqttClientTemplate1` MqttClientTemplate，老的 starter 内置的 MqttClientTemplate 引入也需要添加
bean name。

```java
@Autowired
@Qualifier(MqttClientTemplate.DEFAULT_CLIENT_TEMPLATE_BEAN)
private MqttClientTemplate mqttClientTemplate;
```

##### 3.3 新加入的 mqttClientTemplate1 MqttClientTemplate bean 引入

```java
@Autowired
@Qualifier("mqttClientTemplate1")
private MqttClientTemplate mqttClientTemplate;
```

##### 3.4 新加入的 mqttClientTemplate1 注解订阅

注意：由于 `@MqttClientSubscribe` clientTemplateBean 默认是 `MqttClientTemplate.DEFAULT_CLIENT_TEMPLATE_BEAN`，所以新增的
`mqttClientTemplate1` 注解订阅的时候也需要配置。

```java
@MqttClientSubscribe(
    value = "/#", 
    clientTemplateBean = "mqttClientTemplate1"
)
public void sub1(String topic, byte[] payload) {
    logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
}
```