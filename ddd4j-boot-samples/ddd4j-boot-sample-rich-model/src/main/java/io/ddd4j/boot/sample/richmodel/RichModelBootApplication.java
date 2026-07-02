package io.ddd4j.boot.sample.richmodel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot rich-model sample.
 */
@SpringBootApplication
@MapperScan("io.ddd4j.boot.sample.richmodel.order.infrastructure.persistence")
public class RichModelBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(RichModelBootApplication.class, args);
    }
}
