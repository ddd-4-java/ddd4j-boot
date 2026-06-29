package io.ddd4j.boot.auth.license;

import io.ddd4j.auth.license.LicenseProperties;
import io.ddd4j.auth.license.LicenseVerify;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * License Spring Boot 自动装配。
 *
 * <p>纯 Java 实现来自 {@code io.ddd4j:ddd4j-auth-license}（LicenseVerify / LicenseProperties 等），
 * 本类仅提供 Spring Boot {@link Configuration} 装配。
 *
 * @author ddd4j
 * @since 4.0.x
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({LicenseVerify.class})
@EnableConfigurationProperties({LicenseProperties.class})
public class DefaultLicenseAutoConfiguration {

    @Bean(initMethod = "installLicense", destroyMethod = "unInstallLicense")
    public LicenseVerify licenseVerify(LicenseProperties properties) {
        return new LicenseVerify(properties.getSubject(), properties.getPublicAlias(), properties.getStorePass(),
                properties.getLicensePath(), properties.getPublicKeysStorePath());
    }

}
