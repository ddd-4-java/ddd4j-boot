package io.ddd4j.boot.qlexpress;

import io.ddd4j.boot.qlexpress.rule.RuleCache;
import io.ddd4j.boot.qlexpress.rule.RuleRepository;
import io.ddd4j.boot.qlexpress.rule.RuleService;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleCache;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleRepository;
import io.ddd4j.boot.qlexpress.rule.support.SpringCacheRuleCache;
import io.ddd4j.extension.qlexpress.QLExpress;
import io.ddd4j.extension.qlexpress.QLExpressEngine;
import io.ddd4j.extension.qlexpress.QLExpressEngineBuilder;
import io.ddd4j.extension.qlexpress.function.NamedQLFunction;
import io.ddd4j.extension.qlexpress.model.QLExpressExecutionOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

/**
 * QLExpress 工具引擎与可选规则管理能力的 Spring Boot 自动配置。
 */
@AutoConfiguration
@ConditionalOnClass(QLExpress.class)
@ConditionalOnProperty(prefix = QLExpressProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(QLExpressProperties.class)
public class Ddd4jQLExpressBootAutoConfiguration {

    /**
     * 无参构造器，供 Spring 反射实例化自动配置类使用。
     */
    public Ddd4jQLExpressBootAutoConfiguration() {
    }

    /**
     * 装配 QLExpress 引擎：聚合执行选项与容器中全部 {@link NamedQLFunction} 扩展函数。
     *
     * @param properties      QLExpress 配置属性
     * @param functionProvider 容器中的命名函数提供器，按顺序全部注册到引擎
     * @return QLExpress 引擎实例
     */
    @Bean
    @ConditionalOnMissingBean(QLExpressEngine.class)
    public QLExpressEngine qlExpressEngine(QLExpressProperties properties,
                                           ObjectProvider<NamedQLFunction> functionProvider) {
        QLExpressExecutionOptions executionOptions = QLExpressExecutionOptions.builder()
                .timeoutMillis(properties.getTimeoutMillis())
                .cache(properties.isCache())
                .precise(properties.isPrecise())
                .avoidNullPointer(properties.isAvoidNullPointer())
                .maxArrayLength(properties.getMaxArrayLength())
                .traceExpression(properties.isTraceExpression())
                .build();

        QLExpressEngineBuilder builder = QLExpress.builder()
                .builtInFunctions(properties.isBuiltInFunctions())
                .allowPrivateAccess(properties.isAllowPrivateAccess())
                .traceExpression(properties.isTraceExpression())
                .defaultExecutionOptions(executionOptions);
        functionProvider.orderedStream().forEach(builder::function);
        return builder.build();
    }

    /**
     * 装配默认内存版规则仓库，业务侧可自定义 {@link RuleRepository} Bean 覆盖。
     *
     * @return 内存规则仓库
     */
    @Bean
    @ConditionalOnMissingBean(RuleRepository.class)
    @ConditionalOnProperty(prefix = QLExpressProperties.PREFIX + ".rules", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RuleRepository ruleRepository() {
        return new InMemoryRuleRepository();
    }

    /**
     * 装配规则缓存：容器存在可用 {@link CacheManager} 且目标缓存存在时用 Spring Cache 实现，
     * 否则回退到内存实现。
     *
     * @param properties         QLExpress 配置属性，提供规则缓存名称
     * @param cacheManagerProvider 缓存管理器提供器，允许无缓存环境降级
     * @return 规则缓存实例
     */
    @Bean
    @ConditionalOnMissingBean(RuleCache.class)
    @ConditionalOnProperty(prefix = QLExpressProperties.PREFIX + ".rules", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RuleCache ruleCache(QLExpressProperties properties,
                               ObjectProvider<CacheManager> cacheManagerProvider) {
        CacheManager cacheManager = cacheManagerProvider.getIfAvailable();
        if (Objects.nonNull(cacheManager)) {
            Cache cache = cacheManager.getCache(properties.getRules().getCacheName());
            if (Objects.nonNull(cache)) {
                return new SpringCacheRuleCache(cache);
            }
        }
        return new InMemoryRuleCache();
    }

    /**
     * 装配规则服务，聚合仓库、缓存、引擎与事件发布器。
     *
     * @param repository     规则持久化仓库
     * @param cache          规则缓存
     * @param engine         QLExpress 引擎
     * @param eventPublisher 规则变更事件发布器
     * @return 规则服务实例
     */
    @Bean
    @ConditionalOnMissingBean(RuleService.class)
    @ConditionalOnProperty(prefix = QLExpressProperties.PREFIX + ".rules", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RuleService ruleService(RuleRepository repository,
                                   RuleCache cache,
                                   QLExpressEngine engine,
                                   ApplicationEventPublisher eventPublisher) {
        return new RuleService(repository, cache, engine, eventPublisher);
    }
}
