package io.ddd4j.boot.qlexpress;

import io.ddd4j.boot.qlexpress.rule.RuleCache;
import io.ddd4j.boot.qlexpress.rule.RuleRepository;
import io.ddd4j.boot.qlexpress.rule.RuleService;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleCache;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleRepository;
import io.ddd4j.extension.qlexpress.QLExpressEngine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class Ddd4jQLExpressBootAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Ddd4jQLExpressBootAutoConfiguration.class));

    @Test
    void autoConfigurationShouldCreateEngineAndDefaultRuleComponents() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(QLExpressEngine.class);
            assertThat(context).hasSingleBean(RuleService.class);
            assertThat(context.getBean(RuleRepository.class)).isInstanceOf(InMemoryRuleRepository.class);
            assertThat(context.getBean(RuleCache.class)).isInstanceOf(InMemoryRuleCache.class);
        });
    }

    @Test
    void rulesCanBeDisabledWithoutDisablingExpressionEngine() {
        contextRunner.withPropertyValues("ddd4j.qlexpress.rules.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(QLExpressEngine.class);
                    assertThat(context).doesNotHaveBean(RuleService.class);
                    assertThat(context).doesNotHaveBean(RuleRepository.class);
                    assertThat(context).doesNotHaveBean(RuleCache.class);
                });
    }

    @Test
    void wholeExtensionCanBeDisabled() {
        contextRunner.withPropertyValues("ddd4j.qlexpress.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(QLExpressEngine.class));
    }
}
