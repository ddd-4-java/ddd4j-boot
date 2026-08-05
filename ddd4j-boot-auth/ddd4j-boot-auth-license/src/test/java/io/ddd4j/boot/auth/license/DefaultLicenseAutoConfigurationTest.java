package io.ddd4j.boot.auth.license;

import io.ddd4j.auth.license.LicenseVerify;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link DefaultLicenseAutoConfiguration} 契约测试。
 *
 * <p>覆盖：默认装配 / 缺类回退。
 *
 * <p>证书文件缺失时 {@link LicenseVerify#installLicense()} 内部捕获异常并记录日志，
 * 上下文仍可正常启动（契约锁定该容错行为）。
 */
class DefaultLicenseAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DefaultLicenseAutoConfiguration.class));

    @Test
    void defaultAssemblyShouldCreateLicenseVerify() {
        runner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(LicenseVerify.class);
        });
    }

    @Test
    void shouldBackOffWhenLicenseVerifyMissing() {
        new ApplicationContextRunner()
                .withClassLoader(new FilteredClassLoader(LicenseVerify.class))
                .withConfiguration(AutoConfigurations.of(DefaultLicenseAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(LicenseVerify.class);
                });
    }
}
