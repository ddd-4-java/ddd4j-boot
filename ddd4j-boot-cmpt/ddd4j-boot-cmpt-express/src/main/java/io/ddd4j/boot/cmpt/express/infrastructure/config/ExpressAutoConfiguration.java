package io.ddd4j.boot.cmpt.express.infrastructure.config;

import com.alibaba.qlexpress4.Express4Runner;
import io.ddd4j.boot.cmpt.express.application.service.RuleCacheService;
import io.ddd4j.boot.cmpt.express.application.service.RuleEngineApplicationService;
import io.ddd4j.boot.cmpt.express.domain.repository.RuleDefinitionRepository;
import io.ddd4j.boot.cmpt.express.domain.service.RuleEngineDomainService;
import io.ddd4j.boot.cmpt.express.infrastructure.cache.RedisRuleCacheService;
import io.ddd4j.boot.cmpt.express.infrastructure.service.RuleEngineDomainServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Express组件自动配置
 * 确保所有服务类被Spring管理
 */
@Configuration
@ConditionalOnClass(name = "com.alibaba.qlexpress4.Express4Runner")
public class ExpressAutoConfiguration {

    /**
     * 规则引擎领域服务实现
     */
    @Bean
    @ConditionalOnMissingBean
    public RuleEngineDomainService ruleEngineDomainService(Express4Runner expressRunner) {
        return new RuleEngineDomainServiceImpl(expressRunner);
    }

    /**
     * 规则缓存服务实现
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    public RuleCacheService ruleCacheService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisRuleCacheService(redisTemplate);
    }

    /**
     * 规则引擎应用服务
     */
    @Bean
    @ConditionalOnMissingBean
    public RuleEngineApplicationService ruleEngineApplicationService(
            RuleEngineDomainService ruleEngineDomainService,
            RuleDefinitionRepository ruleRepository,
            RuleCacheService ruleCacheService) {
        return new RuleEngineApplicationService(
                ruleEngineDomainService,
                ruleRepository,
                ruleCacheService);
    }
}

