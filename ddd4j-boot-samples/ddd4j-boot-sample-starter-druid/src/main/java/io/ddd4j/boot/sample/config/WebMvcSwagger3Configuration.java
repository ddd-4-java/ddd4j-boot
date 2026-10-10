package io.ddd4j.boot.sample.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger3 配置类。
 *
 * <p>按 /api/** 路径归组生成公开 API 文档分组，并提供首页跳转到
 * Swagger UI 的控制器。
 */
@Configuration
public class WebMvcSwagger3Configuration {

    /**
     * 构造 Swagger3 配置类实例。
     *
     */
    public WebMvcSwagger3Configuration() {
    }

    /**
     * 注册公开 API 文档分组。
     *
     * @return 针对 /api/** 路径的分组定义 Bean
     */
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * 首页跳转控制器。
     */
    @Controller
    class HomepageController {

        /**
         * 访问根路径时重定向到 Swagger UI。
         *
         * @return 指向 Swagger UI 的重定向视图
         */
        @Hidden
        @GetMapping("/")
        public String index() {
            return "redirect:/swagger-ui/index.html";
        }

    }

}

