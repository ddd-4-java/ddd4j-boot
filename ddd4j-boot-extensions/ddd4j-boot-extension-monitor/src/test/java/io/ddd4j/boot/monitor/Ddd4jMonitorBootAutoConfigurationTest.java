package io.ddd4j.boot.monitor;

import io.ddd4j.extension.monitor.config.BaseMonitorProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jMonitorBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退 / 用户 Bean 覆盖。
 */
class Ddd4jMonitorBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMonitorBootAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldBindMonitorProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(BaseMonitorProperties.class);
        });
    }

    @Test
    void shouldBindPropertiesWhenLogbackMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("ch.qos.logback.classic.LoggerContext"))
                .withConfiguration(AutoConfigurations.of(Ddd4jMonitorBootAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(BaseMonitorProperties.class);
                });
    }

    @Test
    void customPropertiesShouldTakePrecedence() {
        runner.withUserConfiguration(CustomPropertiesConfiguration.class)
                .run(context -> assertThat(context.getBean(BaseMonitorProperties.class))
                        .isSameAs(context.getBean("customMonitorProperties")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomPropertiesConfiguration {

        @Bean
        BaseMonitorProperties customMonitorProperties() {
            return new BaseMonitorProperties();
        }
    }
}
