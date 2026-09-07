package io.ddd4j.boot.web.webmvc;

import io.ddd4j.cache.CacheKit;
import io.ddd4j.cache.local.CaffeineCache;
import io.ddd4j.core.BaseCoreProperties;
import io.ddd4j.core.ProfileManager;
import io.ddd4j.core.cache.CacheConfig;
import io.ddd4j.core.constant.Constants;
import io.ddd4j.web.core.auth.AuthenticationMode;
import io.ddd4j.web.core.auth.BearerSubjectAuthenticator;
import io.ddd4j.web.core.idempotency.CacheIdempotencyGuard;
import io.ddd4j.web.core.context.ClientIpResolver;
import io.ddd4j.web.core.error.DefaultWebExceptionTranslator;
import io.ddd4j.web.core.auth.PathWebAccessPolicy;
import io.ddd4j.web.core.context.RequestIdGenerator;
import io.ddd4j.web.core.auth.WebAccessPolicy;
import io.ddd4j.web.core.error.WebExceptionTranslator;
import io.ddd4j.web.core.idempotency.WebIdempotencyLifecycle;
import io.ddd4j.web.core.context.WebRequestContextFactory;
import io.ddd4j.web.core.context.WebRequestLifecycle;
import io.ddd4j.web.webmvc.Ddd4jWebMvcExceptionHandler;
import io.ddd4j.web.webmvc.Ddd4jWebMvcInterceptor;
import io.ddd4j.web.webmvc.config.BaseWebConfig;
import io.ddd4j.web.webmvc.config.LocalResourceProperteis;
import io.ddd4j.web.webmvc.config.SequenceProperties;
import io.ddd4j.web.webmvc.DefaultMessageSourceConfiguration;
import io.ddd4j.web.webmvc.DefaultSequenceConfiguration;
import io.ddd4j.web.webmvc.DefaultWebMvcConfigurer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.List;

/**
 * ddd4j WebMVC 的 Spring Boot 条件装配入口。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class, Ddd4jWebMvcInterceptor.class})
@EnableConfigurationProperties(Ddd4jWebMvcProperties.class)
@Import({DefaultMessageSourceConfiguration.class, DefaultSequenceConfiguration.class,
        BaseWebConfig.class})
public class Ddd4jWebMvcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BaseCoreProperties baseCoreProperties() {
        return new BaseCoreProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public LocalResourceProperteis localResourceProperteis() {
        return new LocalResourceProperteis();
    }

    @Bean
    @ConditionalOnMissingBean
    public SequenceProperties sequenceProperties() {
        return new SequenceProperties();
    }

    /**
     * Boot 已提供 RequestContextFilter、LocaleResolver 与 MVC Validator；此处只装配 ddd4j 的
     * 请求生命周期、鉴权和幂等基础设施，避免覆盖 Spring Boot 的同名 Bean。
     */
    @Bean
    @ConditionalOnMissingBean
    public ProfileManager profileManager(Environment environment) {
        return new ProfileManager(environment::getActiveProfiles);
    }

    @Bean
    @ConditionalOnMissingBean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(Constants.LANG_PARAM_NAME);
        return interceptor;
    }

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
    public Ddd4jWebMvcExceptionHandler ddd4jWebMvcExceptionHandler(WebExceptionTranslator exceptionTranslator) {
        return new Ddd4jWebMvcExceptionHandler(exceptionTranslator);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebAccessPolicy webAccessPolicy(Environment environment) {
        String[] publicPaths = environment.getProperty("ddd4j.web.public-paths", String[].class,
                new String[]{"/health", "/health/readiness", "/health/liveness", "/assets/**", "/webjars/**"});
        return new PathWebAccessPolicy(List.of(publicPaths), AuthenticationMode.REQUIRED);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebRequestContextFactory webRequestContextFactory(Environment environment) {
        boolean trustForwardedHeaders = environment.getProperty("ddd4j.web.trust-forwarded-headers", Boolean.class,
                false);
        ClientIpResolver clientIpResolver = trustForwardedHeaders
                ? ClientIpResolver.trustedProxy() : ClientIpResolver.remoteAddressOnly();
        return new WebRequestContextFactory(RequestIdGenerator.uuid(), clientIpResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebRequestLifecycle webRequestLifecycle(BearerSubjectAuthenticator authenticator,
                                                   WebAccessPolicy accessPolicy) {
        return new WebRequestLifecycle(authenticator, accessPolicy);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebIdempotencyLifecycle webIdempotencyLifecycle() {
        registerDefaultIdempotencyCache();
        return new WebIdempotencyLifecycle(new CacheIdempotencyGuard());
    }

    /**
     * 为 Web 幂等防护注册默认 CAS 缓存。
     *
     * <p>{@link CacheIdempotencyGuard} 无参构造使用 {@code ddd4j-web-idempotency} 缓存名，
     * 若该缓存未注册到 {@link CacheKit}，任何带幂等 Key 的请求都会因
     * {@code IllegalStateException} 被翻译为 409。此处按需注册本地 Caffeine 实现，
     * 业务方可自行注册同名缓存覆盖。
     */
    private static void registerDefaultIdempotencyCache() {
        String cacheName = CacheIdempotencyGuard.DEFAULT_CACHE_NAME;
        if (CacheKit.getCache(cacheName) == null) {
            CacheKit.register(cacheName, CaffeineCache.create(CacheConfig.builder(cacheName).build()));
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public Ddd4jWebMvcInterceptor ddd4jWebMvcInterceptor(WebRequestContextFactory contextFactory,
                                                         WebRequestLifecycle requestLifecycle,
                                                         WebIdempotencyLifecycle idempotencyLifecycle) {
        return new Ddd4jWebMvcInterceptor(contextFactory, requestLifecycle, idempotencyLifecycle);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultWebMvcConfigurer defaultWebMvcConfigurer(LocalResourceProperteis localResourceProperteis,
                                                           LocaleChangeInterceptor localeChangeInterceptor,
                                                           Ddd4jWebMvcInterceptor ddd4jWebMvcInterceptor) {
        return new DefaultWebMvcConfigurer(localResourceProperteis, localeChangeInterceptor, ddd4jWebMvcInterceptor);
    }

    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "spring-webmvc").build();
    }
}
