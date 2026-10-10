/**
 * Copyright (C) 2018 ddd4j (https://github.com/easy4j).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 接口文档首页配置类，将根路径转发到在线接口文档页面。
 * <p>
 * 可通过 springdoc.api-docs.enabled 配置开关，默认开启。
 */
@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebMvcSwagger3Configuration {

    /**
     * 构造接口文档配置类实例。
     */
    public WebMvcSwagger3Configuration() {
    }

    /**
     * 接口文档首页控制器。
     */
    @Controller
    class HomepageController {

        /**
         * 根路径转发到在线接口文档页面。
         *
         * @return 转发目标路径
         */
        @Hidden
        @GetMapping("/")
        public String index() {
            return "forward:/doc.html";
        }

    }

}
