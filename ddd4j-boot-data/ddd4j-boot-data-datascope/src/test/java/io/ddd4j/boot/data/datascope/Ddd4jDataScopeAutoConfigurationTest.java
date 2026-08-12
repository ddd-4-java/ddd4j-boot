package io.ddd4j.boot.data.datascope;

import io.ddd4j.data.datascope.DataScopeProvider;
import io.ddd4j.data.datascope.RequiresDataPermissionsValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jDataScopeAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 用户 Bean 覆盖。
 */
class Ddd4jDataScopeAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jDataScopeAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldProvideDataScopeBeans() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(DataScopeProvider.class);
            assertThat(context).hasSingleBean(RequiresDataPermissionsValidator.class);
        });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.datascope.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(DataScopeProvider.class));
    }

    @Test
    void customProviderShouldTakePrecedence() {
        runner.withUserConfiguration(CustomProviderConfiguration.class)
                .run(context -> assertThat(context.getBean(DataScopeProvider.class))
                        .isSameAs(context.getBean("customDataScopeProvider")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomProviderConfiguration {

        @Bean
        DataScopeProvider customDataScopeProvider() {
            return DataScopeProvider.nonNullAllowed();
        }
    }
}
