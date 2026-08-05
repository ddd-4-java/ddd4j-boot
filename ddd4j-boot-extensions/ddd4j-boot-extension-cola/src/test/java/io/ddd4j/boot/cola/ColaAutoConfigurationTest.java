package io.ddd4j.boot.cola;

import io.ddd4j.boot.cola.handler.Ddd4jResponseHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ColaAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 缺类回退 / 用户 Bean 覆盖。
 */
class ColaAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ColaAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldRegisterResponseHandler() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(Ddd4jResponseHandler.class);
        });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.cola.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(Ddd4jResponseHandler.class));
    }

    @Test
    void shouldBackOffWhenColaMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("com.alibaba.cola.extension.Extension"))
                .withConfiguration(AutoConfigurations.of(ColaAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(Ddd4jResponseHandler.class);
                });
    }

    @Test
    void customResponseHandlerShouldTakePrecedence() {
        runner.withUserConfiguration(CustomResponseHandlerConfiguration.class)
                .run(context -> assertThat(context.getBean(Ddd4jResponseHandler.class))
                        .isSameAs(context.getBean("customResponseHandler")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomResponseHandlerConfiguration {

        @Bean
        Ddd4jResponseHandler customResponseHandler() {
            return new Ddd4jResponseHandler();
        }
    }
}
