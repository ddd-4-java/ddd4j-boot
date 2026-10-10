package io.ddd4j.boot.sample;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Demo 应用主类。
 *
 * <p>开启声明式缓存与定时任务，装配监控指标公共标签，并在容器启动完成后
 * 输出一条启动完成日志。
 */
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    /**
     * 构造 Demo 应用主类实例。
     *
     */
    public DemoApplication() {
    }

    /**
     * 启动 Spring Boot 应用。
     *
     * @param args 命令行参数
     * @throws Exception 容器启动过程中发生的异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 注册监控指标公共标签定制器。
     *
     * <p>为所有指标追加 application 公共标签，便于按应用维度聚合监控数据。
     *
     * @param applicationName 应用名称，来自配置项 spring.application.name
     * @return 指标定制器 Bean
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    /**
     * 容器启动完成后的回调，输出启动完成提示。
     *
     * @param args 命令行参数
     * @throws Exception 回调执行过程中发生的异常
     */
    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（DDD4J-Boot-Demo） Started !");
    }

}

