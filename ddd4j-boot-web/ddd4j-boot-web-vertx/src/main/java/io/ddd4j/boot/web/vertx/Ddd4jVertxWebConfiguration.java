package io.ddd4j.boot.web.vertx;

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
import io.ddd4j.web.vertx.Ddd4jVertxWeb;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ddd4j Vert.x Web 的 Spring Boot 条件装配入口。
 *
 * <p>类路径存在 Vert.x 与上游 {@link Ddd4jVertxWeb} 适配器时自动装配
 * 请求上下文、鉴权、异常翻译与幂等防护的默认 Bean，业务方可通过注册同类型
 * Bean 全量覆盖。
 */
@AutoConfiguration
@ConditionalOnClass({Vertx.class, Ddd4jVertxWeb.class})
@ConditionalOnProperty(prefix = "ddd4j.web.vertx", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Ddd4jVertxWebProperties.class)
public class Ddd4jVertxWebConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BearerSubjectAuthenticator bearerSubjectAuthenticator() {
        return new BearerSubjectAuthenticator();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebExceptionTranslator webExceptionTranslator() {
        return new DefaultWebExceptionTranslator();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebRequestContextFactory webRequestContextFactory(Ddd4jVertxWebProperties properties) {
        ClientIpResolver clientIpResolver = properties.isTrustForwardedHeaders()
                ? ClientIpResolver.trustedProxy() : ClientIpResolver.remoteAddressOnly();
        return new WebRequestContextFactory(RequestIdGenerator.uuid(), clientIpResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebAccessPolicy webAccessPolicy(Ddd4jVertxWebProperties properties) {
        return new PathWebAccessPolicy(properties.getPublicPaths(), properties.getDefaultAuthenticationMode());
    }

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
     */
    @Bean
    @ConditionalOnMissingBean
    public WebIdempotencyLifecycle webIdempotencyLifecycle(Ddd4jVertxWebProperties properties) {
        registerDefaultIdempotencyCache(properties.getIdempotencyCacheName());
        return new WebIdempotencyLifecycle(new CacheIdempotencyGuard(properties.getIdempotencyCacheName()));
    }

    @Bean
    @ConditionalOnMissingBean
    public Ddd4jVertxWeb ddd4jVertxWeb(WebRequestContextFactory contextFactory,
                                       WebRequestLifecycle requestLifecycle,
                                       WebExceptionTranslator exceptionTranslator,
                                       ObjectProvider<WebIdempotencyLifecycle> idempotencyLifecycle) {
        return new Ddd4jVertxWeb(contextFactory, requestLifecycle, exceptionTranslator,
                idempotencyLifecycle.getIfAvailable(), Json::encode);
    }

    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "vertx").build();
    }

    private static void registerDefaultIdempotencyCache(String cacheName) {
        if (CacheKit.getCache(cacheName) == null) {
            CacheKit.register(cacheName, CaffeineCache.create(CacheConfig.builder(cacheName).build()));
        }
    }
}
