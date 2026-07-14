package io.ddd4j.boot.web.webmvc;

import io.ddd4j.core.BaseCoreProperties;
import io.ddd4j.web.webmvc.config.BaseWebConfig;
import io.ddd4j.web.webmvc.config.LocalResourceProperteis;
import io.ddd4j.web.webmvc.config.SequenceProperties;
import io.ddd4j.web.webmvc.webmvc.DefaultMessageSourceConfiguration;
import io.ddd4j.web.webmvc.webmvc.DefaultSequenceConfiguration;
import io.ddd4j.web.webmvc.webmvc.DefaultWebMvcConfiguration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * ddd4j WebMVC 的 Spring Boot 条件装配入口。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class, DefaultWebMvcConfiguration.class})
@EnableConfigurationProperties(Ddd4jWebMvcProperties.class)
@Import({DefaultMessageSourceConfiguration.class, DefaultSequenceConfiguration.class,
        DefaultWebMvcConfiguration.class, BaseWebConfig.class})
public class Ddd4jWebMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BaseCoreProperties baseCoreProperties() {
        return new BaseCoreProperties();
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
        return () -> Health.up().withDetail("runtime", "spring-webmvc").build();
    }
}
