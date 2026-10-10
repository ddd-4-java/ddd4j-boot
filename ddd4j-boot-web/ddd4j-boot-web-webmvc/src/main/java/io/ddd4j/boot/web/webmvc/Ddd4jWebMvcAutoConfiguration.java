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
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({DispatcherServlet.class, Ddd4jWebMvcInterceptor.class})
@EnableConfigurationProperties(Ddd4jWebMvcProperties.class)
@Import({DefaultMessageSourceConfiguration.class, DefaultSequenceConfiguration.class,
        BaseWebConfig.class})
public class Ddd4jWebMvcAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本配置类。
     */
    public Ddd4jWebMvcAutoConfiguration() {
    }

    /**
     * 注册 ddd4j 核心属性绑定 Bean。
     *
     * @return 核心属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    public BaseCoreProperties baseCoreProperties() {
        return new BaseCoreProperties();
    }

    /**
     * 注册本地静态资源属性绑定 Bean。
     *
     * @return 静态资源属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    public LocalResourceProperteis localResourceProperteis() {
        return new LocalResourceProperteis();
    }

    /**
     * 注册分布式序列属性绑定 Bean。
     *
     * @return 序列属性实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SequenceProperties sequenceProperties() {
        return new SequenceProperties();
    }

    /**
     * Boot 已提供 RequestContextFilter、LocaleResolver 与 MVC Validator；此处只装配 ddd4j 的
     * 请求生命周期、鉴权和幂等基础设施，避免覆盖 Spring Boot 的同名 Bean。
     *
     * @param environment Spring 环境，用于读取激活的 Profile 列表
     * @return Profile 管理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ProfileManager profileManager(Environment environment) {
        return new ProfileManager(environment::getActiveProfiles);
    }

    /**
     * 注册语言切换拦截器（参数名 {@code Constants.LANG_PARAM_NAME}）。
     *
     * @return Locale 切换拦截器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(Constants.LANG_PARAM_NAME);
        return interceptor;
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
     * 注册 MVC 全局异常处理器。
     *
     * @param exceptionTranslator Web 异常翻译器
     * @return MVC 异常处理器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public Ddd4jWebMvcExceptionHandler ddd4jWebMvcExceptionHandler(WebExceptionTranslator exceptionTranslator) {
        return new Ddd4jWebMvcExceptionHandler(exceptionTranslator);
    }

    /**
     * 注册访问策略：从 {@code ddd4j.web.public-paths} 读取公开路径（缺省含健康检查与静态资源），默认要求鉴权。
     *
     * @param environment Spring 环境，提供公开路径配置
     * @return 基于路径的访问策略实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebAccessPolicy webAccessPolicy(Environment environment) {
        String[] publicPaths = environment.getProperty("ddd4j.web.public-paths", String[].class,
                new String[]{"/health", "/health/readiness", "/health/liveness", "/assets/**", "/webjars/**"});
        return new PathWebAccessPolicy(List.of(publicPaths), AuthenticationMode.REQUIRED);
    }

    /**
     * 注册请求上下文工厂：按 {@code ddd4j.web.trust-forwarded-headers} 决定是否信任转发头获取客户端 IP。
     *
     * @param environment Spring 环境，提供转发头信任开关
     * @return 请求上下文工厂实例
     */
    @Bean
    @ConditionalOnMissingBean
    public WebRequestContextFactory webRequestContextFactory(Environment environment) {
        boolean trustForwardedHeaders = environment.getProperty("ddd4j.web.trust-forwarded-headers", Boolean.class,
                false);
        ClientIpResolver clientIpResolver = trustForwardedHeaders
                ? ClientIpResolver.trustedProxy() : ClientIpResolver.remoteAddressOnly();
        return new WebRequestContextFactory(RequestIdGenerator.uuid(), clientIpResolver);
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
     * 注册幂等生命周期组件，并确保默认幂等缓存已注册（见 {@link #registerDefaultIdempotencyCache()}）。
     *
     * @return Web 幂等生命周期实例
     */
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

    /**
     * 注册 MVC 拦截器，串联请求上下文、鉴权与幂等防护。
     *
     * @param contextFactory 请求上下文工厂
     * @param requestLifecycle 请求生命周期组件
     * @param idempotencyLifecycle 幂等生命周期组件
     * @return ddd4j MVC 拦截器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public Ddd4jWebMvcInterceptor ddd4jWebMvcInterceptor(WebRequestContextFactory contextFactory,
                                                         WebRequestLifecycle requestLifecycle,
                                                         WebIdempotencyLifecycle idempotencyLifecycle) {
        return new Ddd4jWebMvcInterceptor(contextFactory, requestLifecycle, idempotencyLifecycle);
    }

    /**
     * 注册默认 MVC 配置器（静态资源、语言切换、拦截器注册）。
     *
     * @param localResourceProperteis 静态资源属性
     * @param localeChangeInterceptor 语言切换拦截器
     * @param ddd4jWebMvcInterceptor ddd4j MVC 拦截器
     * @return 默认 MVC 配置器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultWebMvcConfigurer defaultWebMvcConfigurer(LocalResourceProperteis localResourceProperteis,
                                                           LocaleChangeInterceptor localeChangeInterceptor,
                                                           Ddd4jWebMvcInterceptor ddd4jWebMvcInterceptor) {
        return new DefaultWebMvcConfigurer(localResourceProperteis, localeChangeInterceptor, ddd4jWebMvcInterceptor);
    }

    /**
     * 注册 Web 运行时健康指示器（runtime=spring-webmvc）。
     *
     * @return 健康指示器实例
     */
    @Bean("ddd4jWebHealthIndicator")
    @ConditionalOnClass(HealthIndicator.class)
    @ConditionalOnMissingBean(name = "ddd4jWebHealthIndicator")
    public HealthIndicator ddd4jWebHealthIndicator() {
        return () -> Health.up().withDetail("runtime", "spring-webmvc").build();
    }
}
