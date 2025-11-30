## Ddd4j Boot 3.x 简介：

> Ddd4j Boot 3.x 是以 Spring Boot 3.5.x 为基础，构建的 Spring Boot 项目快速开发脚手架；

- 1、继承 Spring Boot ，具备 Spring Boot 的 所有特性

- 2、集成 Dozer、Fastjson、Jackson、Guava、Mybatis Plus、Okhttp3、Snowflake、Swagger、TrueLicense、Ip2region 等组件

- 3、集成各类三方Maven组件，统一管理版本

- 4、定义基础对象（BaseController、BaseService、BaseMapper 等）、异常对象、IP地址解析

- 5、支持 WebMvc 和 WebFlux 项目自动初始化，全局异常处理

- 6、自定义项目默认的打包编译方式；快速的构建项目部署包

#### Ddd4j Boot 说明：

该项目，主要用于定义通用的Maven配置、第三方依赖、打包规则等！

**Maven模块**：

| 模块                      | 说明                                                                          |
|-------------------------|-----------------------------------------------------------------------------|
| ddd4j-boot-bom          | pom类型模块，BOM依赖管理，用于外部项目引用 ddd4j-boot 模块版本管理                                  |
| ddd4j-boot-dependencies | pom类型模块，负责配置项目公共依赖，便于依赖组件版本控制                                               |
| ddd4j-boot-core         | 项目核心模块，封装基础API、公共对象（BaseController、BaseService、BaseMapper 等）、异常对象到jar中，以便复用 |
| ddd4j-boot-cmpt         | 组件模块父模块，包含各类功能组件的自动配置模块                                                     |
| ddd4j-boot-parent       | pom类型模块，默认配置Maven插件相关参数和打包方式                                                |
| ddd4j-boot-samples      | 具体服务示例，该模块下的各服务模块，均要继承 `ddd4j-boot-parent`                                  |

**注意事项**：

- 1、新建独立项目需要继承 ddd4j-boot-parent，此处的项目指产品项目
- 2、按目前结构，基本后期都是增加业务代码（即子系统的业务）
- 3、如果一个业务模块是比较多的模块，比如学工服务，创建模块时候应考虑后期子服务拆分的问题，应创建一个多级模块
- 4、外部项目引用 ddd4j-boot 模块时，建议使用 ddd4j-boot-bom 进行版本管理

**结构说明**

```
|--ddd4j-boot
|----ddd4j-boot-bom                       #BOM依赖管理，用于外部项目引用 ddd4j-boot 模块版本管理
|----ddd4j-boot-dependencies              #公共依赖，便于依赖组件版本控制
|----ddd4j-boot-core                      #核心模块，基础API、公共对象（BaseController、BaseService、BaseMapper等）、异常对象
|----ddd4j-boot-cmpt                      #组件模块父模块
|------ddd4j-boot-cmpt-akka               #Akka组件
|------ddd4j-boot-cmpt-crypto             #加解密组件
|------ddd4j-boot-cmpt-datascope          #数据权限组件
|------ddd4j-boot-cmpt-license            #License组件
|------ddd4j-boot-cmpt-logs               #日志组件
|------ddd4j-boot-cmpt-pf4j               #PF4J插件组件
|------ddd4j-boot-cmpt-cola               #COLA组件
|------ddd4j-boot-cmpt-satoken            #SaToken组件
|------ddd4j-boot-cmpt-jackson            #Jackson组件
|------ddd4j-boot-cmpt-webmvc             #WebMVC组件
|------ddd4j-boot-cmpt-webflux            #WebFlux组件
|------ddd4j-boot-cmpt-kafka              #Kafka组件
|------ddd4j-boot-cmpt-external           #外部API集成组件
|------ddd4j-boot-cmpt-validation         #验证组件
|----ddd4j-boot-parent                    #子模块的父级工程，定义Maven配置
|----ddd4j-boot-samples                   #具体业务服务
|--------ddd4j-boot-sample-druid          #集成Druid数据源示例
|--------ddd4j-boot-sample-druid-activemq #集成Druid数据源 + ActiveMQ 示例
|--------ddd4j-boot-sample-druid-amqp     #集成Druid数据源 + RabbitMQ 示例
|--------ddd4j-boot-sample-druid-kafka    #集成Druid数据源 + Kafka 示例
|--------ddd4j-boot-sample-druid-mqtt-client1 #集成Druid数据源 + MQTT Client1 示例
|--------ddd4j-boot-sample-druid-mqtt-client2 #集成Druid数据源 + MQTT Client2 示例
|--------ddd4j-boot-sample-druid-mqtt-server  #集成Druid数据源 + MQTT Server 示例
|--------ddd4j-boot-sample-druid-rocketmq #集成Druid数据源 + RocketMQ 示例
|--------ddd4j-boot-sample-druid-war     #集成Druid数据源打War包示例
|--------ddd4j-boot-sample-hikaricp      #集成 Hikaricp数据源示例
|--------ddd4j-boot-sample-hikaricp-activemq #集成 Hikaricp数据源 + ActiveMQ 示例
|--------ddd4j-boot-sample-hikaricp-amqp #集成 Hikaricp数据源 + RabbitMQ 示例
|--------ddd4j-boot-sample-hikaricp-kafka #集成 Hikaricp数据源 + Kafka 示例
|--------ddd4j-boot-sample-hikaricp-rocketmq #集成 Hikaricp数据源 + RocketMQ 示例
|--------ddd4j-boot-sample-hikaricp-war  #集成 Hikaricp数据源打War包示例
|--------ddd4j-boot-sample-r2dbc-webflux #集成 R2dbc + WebFlux 示例
```

