package io.ddd4j.boot.auth.satoken;

import io.ddd4j.auth.satoken.handler.SaInternalCheckHandler;
import io.ddd4j.auth.satoken.handler.SaMixCheckLoginHandler;
import io.ddd4j.core.subject.SubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SaTokenEnhanceAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退 / 用户 Bean 覆盖。
 */
class SaTokenEnhanceAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SaTokenEnhanceAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldRegisterAnnotationHandlers() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SaMixCheckLoginHandler.class);
            assertThat(context).hasSingleBean(SaInternalCheckHandler.class);
            assertThat(context).hasSingleBean(SubjectProvider.class);
            assertThat(context).hasBean("ddd4jSaTokenAnnotationHandlerRegistrar");
        });
    }

    @Test
    void shouldBackOffWhenStpUtilMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("cn.dev33.satoken.stp.StpUtil"))
                .withConfiguration(AutoConfigurations.of(SaTokenEnhanceAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SaMixCheckLoginHandler.class);
                });
    }

    @Test
    void customSubjectProviderShouldTakePrecedence() {
        runner.withUserConfiguration(CustomSubjectProviderConfiguration.class)
                .run(context -> assertThat(context.getBean(SubjectProvider.class))
                        .isSameAs(context.getBean("customSubjectProvider")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomSubjectProviderConfiguration {

        @Bean
        SubjectProvider customSubjectProvider() {
            return new SubjectProvider() {
            };
        }
    }
}
