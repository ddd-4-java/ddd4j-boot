package io.ddd4j.boot.sample.setup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 扩展配置类，可在此注册拦截器、跨域等 Web 层配置。
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 构造 Web MVC 配置类实例。
     */
    public WebMvcConfiguration() {
    }

}
