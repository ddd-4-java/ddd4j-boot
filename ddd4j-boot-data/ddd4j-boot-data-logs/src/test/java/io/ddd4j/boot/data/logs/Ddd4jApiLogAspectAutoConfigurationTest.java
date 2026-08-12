package io.ddd4j.boot.data.logs;

import cn.hutool.core.lang.Snowflake;
import io.ddd4j.data.logs.ApiOperationLogProvider;
import io.ddd4j.data.logs.aspect.ApiOperationLogAspect;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jApiLogAspectAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 用户 Bean 覆盖。
 */
class Ddd4jApiLogAspectAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jApiLogAspectAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideLogAspectBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(ApiOperationLogProvider.class);
            assertThat(context).hasSingleBean(Snowflake.class);
            assertThat(context).hasSingleBean(ApiOperationLogAspect.class);
        });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.logs.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ApiOperationLogAspect.class));
    }

    @Test
    void customProviderShouldTakePrecedence() {
        runner.withUserConfiguration(CustomProviderConfiguration.class)
                .run(context -> assertThat(context.getBean(ApiOperationLogProvider.class))
                        .isSameAs(context.getBean("customProvider")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomProviderConfiguration {

        @Bean
        ApiOperationLogProvider customProvider() {
            return new io.ddd4j.data.logs.DefaultApiOperationLogProvider();
        }
    }
}
