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
 * 示例应用启动类。
 * <p>
 * 开启注解缓存与定时任务支持，容器启动完成后输出启动提示。
 *
 * @since 2023-08-06
 */
@EnableCaching(proxyTargetClass = true)
@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    /**
     * 构造示例应用启动类实例。
     */
    public DemoApplication() {
    }

    /**
     * 应用主入口，启动 Spring Boot 应用。
     *
     * @param args 命令行启动参数
     * @throws Exception 应用启动过程异常
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * 注册 Micrometer 公共标签定制器，为所有指标追加 application 标签。
     *
     * @param applicationName 应用名称，取自 spring.application.name 配置
     * @return 指标注册表公共标签定制器
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

    /**
     * 容器启动完成后回调，打印启动提示信息。
     *
     * @param args 命令行启动参数
     * @throws Exception 运行过程异常
     */
    @Override
    public void run(String... args) throws Exception {
        System.err.println("Spring Boot Application（ddd4j-boot-Demo） Started !");
    }

}
