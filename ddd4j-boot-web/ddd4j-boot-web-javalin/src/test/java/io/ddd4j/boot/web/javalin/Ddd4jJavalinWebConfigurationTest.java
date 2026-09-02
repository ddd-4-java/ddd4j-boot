package io.ddd4j.boot.web.javalin;

import io.ddd4j.web.core.auth.AuthenticationMode;
import io.ddd4j.web.core.auth.BearerSubjectAuthenticator;
import io.ddd4j.web.core.context.WebRequestContextFactory;
import io.ddd4j.web.core.context.WebRequestLifecycle;
import io.ddd4j.web.core.error.WebExceptionTranslator;
import io.ddd4j.web.core.idempotency.WebIdempotencyLifecycle;
import io.ddd4j.web.javalin.Ddd4jJavalinWeb;
import io.javalin.Javalin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jJavalinWebConfiguration} 契约测试。
 *
 * <p>{@code WebEnvironment.NONE} 等价于 {@code WebApplicationType.NONE}：
 * 以非 Web 应用类型启动完整上下文验证自动装配；其余场景通过
 * {@link ApplicationContextRunner} 验证条件装配与默认属性。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = Ddd4jJavalinWebConfiguration.class)
class Ddd4jJavalinWebConfigurationTest {

    @Autowired
    private Ddd4jJavalinWeb ddd4jJavalinWeb;

    @Autowired
    private Ddd4jJavalinWebProperties properties;

    @Autowired
    private WebRequestContextFactory webRequestContextFactory;

    @Autowired
    private WebIdempotencyLifecycle webIdempotencyLifecycle;

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jJavalinWebConfiguration.class));

    @Test
    void shouldLoadInNonWebApplication() {
        assertThat(ddd4jJavalinWeb).isNotNull();
        assertThat(webRequestContextFactory).isNotNull();
        assertThat(webIdempotencyLifecycle).isNotNull();
    }

    @Test
    void shouldLoadConfigurationAndExposeDefaults() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(Ddd4jJavalinWebConfiguration.class);
            assertThat(context).hasSingleBean(Ddd4jJavalinWeb.class);
            assertThat(context).hasSingleBean(BearerSubjectAuthenticator.class);
            assertThat(context).hasSingleBean(WebExceptionTranslator.class);
            assertThat(context).hasSingleBean(WebRequestLifecycle.class);
            assertThat(context).hasBean("ddd4jWebHealthIndicator");
        });
    }

    @Test
    void shouldExposeDefaultProperties() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            Ddd4jJavalinWebProperties resolved = context.getBean(Ddd4jJavalinWebProperties.class);
            assertThat(resolved.getPublicPaths()).containsExactly("/health", "/health/readiness",
                    "/health/liveness");
            assertThat(resolved.getDefaultAuthenticationMode()).isEqualTo(AuthenticationMode.REQUIRED);
            assertThat(resolved.isTrustForwardedHeaders()).isFalse();
            assertThat(resolved.isIdempotencyEnabled()).isTrue();
            assertThat(resolved.getIdempotencyCacheName()).isEqualTo("ddd4j-web-idempotency");
            assertThat(resolved.getIdempotencyTtl()).isEqualTo(Duration.ofMinutes(5));
        });
    }

    @Test
    void shouldBackOffWhenJavalinClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(Javalin.class, Ddd4jJavalinWeb.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jJavalinWebConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(Ddd4jJavalinWeb.class);
                });
    }

    @Test
    void shouldBackOffWhenDisabled() {
        runner.withPropertyValues("ddd4j.web.javalin.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(Ddd4jJavalinWeb.class));
    }

    @Test
    void customJavalinWebShouldTakePrecedence() {
        runner.withUserConfiguration(CustomJavalinWebConfiguration.class)
                .run(context -> assertThat(context.getBean(Ddd4jJavalinWeb.class))
                        .isSameAs(context.getBean("customDdd4jJavalinWeb")));
    }

    @Test
    void shouldHonorPropertyOverrides() {
        runner.withPropertyValues("ddd4j.web.javalin.trust-forwarded-headers=true",
                        "ddd4j.web.javalin.public-paths=/a,/b")
                .run(context -> {
                    Ddd4jJavalinWebProperties resolved = context.getBean(Ddd4jJavalinWebProperties.class);
                    assertThat(resolved.isTrustForwardedHeaders()).isTrue();
                    assertThat(resolved.getPublicPaths()).containsExactly("/a", "/b");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomJavalinWebConfiguration {

        @Bean
        Ddd4jJavalinWeb customDdd4jJavalinWeb() {
            return new Ddd4jJavalinWeb();
        }
    }
}
