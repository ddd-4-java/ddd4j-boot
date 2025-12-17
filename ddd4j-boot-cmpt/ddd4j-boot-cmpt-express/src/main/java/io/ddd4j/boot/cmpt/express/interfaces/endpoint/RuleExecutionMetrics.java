package io.ddd4j.boot.cmpt.express.interfaces.endpoint;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RuleExecutionMetrics {

    @Autowired
    private MeterRegistry meterRegistry;

    private final Counter ruleExecutions;
    private final Counter ruleFailures;
    private final Timer ruleExecutionTimer;

    public RuleExecutionMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.ruleExecutions = Counter.builder("rule.engine.executions")
                .description("规则执行次数")
                .register(meterRegistry);

        this.ruleFailures = Counter.builder("rule.engine.failures")
                .description("规则执行失败次数")
                .register(meterRegistry);

        this.ruleExecutionTimer = Timer.builder("rule.engine.execution.time")
                .description("规则执行耗时")
                .register(meterRegistry);
    }

    /**
     * 记录规则执行
     */
    public void recordRuleExecution(String ruleCode, boolean success, long executionTime) {
        ruleExecutions.increment(Tag.of("rule_code", ruleCode), Tag.of("success", String.valueOf(success)));

        if (!success) {
            ruleFailures.increment(Tag.of("rule_code", ruleCode));
        }

        ruleExecutionTimer.record(executionTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取规则执行统计
     */
    public RuleExecutionStats getExecutionStats(String ruleCode) {
        // 通过Micrometer获取统计数据
        returnnew RuleExecutionStats();
    }
}