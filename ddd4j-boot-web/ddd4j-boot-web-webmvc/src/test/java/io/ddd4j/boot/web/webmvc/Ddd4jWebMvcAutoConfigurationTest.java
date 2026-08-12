package io.ddd4j.boot.web.webmvc;

import io.ddd4j.web.webmvc.Ddd4jWebMvcInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jWebMvcAutoConfiguration} 契约测试。
 *
 * <p>覆盖：Servlet 环境默认装配 / 非 Servlet 回退 / 缺类回退 / 用户 Bean 覆盖。
 */
class Ddd4jWebMvcAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jWebMvcAutoConfiguration.class));

    @Test
    void shouldLoadForServletApplication() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(Ddd4jWebMvcInterceptor.class);
            assertThat(context).hasBean("ddd4jWebHealthIndicator");
        });
    }

    @Test
    void shouldBackOffOutsideServletApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebMvcAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean("ddd4jWebMvcInterceptor"));
    }

    @Test
    void shouldBackOffWhenServletClassMissing() {
        new WebApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(DispatcherServlet.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jWebMvcAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean("ddd4jWebMvcInterceptor");
                });
    }

    @Test
    void customLocaleInterceptorShouldTakePrecedence() {
        runner.withUserConfiguration(CustomLocaleInterceptorConfiguration.class)
                .run(context -> assertThat(context.getBean(LocaleChangeInterceptor.class))
                        .isSameAs(context.getBean("customLocaleInterceptor")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomLocaleInterceptorConfiguration {

        @Bean
        LocaleChangeInterceptor customLocaleInterceptor() {
            return new LocaleChangeInterceptor();
        }
    }
}
