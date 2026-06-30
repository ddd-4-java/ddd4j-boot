package io.ddd4j.boot.express.infrastructure.config;

import com.alibaba.qlexpress4.Express4Runner;
import io.ddd4j.extension.express.application.service.RuleCacheService;
import io.ddd4j.extension.express.application.service.RuleEngineApplicationService;
import io.ddd4j.extension.express.application.service.RuleManagementService;
import io.ddd4j.extension.express.domain.repository.RuleDefinitionRepository;
import io.ddd4j.extension.express.domain.service.RuleEngineDomainService;
import io.ddd4j.extension.express.infrastructure.cache.JetCacheRuleCacheService;
import io.ddd4j.extension.express.infrastructure.service.RuleEngineDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Express Spring 自动配置。
 *
 * <p>从 ddd4j-extension-qlexpress 迁入 ddd4j-boot-extension-qlexpress：
 * 该类强依赖 Spring（@Configuration / @Bean），属于 Spring 框架适配层职责。
 *
 * <p>自动配置规则引擎相关的所有服务 Bean：
 * <ul>
 *   <li>RuleEngineDomainService - 规则引擎领域服务</li>
 *   <li>RuleCacheService - 规则缓存服务（优先使用JetCache多级缓存）</li>
 *   <li>RuleEngineApplicationService - 规则引擎应用服务</li>
 *   <li>RuleManagementService - 规则管理服务</li>
 * </ul>
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 * @author ddd4j-boot
 * @since 1.0
 */
@Configuration
public class ExpressAutoConfiguration {

    /**
     * 规则引擎领域服务实现
     */
    @Bean
    public RuleEngineDomainService ruleEngineDomainService(Express4Runner expressRunner) {
        return new RuleEngineDomainServiceImpl(expressRunner);
    }

    /**
     * 规则缓存服务实现（JetCache多级缓存）
     */
    @Bean
    public RuleCacheService ruleCacheService(JetCacheRuleCacheService jetCacheRuleCacheService) {
        return jetCacheRuleCacheService;
    }

    /**
     * 规则引擎应用服务
     */
    @Bean
    public RuleEngineApplicationService ruleEngineApplicationService(
            RuleEngineDomainService ruleEngineDomainService,
            RuleDefinitionRepository ruleRepository,
            RuleCacheService ruleCacheService) {
        return new RuleEngineApplicationService(
                ruleEngineDomainService,
                ruleRepository,
                ruleCacheService);
    }

    /**
     * 规则管理服务
     */
    @Bean
    public RuleManagementService ruleManagementService(
            RuleDefinitionRepository ruleRepository,
            RuleCacheService ruleCacheService,
            RuleEngineDomainService ruleEngineDomainService) {
        return new RuleManagementService(
                ruleRepository,
                ruleCacheService,
                ruleEngineDomainService);
    }

}
