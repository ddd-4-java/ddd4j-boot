package io.ddd4j.boot.auth.shiro;

import io.ddd4j.auth.spring.shiro.ShiroExceptionHandler;
import io.ddd4j.core.subject.SubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ShiroEnhanceAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / Servlet 环境异常处理器 / 缺类回退 / 用户 Bean 覆盖。
 */
class ShiroEnhanceAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ShiroEnhanceAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideShiroBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(SubjectProvider.class);
            // 非 Servlet 环境不装配 Servlet 专用异常处理器
            assertThat(context).doesNotHaveBean(ShiroExceptionHandler.class);
        });
    }

    @Test
    void shouldRegisterExceptionHandlerInServletEnvironment() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ShiroEnhanceAutoConfiguration.class))
                .run(context -> assertThat(context).hasSingleBean(ShiroExceptionHandler.class));
    }

    @Test
    void shouldBackOffWhenSecurityUtilsMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("org.apache.shiro.SecurityUtils"))
                .withConfiguration(AutoConfigurations.of(ShiroEnhanceAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(SubjectProvider.class);
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
