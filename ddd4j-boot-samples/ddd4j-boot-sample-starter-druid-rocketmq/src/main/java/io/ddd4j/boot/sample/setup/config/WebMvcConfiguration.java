package io.ddd4j.boot.sample.setup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC Web 装配配置：实现 {@code WebMvcConfigurer} 扩展点，样例暂未覆盖定制项。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 无参构造，保持 Spring Bean 默认实例化语义。
     */
    public WebMvcConfiguration() {
    }

}
