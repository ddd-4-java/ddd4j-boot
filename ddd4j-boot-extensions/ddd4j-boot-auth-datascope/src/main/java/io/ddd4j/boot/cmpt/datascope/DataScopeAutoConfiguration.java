package io.ddd4j.boot.cmpt.datascope;

import org.springframework.extension.context.SpringContextAwareContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据权限（DataScope）自动装配：注册默认数据权限提供者与 Spring 上下文感知上下文。
 */
@Configuration(proxyBeanMethods = false)
public class DataScopeAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public DataScopeAutoConfiguration() {
    }

    /**
     * 注册默认空实现的 {@link DataScopeProvider}（用户可自定义 Bean 覆盖）。
     *
     * @return 空实现的数据权限提供者
     */
    @Bean
    @ConditionalOnMissingBean
    public DataScopeProvider dataScopeProvider() {
        return new DataScopeProvider() {
        };
    }

    /**
     * 注册基于 Spring 上下文的 {@link SpringContextAwareContext}（用户可自定义 Bean 覆盖）。
     *
     * @return Spring 上下文感知上下文实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringContextAwareContext springContextAwareContext() {
        return new SpringContextAwareContext();
    }

}
