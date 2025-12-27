/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.sample.setup.config;



import springfox.documentation.annotations.ApiIgnore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@ConditionalOnProperty(prefix = "swagger", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WebMvcSwagger2Configuration {

    @Controller
    class HomepageController {

        @ApiIgnore
        @GetMapping("/")
        public String index() {
            return "forward:/doc.html";
        }

    }

}
