package io.ddd4j.boot.web.webflux;

import io.ddd4j.web.webflux.Ddd4jWebFluxFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ddd4jWebFluxAutoConfigurationTest {

    @Test
    void shouldLoadForReactiveApplication() {
        new ReactiveWebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> {
                    assertTrue(context.containsBean("ddd4jWebFluxFilter"));
                    assertTrue(context.containsBean("ddd4jWebHealthIndicator"));
                    assertTrue(context.getBean(Ddd4jWebFluxFilter.class) instanceof Ddd4jWebFluxFilter);
                });
    }

    @Test
    void shouldBackOffWhenDisabled() {
        new ReactiveWebApplicationContextRunner()
                .withPropertyValues("ddd4j.web.webflux.enabled=false")
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> assertFalse(context.containsBean("ddd4jWebFluxFilter")));
    }

    @Test
    void shouldBackOffOutsideReactiveApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> assertFalse(context.containsBean("ddd4jWebFluxFilter")));
    }
}
