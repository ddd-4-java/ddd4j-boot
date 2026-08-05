package io.ddd4j.boot.excel;

import io.ddd4j.boot.excel.config.ExcelProperties;
import io.ddd4j.boot.excel.web.ExcelHttpKit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link Ddd4jExcelBootAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配（enabled matchIfMissing=true）/ 显式关闭 / 缺类回退。
 */
class Ddd4jExcelBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jExcelBootAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldBindPropertiesAndWebKit() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jExcelBootAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(ExcelProperties.class);
                    assertThat(context).hasSingleBean(ExcelHttpKit.class);
                });
    }

    @Test
    void shouldStayOffWhenExplicitlyDisabled() {
        runner.withPropertyValues("ddd4j.excel.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(ExcelProperties.class));
    }

    @Test
    void shouldBackOffWhenEasyExcelMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader("com.alibaba.excel.EasyExcel"))
                .withConfiguration(AutoConfigurations.of(Ddd4jExcelBootAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(ExcelProperties.class);
                });
    }
}
