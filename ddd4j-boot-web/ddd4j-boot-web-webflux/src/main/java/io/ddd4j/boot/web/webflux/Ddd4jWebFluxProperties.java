package io.ddd4j.boot.web.webflux;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ddd4j WebFlux 的 Spring Boot 配置属性。
 */
@Data
@ConfigurationProperties(prefix = "ddd4j.web.webflux")
public class Ddd4jWebFluxProperties {

    private boolean enabled = true;
}
