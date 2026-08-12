package io.ddd4j.boot.data.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jMybatisAutoConfiguration} 契约测试。
 *
 * <p>覆盖：存在 DataSource 时默认装配 / 无 DataSource 回退 / 用户 Bean 覆盖。
 */
class Ddd4jMybatisAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jMybatisAutoConfiguration.class))
            .withUserConfiguration(DataSourceConfiguration.class);

    @Test
    void defaultAssemblyShouldCreateMybatisPlusInterceptor() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(MybatisPlusInterceptor.class);
        });
    }

    @Test
    void shouldBackOffWithoutDataSource() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jMybatisAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(MybatisPlusInterceptor.class);
                });
    }

    @Test
    void customInterceptorShouldTakePrecedence() {
        runner.withUserConfiguration(CustomInterceptorConfiguration.class)
                .run(context -> assertThat(context.getBean(MybatisPlusInterceptor.class))
                        .isSameAs(context.getBean("customInterceptor")));
    }

    @Configuration(proxyBeanMethods = false)
    static class DataSourceConfiguration {

        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomInterceptorConfiguration {

        @Bean
        MybatisPlusInterceptor customInterceptor() {
            return new MybatisPlusInterceptor();
        }
    }
}
