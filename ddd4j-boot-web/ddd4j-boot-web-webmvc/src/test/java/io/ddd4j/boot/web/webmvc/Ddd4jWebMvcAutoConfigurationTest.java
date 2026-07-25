package io.ddd4j.boot.web.webmvc;

import io.ddd4j.web.webmvc.Ddd4jWebMvcInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Ddd4jWebMvcAutoConfigurationTest {

    @Test
    void shouldLoadForServletApplication() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebMvcAutoConfiguration.class))
                .run(context -> {
                    assertTrue(context.containsBean("ddd4jWebMvcInterceptor"));
                    assertTrue(context.containsBean("ddd4jWebHealthIndicator"));
                    assertTrue(context.getBean(Ddd4jWebMvcInterceptor.class) instanceof Ddd4jWebMvcInterceptor);
                });
    }

    @Test
    void shouldBackOffOutsideServletApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebMvcAutoConfiguration.class))
                .run(context -> assertFalse(context.containsBean("ddd4jWebMvcInterceptor")));
    }
}
