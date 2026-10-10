package io.ddd4j.boot.sample.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于共享 Order Domain/Application 的 Spring Boot 示例入口。
 */
@SpringBootApplication
public class OrderBootApplication {

/**
 * 构造OrderBootApplication对象（默认无参构造，字段由调用方逐个设置）。
 */
public OrderBootApplication() {
}

    /**
     * 应用启动入口。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(OrderBootApplication.class, args);
    }
}
