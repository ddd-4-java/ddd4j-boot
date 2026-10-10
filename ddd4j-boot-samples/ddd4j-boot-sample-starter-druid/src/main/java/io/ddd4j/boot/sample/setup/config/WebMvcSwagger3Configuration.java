/**
 * Copyright (C) 2018 ddd4j (https://github.com/ddd-4-java/ddd4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * WebMvcSwagger3Configuration 类
 */
@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebMvcSwagger3Configuration {
    /**
     * 构造 WebMvcSwagger3Configuration 实例。
     *
     */
    public WebMvcSwagger3Configuration() {
    }

    @Controller
    class HomepageController {

        @Hidden
        @GetMapping("/")
        public String index() {
            return "forward:/doc.html";
        }

    }

}
