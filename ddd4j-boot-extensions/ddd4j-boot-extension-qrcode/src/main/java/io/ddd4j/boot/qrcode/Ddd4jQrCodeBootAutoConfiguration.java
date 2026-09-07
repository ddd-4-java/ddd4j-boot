package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.DefaultQrCodeService;
import io.ddd4j.extension.qrcode.QrCodeService;
import io.ddd4j.extension.qrcode.resource.QrCodeResourceResolver;
import io.ddd4j.extension.qrcode.template.InMemoryQrCodeTemplateRegistry;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateRegistry;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateBinder;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

/** Spring Boot assembly for the framework-neutral QR code application service. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(QrCodeService.class)
@ConditionalOnProperty(prefix = QrCodeProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(QrCodeProperties.class)
public class Ddd4jQrCodeBootAutoConfiguration {

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(QrCodeService.class)
    public DefaultQrCodeService qrCodeService(QrCodeProperties properties) {
        // 上游 DefaultQrCodeService 仅提供 (int concurrency, int maxBatchSize) 构造
        return new DefaultQrCodeService(properties.getConcurrency(), properties.getMaxBatchSize());
    }

    @Bean
    @ConditionalOnMissingBean(QrCodeTemplateRegistry.class)
    public QrCodeTemplateRegistry qrCodeTemplateRegistry() {
        return new InMemoryQrCodeTemplateRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(QrCodeTemplateBinder.class)
    public QrCodeTemplateBinder qrCodeTemplateBinder() {
        return new QrCodeTemplateBinder();
    }

    @Bean
    @ConditionalOnMissingBean(QrCodeResourceResolver.class)
    public QrCodeResourceResolver qrCodeResourceResolver(ResourceLoader resourceLoader) {
        return new SpringResourceQrCodeResolver(resourceLoader);
    }
}
