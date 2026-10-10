package io.ddd4j.boot.sample;

import io.ddd4j.boot.sample.entity.MyEvent;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * WebFlux 演示应用启动类：开启组件扫描、注册监控通用标签，并在启动时初始化 MongoDB capped 集合。
 *
 * @author ddd4j
 * @since 1.0.0
 */
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    /**
     * 构造演示应用入口对象。
     */
    public DemoApplication() {
    }

    /**
     * 应用程序入口，以默认参数启动 Spring Boot 应用。
     *
     * @param args 命令行参数
     * @throws Exception 启动过程中的异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 注册 Micrometer 监控配置器，为全部指标附加 application 通用标签。
     *
     * @param applicationName 应用名称，来自 spring.application.name 配置
     * @return 应用公共标签定制器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    /**
     * 命令行回调，启动完成后在标准错误输出打印启动标识。
     *
     * @param args 命令行参数
     * @throws Exception 回调执行过程中的异常
     */
    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（ddd4j-boot-Demo） Started !");
    }

    /**
     * 初始化 MongoDB 集合：先删除 MyEvent 集合，再以 200 字节上限创建 capped 集合，
     * 使事件写入超出容量后自动淘汰最旧数据。
     *
     * @param mongo MongoDB 操作模板
     * @return 执行集合初始化的命令行执行器
     */
    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {  // 2
        return (String... args) -> {    // 3
            mongo.dropCollection(MyEvent.class);    // 4
            mongo.createCollection(MyEvent.class, CollectionOptions.empty().size(200).capped()); // 5
        };
    }

}
