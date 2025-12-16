package io.ddd4j.boot.cmpt.express.infrastructure.service;

import com.alibaba.qlexpress4.CheckOptions;
import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import io.ddd4j.boot.cmpt.express.domain.model.entity.RuleDefinition;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleExecutionResult;
import io.ddd4j.boot.cmpt.express.domain.model.vo.RuleValidationResult;
import io.ddd4j.boot.cmpt.express.domain.service.RuleEngineDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 规则引擎领域服务实现
 * 基础设施层：使用QLExpress实现规则执行
 * 
 * 注意：此类通过ExpressAutoConfiguration自动配置，无需手动添加@Service注解
 */
public class RuleEngineDomainServiceImpl implements RuleEngineDomainService {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineDomainServiceImpl.class);

    private final Express4Runner expressRunner;

    public RuleEngineDomainServiceImpl(Express4Runner expressRunner) {
        this.expressRunner = expressRunner;
    }

    @Override
    public RuleExecutionResult executeRule(RuleDefinition rule, Map<String, Object> context) {
        try {
            // 创建执行上下文
            ExpressContext expressContext = new MapExpressContext(context);

            // 执行表达式
            Object result = expressRunner.execute(rule.getRuleExpression(), expressContext, null);

            return RuleExecutionResult.builder()
                    .success(true)
                    .result(result)
                    .ruleCode(rule.getRuleCode())
                    .executedAt(java.time.LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("执行规则失败: ruleCode={}", rule.getRuleCode(), e);
            return RuleExecutionResult.builder()
                    .success(false)
                    .errorCode("EXECUTION_ERROR")
                    .errorMessage("规则执行异常: " + e.getMessage())
                    .ruleCode(rule.getRuleCode())
                    .executedAt(java.time.LocalDateTime.now())
                    .build();
        }
    }

    @Override
    public RuleValidationResult validateExpression(String expression) {
        try {
            expressRunner.check(expression, CheckOptions.DEFAULT_OPTIONS);
            return RuleValidationResult.builder()
                    .valid(true)
                    .message("规则语法正确")
                    .build();
        } catch (Exception e) {
            return RuleValidationResult.builder()
                    .valid(false)
                    .message("规则语法错误: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean isRuleAvailable(RuleDefinition rule) {
        return rule != null && rule.isAvailable();
    }
}

