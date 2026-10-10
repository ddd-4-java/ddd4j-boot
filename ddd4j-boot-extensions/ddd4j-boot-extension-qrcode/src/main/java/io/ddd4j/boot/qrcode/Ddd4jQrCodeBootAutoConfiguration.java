package io.ddd4j.boot.qrcode;

import io.ddd4j.extension.qrcode.DefaultQrCodeService;
import io.ddd4j.extension.qrcode.QrCodeService;
import io.ddd4j.extension.qrcode.resource.QrCodeResourceResolver;
import io.ddd4j.extension.qrcode.template.InMemoryQrCodeTemplateRegistry;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateRegistry;
import io.ddd4j.extension.qrcode.template.QrCodeTemplateBinder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;

/**
 * 框架无关二维码应用服务的 Spring Boot 装配类。
 *
 * <p>在类路径存在 {@link QrCodeService} 且 {@code ddd4j.qrcode.enabled}（默认 true）时生效，
 * 注册默认二维码服务、模板注册表、模板绑定器与 Spring 资源解析器。
 */
@AutoConfiguration
@ConditionalOnClass(QrCodeService.class)
@ConditionalOnProperty(prefix = QrCodeProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(QrCodeProperties.class)
public class Ddd4jQrCodeBootAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public Ddd4jQrCodeBootAutoConfiguration() {
    }

    /**
     * 注册默认二维码服务（并发度与批量上限取自 {@link QrCodeProperties}），关闭时自动 close。
     *
     * @param properties 二维码配置属性
     * @return 默认 {@link DefaultQrCodeService} 实例
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(QrCodeService.class)
    public DefaultQrCodeService qrCodeService(QrCodeProperties properties) {
        // 上游 DefaultQrCodeService 仅提供 (int concurrency, int maxBatchSize) 构造
        return new DefaultQrCodeService(properties.getConcurrency(), properties.getMaxBatchSize());
    }

    /**
     * 注册内存版二维码模板注册表（用户可自定义 Bean 覆盖）。
     *
     * @return {@link QrCodeTemplateRegistry} 实例
     */
    @Bean
    @ConditionalOnMissingBean(QrCodeTemplateRegistry.class)
    public QrCodeTemplateRegistry qrCodeTemplateRegistry() {
        return new InMemoryQrCodeTemplateRegistry();
    }

    /**
     * 注册二维码模板绑定器（用户可自定义 Bean 覆盖）。
     *
     * @return {@link QrCodeTemplateBinder} 实例
     */
    @Bean
    @ConditionalOnMissingBean(QrCodeTemplateBinder.class)
    public QrCodeTemplateBinder qrCodeTemplateBinder() {
        return new QrCodeTemplateBinder();
    }

    /**
     * 注册基于 Spring {@link ResourceLoader} 的二维码资源解析器。
     *
     * @param resourceLoader Spring 资源加载器
     * @return {@link QrCodeResourceResolver} 实例
     */
    @Bean
    @ConditionalOnMissingBean(QrCodeResourceResolver.class)
    public QrCodeResourceResolver qrCodeResourceResolver(ResourceLoader resourceLoader) {
        return new SpringResourceQrCodeResolver(resourceLoader);
    }
}
