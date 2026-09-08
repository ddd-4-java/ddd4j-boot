package io.ddd4j.boot.auth.license;

import io.ddd4j.auth.license.LicenseProperties;
import io.ddd4j.auth.license.LicenseVerify;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * License Spring Boot 自动装配。
 *
 * <p>纯 Java 实现来自 {@code io.ddd4j:ddd4j-auth-license}（LicenseVerify / LicenseProperties 等），
 * 本类仅提供 Spring Boot {@link Configuration} 装配。
 *
 * <p>{@link LicenseProperties} 是上游零 Spring 依赖纯 POJO（不标注 {@code @ConfigurationProperties}），
 * 因此不能用 {@code @EnableConfigurationProperties}（启动期抛 "No ConfigurationProperties annotation found"），
 * 这里用 {@link Binder} 手动绑定。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({LicenseVerify.class})
public class DefaultLicenseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(LicenseProperties.class)
    public LicenseProperties licenseProperties(Environment environment) {
        LicenseProperties properties = new LicenseProperties();
        Binder.get(environment).bind(LicenseProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    @Bean(initMethod = "installLicense", destroyMethod = "unInstallLicense")
    public LicenseVerify licenseVerify(LicenseProperties properties) {
        return new LicenseVerify(properties.getSubject(), properties.getPublicAlias(), properties.getStorePass(),
                properties.getLicensePath(), properties.getPublicKeysStorePath(), properties.getSignatureAlgorithm());
    }

}
