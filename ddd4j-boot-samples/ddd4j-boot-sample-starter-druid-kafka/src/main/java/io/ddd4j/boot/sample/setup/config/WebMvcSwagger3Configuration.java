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
 * OpenAPI（Swagger3）文档页装配配置。
 * <p>
 * 仅在配置项 {@code springdoc.api-docs.enabled} 为 {@code true}（或未配置）时生效，
 * 并注册首页控制器将根路径转发到在线文档页。
 * </p>
 */
@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebMvcSwagger3Configuration {

    /**
     * 构造 OpenAPI 文档装配配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public WebMvcSwagger3Configuration() {
    }

    /**
     * 首页控制器：将应用根路径转发到在线文档页。
     */
    @Controller
    class HomepageController {

        /**
         * 构造首页控制器（显式无参构造器，与编译器生成的默认构造器等价）。
         */
        HomepageController() {
        }

        /**
         * 处理根路径请求并转发到文档页。
         *
         * @return 转发目标视图路径
         */
        @Hidden
        @GetMapping("/")
        public String index() {
            return "forward:/doc.html";
        }

    }

}
