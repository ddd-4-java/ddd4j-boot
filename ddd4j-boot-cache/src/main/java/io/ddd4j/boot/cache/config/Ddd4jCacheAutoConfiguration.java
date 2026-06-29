package io.ddd4j.boot.cache.config;

import io.ddd4j.cache.CacheKit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ddd4j 缓存 Spring Boot 自动配置。
 *
 * <p>{@link CacheKit} 是静态工具类（private 构造器），无需注册为 Bean。
 * 本配置类仅提供 {@link CacheProperties} 属性绑定，
 * 业务项目通过 {@code CacheKit.get(biz, key)} / {@code CacheKit.register(biz, cache)} 静态方法使用。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(CacheKit.class)
@EnableConfigurationProperties(CacheProperties.class)
public class Ddd4jCacheAutoConfiguration {

}
