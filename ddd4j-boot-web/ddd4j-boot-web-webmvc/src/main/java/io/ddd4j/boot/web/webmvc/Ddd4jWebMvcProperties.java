package io.ddd4j.boot.web.webmvc;

import io.ddd4j.web.webmvc.config.BaseWebProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ddd4j WebMVC 的 Spring Boot 配置属性。
 */
@ConfigurationProperties(prefix = "ddd4j.web.mvc")
public class Ddd4jWebMvcProperties extends BaseWebProperties {

    /**
     * 显式无参构造器，供 {@code @EnableConfigurationProperties} 绑定时实例化。
     */
    public Ddd4jWebMvcProperties() {
    }
}
