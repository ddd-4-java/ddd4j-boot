package io.ddd4j.boot.express;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import io.ddd4j.extension.express.application.service.RuleCacheService;
import io.ddd4j.extension.express.application.service.RuleEngineApplicationService;
import io.ddd4j.extension.express.application.service.RuleManagementService;
import io.ddd4j.extension.express.domain.repository.RuleDefinitionRepository;
import io.ddd4j.extension.express.domain.service.RuleEngineDomainService;
import io.ddd4j.extension.express.infrastructure.cache.JetCacheRuleCacheService;
import io.ddd4j.extension.express.infrastructure.function.ContainsFunction;
import io.ddd4j.extension.express.infrastructure.function.EndsWithFunction;
import io.ddd4j.extension.express.infrastructure.function.FormatDateFunction;
import io.ddd4j.extension.express.infrastructure.function.StartsWithFunction;
import io.ddd4j.extension.express.infrastructure.service.RuleEngineDomainServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Objects;

/**
 * ddd4j qlexpress 规则引擎的 Spring Boot 整合入口（融合单类）。
 *
 * <p>仅负责 Boot 层的自动装配：当 classpath 上存在
 * {@code com.alibaba.qlexpress4.Express4Runner} 时，自动注册以下 Bean：
 * <ul>
 *   <li>{@link Express4Runner} expressRunner —— QLExpress 运行器（含函数注册）</li>
 *   <li>{@link RuleEngineDomainService} ruleEngineDomainService —— 规则引擎领域服务</li>
 *   <li>{@link RuleCacheService} ruleCacheService —— 规则缓存服务（JetCache 多级缓存）</li>
 *   <li>{@link RuleEngineApplicationService} ruleEngineApplicationService —— 规则引擎应用服务</li>
 *   <li>{@link RuleManagementService} ruleManagementService —— 规则管理服务</li>
 * </ul>
 *
 * <p>领域/应用/基础设施实现来自
 * {@code io.ddd4j:ddd4j-extension-qlexpress}，本类只承担 Spring 适配职责。
 * 早期版本拆分为 {@code QLExpressConfig} + {@code ExpressAutoConfiguration} +
 * {@code Ddd4jExpressBootAutoConfiguration} 三类，本版本按"一个对象一个职责"合并为
 * 单个自动配置类，减少模块碎片化。
 *
 * @author ddd4j-boot
 * @since 1.0
 */
@AutoConfiguration
@ConditionalOnClass(name = "com.alibaba.qlexpress4.Express4Runner")
@Slf4j
public class Ddd4jExpressBootAutoConfiguration {

    // ----------------------------------------------------------------------
    // QLExpress 引擎初始化
    // ----------------------------------------------------------------------

    /**
     * 创建并配置 QLExpress 运行器。
     *
     * <p>初始化 Express4Runner，并注册所有自定义函数：
     * <ol>
     *   <li>硬编码函数：contains / startsWith / endsWith / formatDate</li>
     *   <li>注解函数：自动发现 {@code @QLAlias} 注解的 {@link CustomFunction} Bean</li>
     * </ol>
     *
     * @param functionProvider 自定义函数提供者，用于自动发现通过注解声明的函数
     * @return 配置好的 Express4Runner 实例
     */
    @Bean
    public Express4Runner expressRunner(ObjectProvider<CustomFunction> functionProvider) {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            registerHardcodedFunctions(runner);
            registerAnnotatedFunctions(runner, functionProvider);
            log.info("QLExpress函数注册完成");
        } catch (Exception e) {
            log.error("注册QLExpress函数失败", e);
            throw new RuntimeException("注册QLExpress函数失败", e);
        }
        return runner;
    }

    private void registerHardcodedFunctions(Express4Runner runner) throws Exception {
        runner.addFunction("contains", new ContainsFunction());
        runner.addFunction("startsWith", new StartsWithFunction());
        runner.addFunction("endsWith", new EndsWithFunction());
        runner.addFunction("formatDate", new FormatDateFunction());
    }

    private void registerAnnotatedFunctions(Express4Runner runner, ObjectProvider<CustomFunction> functionProvider) {
        functionProvider.forEach(function -> {
            QLAlias qlAlias = AnnotationUtils.findAnnotation(function.getClass(), QLAlias.class);
            if (Objects.nonNull(qlAlias) && qlAlias.value().length >= 2) {
                try {
                    runner.addAlias(qlAlias.value()[0], qlAlias.value()[1]);
                    log.debug("注册注解函数: {} -> {}", qlAlias.value()[0], qlAlias.value()[1]);
                } catch (Exception e) {
                    log.warn("注册注解函数失败: {}", function.getClass().getName(), e);
                }
            }
        });
    }

    // ----------------------------------------------------------------------
    // 规则引擎服务装配
    // ----------------------------------------------------------------------

    /**
     * 规则引擎领域服务实现。
     */
    @Bean
    public RuleEngineDomainService ruleEngineDomainService(Express4Runner expressRunner) {
        return new RuleEngineDomainServiceImpl(expressRunner);
    }

    /**
     * 规则缓存服务（JetCache 多级缓存）。
     */
    @Bean
    public RuleCacheService ruleCacheService(JetCacheRuleCacheService jetCacheRuleCacheService) {
        return jetCacheRuleCacheService;
    }

    /**
     * 规则引擎应用服务。
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
     * 规则管理服务。
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
