package io.ddd4j.boot.qlexpress.rule;

import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleCache;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleRepository;
import io.ddd4j.extension.qlexpress.QLExpress;
import io.ddd4j.extension.qlexpress.model.QLExpressExecutionResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RuleServiceTest {

    @Test
    void crudExecuteAndEventsShouldFormAClosedFlow() {
        List<Object> events = new ArrayList<>();
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                events::add);

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("pricing.total")
                .name("订单总价")
                .expression("price * quantity")
                .type(RuleType.CALCULATION)
                .enabled(true)
                .priority(10)
                .build());

        QLExpressExecutionResult<Object> first = service.execute(
                "pricing.total", Map.of("price", 20, "quantity", 3));
        assertThat(first.success()).isTrue();
        assertThat(first.value()).isEqualTo(60);

        service.update(created.getId(), RuleDefinition.builder()
                .name("订单总价（含服务费）")
                .expression("price * quantity + fee")
                .type(RuleType.CALCULATION)
                .enabled(true)
                .priority(20)
                .build());
        assertThat(service.execute("pricing.total",
                Map.of("price", 20, "quantity", 3, "fee", 5)).value()).isEqualTo(65);

        service.disable(created.getId());
        assertThat(service.execute("pricing.total", Map.of()).errorCode()).isEqualTo("RULE_DISABLED");

        service.enable(created.getId());
        service.delete(created.getId());
        assertThat(service.findByCode("pricing.total")).isEmpty();

        assertThat(events).hasSize(5);
        assertThat(events).allMatch(RuleChangedEvent.class::isInstance);
    }

    @Test
    void duplicateCodeAndInvalidExpressionShouldBeRejected() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        RuleDefinition valid = RuleDefinition.builder()
                .code("validation.age")
                .name("年龄校验")
                .expression("age >= 18")
                .type(RuleType.VALIDATION)
                .build();
        service.create(valid);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.create(RuleDefinition.builder()
                        .code("validation.age")
                        .name("重复规则")
                        .expression("true")
                        .type(RuleType.VALIDATION)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("已存在");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.create(RuleDefinition.builder()
                        .code("invalid")
                        .name("错误表达式")
                        .expression("if (")
                        .type(RuleType.VALIDATION)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("表达式无效");
    }
}
