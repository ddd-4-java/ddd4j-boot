package io.ddd4j.boot.data.external.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jExternalAutoConfiguration} / {@link Ddd4jGlobalSequenceAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 用户 Bean 覆盖 / 全局序列号装配。
 */
class Ddd4jExternalAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    Ddd4jExternalAutoConfiguration.class, Ddd4jGlobalSequenceAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideExternalBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(HttpClient.class);
            assertThat(context).hasBean("globalSequence");
        });
    }

    @Test
    void customHttpClientShouldTakePrecedence() {
        runner.withUserConfiguration(CustomHttpClientConfiguration.class)
                .run(context -> assertThat(context.getBean(HttpClient.class))
                        .isSameAs(context.getBean("customHttpClient")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomHttpClientConfiguration {

        @Bean
        HttpClient customHttpClient() {
            return HttpClient.newHttpClient();
        }
    }
}
