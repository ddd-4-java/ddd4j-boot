package io.ddd4j.boot.web.javalin;

import io.ddd4j.cache.CacheKit;
import io.ddd4j.cache.local.CaffeineCache;
import io.ddd4j.core.cache.CacheConfig;
import io.ddd4j.web.core.auth.BearerSubjectAuthenticator;
import io.ddd4j.web.core.auth.PathWebAccessPolicy;
import io.ddd4j.web.core.auth.WebAccessPolicy;
import io.ddd4j.web.core.context.ClientIpResolver;
import io.ddd4j.web.core.context.RequestIdGenerator;
import io.ddd4j.web.core.context.WebRequestContextFactory;
import io.ddd4j.web.core.context.WebRequestLifecycle;
import io.ddd4j.web.core.error.DefaultWebExceptionTranslator;
import io.ddd4j.web.core.error.WebExceptionTranslator;
import io.ddd4j.web.core.idempotency.CacheIdempotencyGuard;
import io.ddd4j.web.core.idempotency.WebIdempotencyLifecycle;
import io.ddd4j.web.javalin.Ddd4jJavalinWeb;
import io.javalin.Javalin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ddd4j Javalin Web 的 Spring Boot 条件装配入口。
 *
 * <p>类路径存在 Javalin 与上游 {@link Ddd4jJavalinWeb} 适配器时自动装配
 * 请求上下文、鉴权、异常翻译与幂等防护的默认 Bean，业务方可通过注册同类型
 * Bean 全量覆盖。
 */
@AutoConfiguration
@ConditionalOnClass({Javalin.class, Ddd4jJavalinWeb.class})
@ConditionalOnProperty(prefix = "ddd4j.web.javalin", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Ddd4jJavalinWebProperties.class)
public class Ddd4jJavalinWebConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public Ddd4jJavalinWebConfiguration() {
    }

    /**
     * 注册 Bearer Token 主体认证器。
     *
     * @return Bearer 主体认证器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BearerSubjectAuthenticator bearerSubjectAuthenticator() {
        return new BearerSubjectAuthenticator();
    }

    /**
     * 注册默认异常翻译器。
     *
     * @return Web 异常翻译器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebExceptionTranslator webExceptionTranslator() {
        return new DefaultWebExceptionTranslator();
    }

    /**
     * 注册请求上下文工厂，按配置决定是否信任转发头获取客户端 IP。
     *
     * @param properties Javalin Web 配置属性
     * @return 请求上下文工厂实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebRequestContextFactory webRequestContextFactory(Ddd4jJavalinWebProperties properties) {
        ClientIpResolver clientIpResolver = properties.isTrustForwardedHeaders()
                ? ClientIpResolver.trustedProxy() : ClientIpResolver.remoteAddressOnly();
        return new WebRequestContextFactory(RequestIdGenerator.uuid(), clientIpResolver);
    }

    /**
     * 注册访问策略：公开路径与鉴权模式来自 {@code ddd4j.web.javalin.*} 配置。
     *
     * @param properties Javalin Web 配置属性
     * @return 基于路径的访问策略实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebAccessPolicy webAccessPolicy(Ddd4jJavalinWebProperties properties) {
        return new PathWebAccessPolicy(properties.getPublicPaths(), properties.getDefaultAuthenticationMode());
    }

    /**
     * 注册请求生命周期组件（鉴权 + 访问策略）。
     *
     * @param authenticator Bearer 主体认证器
     * @param accessPolicy 访问策略
     * @return 请求生命周期实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebRequestLifecycle webRequestLifecycle(BearerSubjectAuthenticator authenticator,
                                                   WebAccessPolicy accessPolicy) {
        return new WebRequestLifecycle(authenticator, accessPolicy);
    }

    /**
     * 为 Web 幂等防护注册默认 CAS 缓存。
     *
     * <p>{@link CacheIdempotencyGuard} 依赖 {@link CacheKit} 中已注册的同名缓存，
     * 若该缓存未注册，任何带幂等 Key 的请求都会因 {@code IllegalStateException}
     * 被翻译为 409。此处按需注册本地 Caffeine 实现，业务方可自行注册同名缓存覆盖。
     *
     * @param properties Javalin Web 配置属性，提供幂等缓存名称
     * @return Web 幂等生命周期实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebIdempotencyLifecycle webIdempotencyLifecycle(Ddd4jJavalinWebProperties properties) {
        registerDefaultIdempotencyCache(properties.getIdempotencyCacheName());
        return new WebIdempotencyLifecycle(new CacheIdempotencyGuard(properties.getIdempotencyCacheName()));
    }

    /**
     * 注册 Javalin Web 适配器，将 ddd4j 请求基础设施接入 Javalin。
     *
     * @param contextFactory 请求上下文工厂
     * @param requestLifecycle 请求生命周期组件
     * @param exceptionTranslator 异常翻译器
     * @param idempotencyLifecycle 幂等生命周期（可选）
     * @return ddd4j Javalin Web 适配器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public Ddd4jJavalinWeb ddd4jJavalinWeb(WebRequestContextFactory contextFactory,
                                           WebRequestLifecycle requestLifecycle,
                                           WebExceptionTranslator exceptionTranslator,
                                           ObjectProvider<WebIdempotencyLifecycle> idempotencyLifecycle) {
        return new Ddd4jJavalinWeb(contextFactory, requestLifecycle, exceptionTranslator,
                idempotencyLifecycle.getIfAvailable());
    }

    /**
     * 注册 Web 运行时健康指示器（runtime=javalin）。
     *
     * @return 健康指示器实例
     */
    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "javalin").build();
    }

    /**
     * 按缓存名注册本地 Caffeine 幂等缓存（幂等操作，已存在则跳过）。
     *
     * @param cacheName 幂等缓存名称
     */
    private static void registerDefaultIdempotencyCache(String cacheName) {
        if (CacheKit.getCache(cacheName) == null) {
            CacheKit.register(cacheName, CaffeineCache.create(CacheConfig.builder(cacheName).build()));
        }
    }
}
