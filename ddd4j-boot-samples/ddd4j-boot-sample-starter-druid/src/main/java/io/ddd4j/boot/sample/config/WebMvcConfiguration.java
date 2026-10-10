package io.ddd4j.boot.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 通用配置，当前保留默认约定，预留扩展点。
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 构造 MVC 配置类实例。
     *
     */
    public WebMvcConfiguration() {
    }

}
