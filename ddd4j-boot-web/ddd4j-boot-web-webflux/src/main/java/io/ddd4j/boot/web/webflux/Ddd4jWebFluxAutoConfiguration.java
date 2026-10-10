package io.ddd4j.boot.web.webflux;

import tools.jackson.databind.ObjectMapper;
import io.ddd4j.web.webflux.DefaultMessageSourceConfiguration;
import io.ddd4j.web.webflux.DefaultSequenceConfiguration;
import io.ddd4j.web.webflux.DefaultWebFluxConfiguration;
import io.ddd4j.web.webflux.config.LocalResourceProperteis;
import io.ddd4j.web.webflux.config.SequenceProperties;
import io.ddd4j.web.webflux.error.GlobalErrorAttributes;
import io.ddd4j.web.webflux.error.GlobalErrorWebExceptionHandler;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.DispatcherHandler;

/**
 * ddd4j WebFlux 的 Spring Boot 条件装配入口。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({DispatcherHandler.class, DefaultWebFluxConfiguration.class})
@ConditionalOnProperty(prefix = "ddd4j.web.webflux", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Ddd4jWebFluxProperties.class)
@Import({DefaultMessageSourceConfiguration.class, DefaultSequenceConfiguration.class,
        DefaultWebFluxConfiguration.class, GlobalErrorAttributes.class, GlobalErrorWebExceptionHandler.class})
public class Ddd4jWebFluxAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public Ddd4jWebFluxAutoConfiguration() {
    }

    /**
     * 注册 Jackson {@link ObjectMapper}（用户可通过同类型 Bean 覆盖）。
     *
     * @return JSON 序列化器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * 注册本地静态资源属性绑定 Bean。
     *
     * @return 静态资源属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalResourceProperteis localResourceProperteis() {
        return new LocalResourceProperteis();
    }

    /**
     * 注册分布式序列属性绑定 Bean。
     *
     * @return 序列属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SequenceProperties sequenceProperties() {
        return new SequenceProperties();
    }

    /**
     * 注册 Web 运行时健康指示器（runtime=spring-webflux）。
     *
     * @return 健康指示器实例
     */
    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "spring-webflux").build();
    }
}
