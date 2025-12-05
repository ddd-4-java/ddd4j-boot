package com.github.hiwepy.ddd4j.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hiwepy.ddd4j.crypto.provider.CryptoProvider;
import com.github.hiwepy.ddd4j.crypto.provider.DefaultCryptoProvider;
import com.github.hiwepy.ddd4j.crypto.strategy.CryptoStrategy;
import com.github.hiwepy.ddd4j.crypto.strategy.DefaultCryptoStrategy;
import com.github.hiwepy.ddd4j.crypto.strategy.FlksecCryptoStrategy;
import com.github.hiwepy.ddd4j.crypto.strategy.NoOpCryptoStrategy;
import okhttp3.OkHttpClient;
import okhttp3.spring.boot.OkHttp3Template;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;

/**
 * @author wandl
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ObjectMapper.class})
@EnableConfigurationProperties(CryptoProperties.class)
public class DefaultCryptoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CryptoProvider.class)
    public DefaultCryptoProvider cryptoProvider(ObjectProvider<CryptoStrategy> cryptoStrategyProvider, CryptoProperties cryptoProperties) {
        return new DefaultCryptoProvider(cryptoStrategyProvider.stream().collect(Collectors.toList()), cryptoProperties);
    }

    @Bean
    @ConditionalOnMissingBean(NoOpCryptoStrategy.class)
    public NoOpCryptoStrategy noOpCryptoStrategy(ObjectProvider<ObjectMapper> objectMapperProvider, CryptoProperties cryptoProperties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new NoOpCryptoStrategy(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(DefaultCryptoStrategy.class)
    public DefaultCryptoStrategy defaultCryptoStrategy(ObjectProvider<ObjectMapper> objectMapperProvider, CryptoProperties cryptoProperties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new DefaultCryptoStrategy(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(FlksecCryptoStrategy.class)
    public FlksecCryptoStrategy flksecCryptoStrategy(ObjectProvider<ObjectMapper> objectMapperProvider,
                                                     ObjectProvider<OkHttp3Template> okHttp3TemplateProvider, CryptoProperties cryptoProperties) {
        OkHttp3Template okHttp3Template = okHttp3TemplateProvider.getIfAvailable(() -> {
            ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
            return new OkHttp3Template(new OkHttpClient(), objectMapper);
        });
        return new FlksecCryptoStrategy(okHttp3Template, cryptoProperties.getFlksecAddress(), cryptoProperties.getFlksecPort());
    }

}