**使用说明**

### 1. 外部项目引用（推荐使用 BOM）

在外部项目的 `pom.xml` 中引入 BOM：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.github.hiwepy</groupId>
            <artifactId>ddd4j-boot-bom</artifactId>
            <version>${ddd4j-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

然后直接引入需要的模块，无需指定版本：

```xml
<dependencies>
    <dependency>
        <groupId>com.github.hiwepy</groupId>
        <artifactId>ddd4j-boot-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.hiwepy</groupId>
        <artifactId>ddd4j-boot-cmpt-webmvc</artifactId>
    </dependency>
</dependencies>
```

### 2. 内部项目使用

内部项目继承 `ddd4j-boot-parent`：

```xml
<parent>
    <groupId>com.github.hiwepy</groupId>
    <artifactId>ddd4j-boot-parent</artifactId>
    <version>${revision}</version>
    <relativePath>../ddd4j-boot-parent/pom.xml</relativePath>
</parent>
```

### 3. 组件模块说明

| 组件模块                       | 说明                                |
|----------------------------|-----------------------------------|
| ddd4j-boot-cmpt-akka       | Akka 组件，支持 Akka 3 Actor 系统          |
| ddd4j-boot-cmpt-crypto     | 加解密组件，支持 AES、SM3、SM4 等加密算法        |
| ddd4j-boot-cmpt-datascope  | 数据权限组件，支持数据范围权限控制                 |
| ddd4j-boot-cmpt-license    | License 组件，支持 TrueLicense 许可证管理   |
| ddd4j-boot-cmpt-logs       | 日志组件，支持 API 操作日志记录                |
| ddd4j-boot-cmpt-pf4j       | PF4J 插件组件，支持插件化开发                 |
| ddd4j-boot-cmpt-cola       | COLA 组件，支持 COLA 架构模式              |
| ddd4j-boot-cmpt-satoken    | SaToken 组件，支持 SaToken 权限认证        |
| ddd4j-boot-cmpt-jackson    | Jackson 组件，支持 Jackson 序列化配置       |
| ddd4j-boot-cmpt-webmvc     | WebMVC 组件，支持 Spring MVC 自动配置      |
| ddd4j-boot-cmpt-webflux    | WebFlux 组件，支持 Spring WebFlux 自动配置 | 
| ddd4j-boot-cmpt-kafka      | Kafka 组件，支持 Kafka 消息队列集成          |
| ddd4j-boot-cmpt-external   | 外部 API 集成组件，支持外部服务调用              |
| ddd4j-boot-cmpt-validation | 验证组件，支持自定义验证规则                    |

#### Spring Docs

https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/features.html#features.spring-application
