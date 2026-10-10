package io.ddd4j.boot.data.datascope;

import io.ddd4j.data.datascope.DataScopeProvider;
import io.ddd4j.data.datascope.RequiresDataPermissionsValidator;
import io.ddd4j.spring.config.SpringCoreConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * ddd4j 数据权限（DataScope）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-datascope} 迁入 boot 层。
 * 通用层仅保留纯 Java 的 DataScopeProvider 接口和 @RequiresDataPermissions 注解。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@Import(SpringCoreConfig.class)
@ConditionalOnProperty(prefix = "ddd4j.datascope", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Ddd4jDataScopeAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public Ddd4jDataScopeAutoConfiguration() {
    }

    /**
     * 注册默认数据权限提供者（缺省放行非空数据范围）。
     *
     * @return 数据权限提供者实例
     */
    @Bean
    @ConditionalOnMissingBean(DataScopeProvider.class)
    public DataScopeProvider dataScopeProvider() {
        return DataScopeProvider.nonNullAllowed();
    }

    /**
     * 注册 {@code @RequiresDataPermissions} 注解校验器。
     *
     * @param provider 数据权限提供者
     * @return 数据权限校验器实例
     */
    @Bean
    @ConditionalOnMissingBean(RequiresDataPermissionsValidator.class)
    public RequiresDataPermissionsValidator requiresDataPermissionsValidator(DataScopeProvider provider) {
        return new RequiresDataPermissionsValidator(provider);
    }

}
