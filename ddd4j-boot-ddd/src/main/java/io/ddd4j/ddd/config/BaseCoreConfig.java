package io.ddd4j.ddd.config;

import io.ddd4j.spring.context.SpringContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * ddd4j Boot 基础自动配置。
 *
 * <p>注册 {@link SpringContext} 为 Spring Bean，使非 Web 场景也能静态获取
 * ApplicationContext。
 */
@AutoConfiguration
@EnableAspectJAutoProxy(exposeProxy = true)
public class BaseCoreConfig {

    @Bean
    @ConditionalOnMissingBean(SpringContext.class)
    public SpringContext springContext() {
        return new SpringContext();
    }

}
