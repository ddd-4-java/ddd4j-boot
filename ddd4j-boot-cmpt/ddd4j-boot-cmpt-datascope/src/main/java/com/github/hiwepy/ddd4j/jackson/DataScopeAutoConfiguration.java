package com.github.hiwepy.ddd4j.jackson;

import com.github.hiwepy.ddd4j.jackson.datascope.DataScopeProvider;
import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DataScopeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DataScopeProvider dataScopeProvider() {
        return new DataScopeProvider() {
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringContextAwareContext springContextAwareContext() {
        return new SpringContextAwareContext();
    }

}
