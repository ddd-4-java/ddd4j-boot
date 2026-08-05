package io.ddd4j.boot.qrcode;

import io.ddd4j.boot.qrcode.Ddd4jQrCodeBootAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link QrCodeWebAutoConfiguration} 契约测试。
 *
 * <p>覆盖：Web 环境显式开启后装配 / 未开启回退 / 非 Web 环境回退。
 */
class QrCodeWebAutoConfigurationTest {

    private final WebApplicationContextRunner webRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    Ddd4jQrCodeBootAutoConfiguration.class, QrCodeWebAutoConfiguration.class))
            .withPropertyValues("ddd4j.qrcode.web.enabled=true");

    @Test
    void shouldAssembleControllerInServletEnvironmentWhenEnabled() {
        webRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(QrCodeController.class);
            assertThat(context).hasSingleBean(QrCodeExceptionHandler.class);
        });
    }

    @Test
    void shouldBackOffWhenWebEnabledMissing() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        Ddd4jQrCodeBootAutoConfiguration.class, QrCodeWebAutoConfiguration.class))
                .run(context -> assertThat(context).doesNotHaveBean(QrCodeController.class));
    }

    @Test
    void shouldBackOffOutsideServletEnvironment() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        Ddd4jQrCodeBootAutoConfiguration.class, QrCodeWebAutoConfiguration.class))
                .withPropertyValues("ddd4j.qrcode.web.enabled=true")
                .run(context -> assertThat(context).doesNotHaveBean(QrCodeController.class));
    }
}
