package io.ddd4j.boot.sample.config;

import springfox.documentation.annotations.ApiIgnore;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Swagger2配置类
 */
@Configuration
public class WebMvcSwagger2Configuration {

    @Controller
    class HomepageController {

        @ApiIgnore
        @GetMapping("/")
        public String index() {
            return "redirect:/doc.html";
        }

    }

}

