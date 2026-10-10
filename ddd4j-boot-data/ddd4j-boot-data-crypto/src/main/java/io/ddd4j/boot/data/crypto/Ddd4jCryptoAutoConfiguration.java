package io.ddd4j.boot.data.crypto;

import tools.jackson.databind.ObjectMapper;
import io.ddd4j.data.crypto.CryptoProperties;
import io.ddd4j.data.crypto.provider.DefaultCryptoProvider;
import io.ddd4j.data.crypto.strategy.CryptoStrategy;
import io.ddd4j.data.crypto.strategy.DefaultCryptoStrategy;
import io.ddd4j.data.crypto.strategy.NoOpCryptoStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.stream.Collectors;

/**
 * ddd4j 加解密 Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-crypto} 迁入，承担加解密策略 Bean 的装配职责。
 * 通用层（ddd4j-data-crypto）仅保留纯 Java 加解密实现。
 *
 * <p>注意：{@code FlksecCryptoStrategy} 需要 {@code java.net.http.HttpClient}，
 * 业务方可按需自行创建 Bean。
 *
 * <p>{@link CryptoProperties} 是零 Spring 依赖纯 POJO（不标注 {@code @ConfigurationProperties}），
 * 因此不能用 {@code @EnableConfigurationProperties}（启动期抛 "No ConfigurationProperties annotation found"），
 * 这里用 {@link Binder} 手动绑定 {@code ddd4j.crypto.*}。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = CryptoProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class Ddd4jCryptoAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public Ddd4jCryptoAutoConfiguration() {
    }

    /**
     * 手动绑定 {@code ddd4j.crypto.*} 到上游纯 POJO {@link CryptoProperties}。
     *
     * @param environment Spring 环境，提供配置来源
     * @return 绑定完成的加解密属性实例
     */
    @Bean
    @ConditionalOnMissingBean(CryptoProperties.class)
    public CryptoProperties cryptoProperties(Environment environment) {
        CryptoProperties properties = new CryptoProperties();
        Binder.get(environment).bind(CryptoProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    /**
     * 注册默认加解密提供者，聚合容器中全部 {@link CryptoStrategy} 实现。
     *
     * @param cryptoStrategyProvider 全部加解密策略
     * @param cryptoProperties 加解密属性
     * @return 默认加解密提供者实例
     */
    @Bean
    public DefaultCryptoProvider cryptoProvider(ObjectProvider<CryptoStrategy> cryptoStrategyProvider, CryptoProperties cryptoProperties) {
        return new DefaultCryptoProvider(cryptoStrategyProvider.stream().collect(Collectors.toList()), cryptoProperties);
    }

    /**
     * 注册空操作加解密策略（明文透传，作为兜底实现）。
     *
     * @param objectMapperProvider 可选的 Jackson 序列化器
     * @param cryptoProperties 加解密属性
     * @return 空操作加解密策略实例
     */
    @Bean
    public NoOpCryptoStrategy noOpCryptoStrategy(ObjectProvider<ObjectMapper> objectMapperProvider, CryptoProperties cryptoProperties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new NoOpCryptoStrategy(objectMapper);
    }

    /**
     * 注册默认对称加解密策略。
     *
     * @param objectMapperProvider 可选的 Jackson 序列化器
     * @param cryptoProperties 加解密属性
     * @return 默认加解密策略实例
     */
    @Bean
    public DefaultCryptoStrategy defaultCryptoStrategy(ObjectProvider<ObjectMapper> objectMapperProvider, CryptoProperties cryptoProperties) {
        ObjectMapper objectMapper = objectMapperProvider.getIfAvailable(ObjectMapper::new);
        return new DefaultCryptoStrategy(objectMapper);
    }

}
