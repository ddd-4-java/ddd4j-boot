package io.ddd4j.boot.web.vertx;

import io.ddd4j.web.core.auth.AuthenticationMode;
import io.ddd4j.web.core.auth.BearerSubjectAuthenticator;
import io.ddd4j.web.core.context.WebRequestContextFactory;
import io.ddd4j.web.core.context.WebRequestLifecycle;
import io.ddd4j.web.core.error.WebExceptionTranslator;
import io.ddd4j.web.core.idempotency.WebIdempotencyLifecycle;
import io.ddd4j.web.vertx.Ddd4jVertxWeb;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jVertxWebConfiguration} 契约测试。
 *
 * <p>{@code WebEnvironment.NONE} 等价于 {@code WebApplicationType.NONE}：
 * 以非 Web 应用类型启动完整上下文验证自动装配；其余场景通过
 * {@link ApplicationContextRunner} 验证条件装配与默认属性。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = Ddd4jVertxWebConfiguration.class)
class Ddd4jVertxWebConfigurationTest {

    @Autowired
    private Ddd4jVertxWeb ddd4jVertxWeb;

    @Autowired
    private Ddd4jVertxWebProperties properties;

    @Autowired
    private WebRequestContextFactory webRequestContextFactory;

    @Autowired
    private WebIdempotencyLifecycle webIdempotencyLifecycle;

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jVertxWebConfiguration.class));

    @Test
    void shouldLoadInNonWebApplication() {
        assertThat(ddd4jVertxWeb).isNotNull();
        assertThat(webRequestContextFactory).isNotNull();
        assertThat(webIdempotencyLifecycle).isNotNull();
    }

    @Test
    void shouldLoadConfigurationAndExposeDefaults() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(Ddd4jVertxWebConfiguration.class);
            assertThat(context).hasSingleBean(Ddd4jVertxWeb.class);
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
            Ddd4jVertxWebProperties resolved = context.getBean(Ddd4jVertxWebProperties.class);
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
    void shouldBackOffWhenVertxClassMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(Vertx.class, Ddd4jVertxWeb.class))
                .withConfiguration(AutoConfigurations.of(Ddd4jVertxWebConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(Ddd4jVertxWeb.class);
                });
    }

    @Test
    void shouldBackOffWhenDisabled() {
        runner.withPropertyValues("ddd4j.web.vertx.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(Ddd4jVertxWeb.class));
    }

    @Test
    void customVertxWebShouldTakePrecedence() {
        runner.withUserConfiguration(CustomVertxWebConfiguration.class)
                .run(context -> assertThat(context.getBean(Ddd4jVertxWeb.class))
                        .isSameAs(context.getBean("customDdd4jVertxWeb")));
    }

    @Test
    void shouldHonorPropertyOverrides() {
        runner.withPropertyValues("ddd4j.web.vertx.trust-forwarded-headers=true",
                        "ddd4j.web.vertx.public-paths=/a,/b")
                .run(context -> {
                    Ddd4jVertxWebProperties resolved = context.getBean(Ddd4jVertxWebProperties.class);
                    assertThat(resolved.isTrustForwardedHeaders()).isTrue();
                    assertThat(resolved.getPublicPaths()).containsExactly("/a", "/b");
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomVertxWebConfiguration {

        @Bean
        Ddd4jVertxWeb customDdd4jVertxWeb() {
            return new Ddd4jVertxWeb();
        }
    }
}
