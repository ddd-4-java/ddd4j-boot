package io.ddd4j.boot.web.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ddd4j.web.webflux.DefaultMessageSourceConfiguration;
import io.ddd4j.web.webflux.DefaultSequenceConfiguration;
import io.ddd4j.web.webflux.DefaultWebFluxConfiguration;
import io.ddd4j.web.webflux.config.LocalResourceProperteis;
import io.ddd4j.web.webflux.config.SequenceProperties;
import io.ddd4j.web.webflux.error.GlobalErrorAttributes;
import io.ddd4j.web.webflux.error.GlobalErrorWebExceptionHandler;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({DispatcherHandler.class, DefaultWebFluxConfiguration.class})
@ConditionalOnProperty(prefix = "ddd4j.web.webflux", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Ddd4jWebFluxProperties.class)
@Import({DefaultMessageSourceConfiguration.class, DefaultSequenceConfiguration.class,
        DefaultWebFluxConfiguration.class, GlobalErrorAttributes.class, GlobalErrorWebExceptionHandler.class})
public class Ddd4jWebFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalResourceProperteis localResourceProperteis() {
        return new LocalResourceProperteis();
    }

    @Bean
    @ConditionalOnMissingBean
    public SequenceProperties sequenceProperties() {
        return new SequenceProperties();
    }

    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "spring-webflux").build();
    }
}
