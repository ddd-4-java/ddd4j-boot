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

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public DefaultLicenseAutoConfiguration() {
    }

    /**
     * 手动绑定 {@code ddd4j.license.*} 到上游纯 POJO {@link LicenseProperties}。
     *
     * @param environment Spring 环境，提供配置来源
     * @return 绑定完成的 License 属性实例
     */
    @Bean
    @ConditionalOnMissingBean(LicenseProperties.class)
    public LicenseProperties licenseProperties(Environment environment) {
        LicenseProperties properties = new LicenseProperties();
        Binder.get(environment).bind(LicenseProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    /**
     * 注册 License 校验器：初始化时安装 License，容器销毁时卸载。
     *
     * @param properties 已绑定的 License 属性
     * @return License 校验器实例
     */
    @Bean(initMethod = "installLicense", destroyMethod = "unInstallLicense")
    public LicenseVerify licenseVerify(LicenseProperties properties) {
        return new LicenseVerify(properties.getSubject(), properties.getPublicAlias(), properties.getStorePass(),
                properties.getLicensePath(), properties.getPublicKeysStorePath(), properties.getSignatureAlgorithm());
    }

}
