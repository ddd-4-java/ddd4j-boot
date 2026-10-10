package io.ddd4j.boot.sample.setup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 通用装配配置。
 * <p>
 * 实现 {@code WebMvcConfigurer} 扩展点，供后续按需覆写拦截器、
 * 资源映射、跨域等 MVC 层默认约定；当前保持框架默认行为。
 * </p>
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


    /**
     * 构造 MVC 装配配置类（显式无参构造器，与编译器生成的默认构造器等价）。
     */
    public WebMvcConfiguration() {
    }

}
