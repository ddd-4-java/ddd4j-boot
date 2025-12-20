package io.ddd4j.boot.sample.config;

import io.swagger.v3.oas.annotations.Hidden;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger3配置类
 */
@Configuration
public class WebMvcSwagger3Configuration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }

    @Controller
    class HomepageController {

        @Hidden
        @GetMapping("/")
        public String index() {
            return "redirect:/swagger-ui/index.html";
        }

    }

}

