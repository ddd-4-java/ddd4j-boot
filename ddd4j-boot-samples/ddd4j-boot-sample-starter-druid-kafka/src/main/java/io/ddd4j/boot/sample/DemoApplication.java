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
 * Kafka 示例演示应用的启动类。
 * <p>
 * 作为 Spring Boot 的引导入口，开启注解驱动缓存与定时任务调度，
 * 装配 Micrometer 指标公共标签，并在容器启动完成后输出启动提示。
 * </p>
 */
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    /**
     * 构造演示应用启动对象（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public DemoApplication() {
    }

    /**
     * 应用主入口：以当前类为源类启动 Spring Boot 应用。
     *
     * @param args 命令行启动参数
     * @throws Exception 应用启动过程中发生的任何异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 注册指标公共标签定制器，为所有指标统一追加应用名标签。
     *
     * @param applicationName 应用名，取自配置项 {@code spring.application.name}
     * @return 为指标注册表追加公共标签的定制器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    /**
     * 容器启动完成后的回调，向标准错误流输出启动提示。
     *
     * @param args 命令行启动参数
     * @throws Exception 回调处理过程中发生的任何异常
     */
    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（ddd4j-boot-Demo） Started !");
    }

}
