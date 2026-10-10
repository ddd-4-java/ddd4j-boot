/**
 * Copyright (C) 2018 ddd4j (http://ddd4j.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.sample.setup.config;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger（springdoc）文档开关配置：按 {@code springdoc.api-docs.enabled} 条件装配首页跳转控制器。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebMvcSwagger3Configuration {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public WebMvcSwagger3Configuration() {
    }

    /**
     * 文档首页控制器：将根路径请求转发到 {@code /doc.html} 在线文档页。
     */
    @Controller
    class HomepageController {

        /**
         * 无参构造，保持 Spring Bean 默认实例化语义。
         */
        HomepageController() {
        }

        /**
         * 根路径转发到在线文档页。
         *
         * @return 转发目标视图 {@code /doc.html}
         */
        @Hidden
        @GetMapping("/")
        public String index() {
            return "forward:/doc.html";
        }

    }

}
