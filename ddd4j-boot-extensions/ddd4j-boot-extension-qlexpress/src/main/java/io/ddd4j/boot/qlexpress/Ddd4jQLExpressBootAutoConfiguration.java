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
import org.springframework.context.annotation.Configuration;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(QLExpress.class)
@ConditionalOnProperty(prefix = QLExpressProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(QLExpressProperties.class)
public class Ddd4jQLExpressBootAutoConfiguration {

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

    @Bean
    @ConditionalOnMissingBean(RuleRepository.class)
    @ConditionalOnProperty(prefix = QLExpressProperties.PREFIX + ".rules", name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public RuleRepository ruleRepository() {
        return new InMemoryRuleRepository();
    }

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
