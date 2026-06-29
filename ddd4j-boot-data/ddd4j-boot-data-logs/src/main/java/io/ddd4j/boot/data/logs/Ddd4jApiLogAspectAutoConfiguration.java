package io.ddd4j.boot.data.logs;

import io.ddd4j.data.logs.aspect.ApiOperationLogProvider;
import io.ddd4j.data.logs.aspect.DefaultApiOperationLogProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ddd4j API 操作日志 Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-logs} 迁入 boot 层。
 * 通用层仅保留纯 AspectJ 的切面实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "ddd4j.logs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class Ddd4jApiLogAspectAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ApiOperationLogProvider.class)
    public ApiOperationLogProvider apiOperationLogProvider() {
        return new DefaultApiOperationLogProvider();
    }

}
