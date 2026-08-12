package io.ddd4j.boot.sample.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 基于共享 Order Domain/Application 的 Spring Boot 示例入口。
 */
@SpringBootApplication
public class OrderBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderBootApplication.class, args);
    }
}
