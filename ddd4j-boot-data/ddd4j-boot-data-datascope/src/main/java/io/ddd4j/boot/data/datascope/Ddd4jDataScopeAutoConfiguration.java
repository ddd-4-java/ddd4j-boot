package io.ddd4j.boot.data.datascope;

import io.ddd4j.data.datascope.DataScopeProvider;
import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ddd4j 数据权限（DataScope）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-datascope} 迁入 boot 层。
 * 通用层仅保留纯 Java 的 DataScopeProvider 接口和 @RequiresDataPermissions 注解。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "ddd4j.datascope", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Ddd4jDataScopeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataScopeProvider.class)
    public DataScopeProvider dataScopeProvider() {
        return new DataScopeProvider() {
        };
    }

    @Bean
    @ConditionalOnMissingBean(SpringContextAwareContext.class)
    public SpringContextAwareContext springContextAwareContext() {
        return new SpringContextAwareContext();
    }

}
