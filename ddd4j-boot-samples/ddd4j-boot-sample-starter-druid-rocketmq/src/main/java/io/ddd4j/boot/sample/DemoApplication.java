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
 * RocketMQ 示例启动类：开启缓存与定时任务，注册应用名监控公共标签。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    /**
     * 无参构造，保持 Spring 入口类默认实例化语义。
     */
    public DemoApplication() {
    }

    /**
     * 应用主入口。
     *
     * @param args 命令行参数
     * @throws Exception 启动过程异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 注册监控公共标签定制器，为所有指标附加应用名标签。
     *
     * @param applicationName 应用名（取自 {@code spring.application.name}）
     * @return 面向 MeterRegistry 的定制器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    /**
     * 容器启动完成后的命令行回调，打印启动标识。
     *
     * @param args 命令行参数
     * @throws Exception 执行过程异常
     */
    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（ddd4j-boot-Demo） Started !");
    }

}
