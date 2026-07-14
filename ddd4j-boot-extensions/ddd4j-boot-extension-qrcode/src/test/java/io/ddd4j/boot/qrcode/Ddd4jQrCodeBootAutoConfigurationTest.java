package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.DefaultQrCodeService;
import io.ddd4j.extension.qrcode.QrCodeService;
import io.ddd4j.extension.qrcode.resource.QrCodeResourceResolver;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateRegistry;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateBinder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class Ddd4jQrCodeBootAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jQrCodeBootAutoConfiguration.class));

    @Test
    void shouldCreateDefaultBeans() {
        runner.run(context -> {
            assertThat(context).hasSingleBean(QrCodeService.class);
            assertThat(context).hasSingleBean(QrCodeTemplateRegistry.class);
            assertThat(context).hasSingleBean(QrCodeTemplateBinder.class);
            assertThat(context).hasSingleBean(QrCodeResourceResolver.class);
        });
    }

    @Test
    void shouldDisableAllBeans() {
        runner.withPropertyValues("ddd4j.qrcode.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(QrCodeService.class));
    }

    @Test
    void shouldKeepCustomService() {
        runner.withUserConfiguration(CustomServiceConfiguration.class)
                .run(context -> assertThat(context.getBean(QrCodeService.class))
                        .isSameAs(context.getBean("customQrCodeService")));
    }

    @Configuration(proxyBeanMethods = false)
    static class CustomServiceConfiguration {

        @Bean(destroyMethod = "close")
        DefaultQrCodeService customQrCodeService() {
            return new DefaultQrCodeService();
        }
    }
}
