package io.ddd4j.boot.web.webflux;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.ddd4j.boot.core.Ddd4jCoreAutoConfiguration;
import io.ddd4j.web.webflux.Ddd4jWebFluxFilter;
import io.ddd4j.web.webflux.DefaultWebFluxConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.DispatcherHandler;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jWebFluxAutoConfiguration} 契约测试。
 *
 * <p>覆盖：Reactive 环境默认装配 / enabled=false / 非 Reactive 回退 / 缺类回退 / 用户 Bean 覆盖。
 *
 * <p>默认装配需与 {@link Ddd4jCoreAutoConfiguration} 协同：上游 {@code DefaultWebFluxConfiguration}
 * 的 readiness controller 依赖 core 的 {@code RuntimeReadinessRegistry} Bean。
 */
class Ddd4jWebFluxAutoConfigurationTest {

    private final ReactiveWebApplicationContextRunner runner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jCoreAutoConfiguration.class, Ddd4jWebFluxAutoConfiguration.class));

    @Test
    void shouldLoadForReactiveApplication() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasBean("ddd4jWebFluxFilter");
            assertThat(context).hasBean("ddd4jWebHealthIndicator");
            assertThat(context).hasSingleBean(Ddd4jWebFluxFilter.class);
        });
    }

    @Test
    void shouldBackOffWhenDisabled() {
        new ReactiveWebApplicationContextRunner()
                .withPropertyValues("ddd4j.web.webflux.enabled=false")
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean("ddd4jWebFluxFilter"));
    }

    @Test
    void shouldBackOffOutsideReactiveApplication() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean("ddd4jWebFluxFilter"));
    }

    @Test
    void shouldBackOffWhenReactiveClassMissing() {
        new ReactiveWebApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(DispatcherHandler.class, DefaultWebFluxConfiguration.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jWebFluxAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean("ddd4jWebFluxFilter");
                });
    }

    @Test
    void customObjectMapperShouldTakePrecedence() {
        runner.withUserConfiguration(CustomObjectMapperConfiguration.class)
                .run(context -> assertThat(context.getBean(ObjectMapper.class))
                        .isSameAs(context.getBean("customObjectMapper")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomObjectMapperConfiguration {

        @Bean
        ObjectMapper customObjectMapper() {
            return new ObjectMapper();
        }
    }
}
