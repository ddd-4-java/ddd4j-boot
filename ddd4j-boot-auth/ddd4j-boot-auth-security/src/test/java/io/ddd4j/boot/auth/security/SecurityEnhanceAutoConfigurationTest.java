package io.ddd4j.boot.auth.security;

import io.ddd4j.auth.security.handler.SecurityExceptionHandler;
import io.ddd4j.core.subject.SubjectProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SecurityEnhanceAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / Servlet 环境异常处理器 / 缺类回退 / 用户 Bean 覆盖。
 */
class SecurityEnhanceAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SecurityEnhanceAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideSecurityBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(PasswordEncoder.class);
            assertThat(context).hasSingleBean(SubjectProvider.class);
            // 非 Servlet 环境不装配 Servlet 专用异常处理器
            assertThat(context).doesNotHaveBean(SecurityExceptionHandler.class);
        });
    }

    @Test
    void shouldRegisterExceptionHandlerInServletEnvironment() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(SecurityEnhanceAutoConfiguration.class))
                .run(context -> assertThat(context).hasSingleBean(SecurityExceptionHandler.class));
    }

    @Test
    void shouldBackOffWhenSecurityContextMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("org.springframework.security.core.context.SecurityContextHolder"))
                .withConfiguration(AutoConfigurations.of(SecurityEnhanceAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(PasswordEncoder.class);
                });
    }

    @Test
    void customPasswordEncoderShouldTakePrecedence() {
        runner.withUserConfiguration(CustomPasswordEncoderConfiguration.class)
                .run(context -> assertThat(context.getBean(PasswordEncoder.class))
                        .isSameAs(context.getBean("customPasswordEncoder")));
    }

    @Test
    void customSubjectProviderShouldTakePrecedence() {
        runner.withUserConfiguration(CustomSubjectProviderConfiguration.class)
                .run(context -> assertThat(context.getBean(SubjectProvider.class))
                        .isSameAs(context.getBean("customSubjectProvider")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomPasswordEncoderConfiguration {

        @Bean
        PasswordEncoder customPasswordEncoder() {
            return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }
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
