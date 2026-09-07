package io.ddd4j.boot.qlexpress.rule;

import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleCache;
import io.ddd4j.boot.qlexpress.rule.support.InMemoryRuleRepository;
import io.ddd4j.extension.qlexpress.QLExpress;
import io.ddd4j.extension.qlexpress.model.QLExpressExecutionResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        Map<String, Object> firstContext = new HashMap<>();
        firstContext.put("price", 20);
        firstContext.put("quantity", 3);
        QLExpressExecutionResult<Object> first = service.execute("pricing.total", firstContext);
        assertThat(first.success()).isTrue();
        assertThat(first.value()).isEqualTo(60);

        service.update(created.getId(), RuleDefinition.builder()
                .name("订单总价（含服务费）")
                .expression("price * quantity + fee")
                .type(RuleType.CALCULATION)
                .enabled(true)
                .priority(20)
                .build());
        Map<String, Object> updatedContext = new HashMap<>();
        updatedContext.put("price", 20);
        updatedContext.put("quantity", 3);
        updatedContext.put("fee", 5);
        assertThat(service.execute("pricing.total", updatedContext).value()).isEqualTo(65);

        service.disable(created.getId());
        assertThat(service.execute("pricing.total", Collections.emptyMap()).errorCode()).isEqualTo("RULE_DISABLED");

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

    // ── update guard branches ─────────────────────────────────

    @Test
    void update_blankId_shouldReject() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        assertThatThrownBy(() -> service.update("  ", RuleDefinition.builder()
                        .name("x").expression("1").type(RuleType.CALCULATION).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id 不能为空");
    }

    @Test
    void update_nonexistentId_shouldRejectAndPublishNoEvent() {
        List<Object> events = new ArrayList<>();
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                events::add);

        assertThatThrownBy(() -> service.update("no-such-id", RuleDefinition.builder()
                        .name("x").expression("1").type(RuleType.CALCULATION).build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("规则不存在")
                .hasMessageContaining("no-such-id");

        assertThat(events).isEmpty();
    }

    @Test
    void update_nullChanges_shouldThrowNPE() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("npe.rule")
                .name("NPE 测试")
                .expression("1 + 1")
                .type(RuleType.CALCULATION)
                .build());

        assertThatThrownBy(() -> service.update(created.getId(), null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("changes 不能为空");
    }

    @Test
    void update_attemptToChangeCode_shouldReject() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("immutable.code")
                .name("不可变编码")
                .expression("1")
                .type(RuleType.CALCULATION)
                .build());

        assertThatThrownBy(() -> service.update(created.getId(), RuleDefinition.builder()
                        .code("new.code")
                        .name("x")
                        .expression("2")
                        .type(RuleType.CALCULATION)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("规则编码不允许修改")
                .hasMessageContaining("immutable.code");
    }

    @Test
    void update_invalidExpression_shouldReject() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("expr.rule")
                .name("表达式校验")
                .expression("1 + 2")
                .type(RuleType.CALCULATION)
                .build());

        assertThatThrownBy(() -> service.update(created.getId(), RuleDefinition.builder()
                        .name("x")
                        .expression("if (")
                        .type(RuleType.CALCULATION)
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("规则表达式无效");
    }

    @Test
    void update_partialUpdate_shouldFallbackExistingValuesAndBumpTimestamp() {
        RuleService service = new RuleService(
                new InMemoryRuleRepository(),
                new InMemoryRuleCache(),
                QLExpress.create(),
                event -> { });

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("partial.update")
                .name("部分更新")
                .expression("a + b")
                .type(RuleType.CALCULATION)
                .enabled(true)
                .priority(42)
                .build());

        // Update with null enabled and null priority -> fall back to existing values
        // Also use blank code -> backfilled from existing
        RuleDefinition updated = service.update(created.getId(), RuleDefinition.builder()
                .code("")
                .name("部分更新 v2")
                .expression("a + b + c")
                .type(RuleType.VALIDATION)
                .enabled(null)
                .priority(null)
                .build());

        assertThat(updated.getName()).isEqualTo("部分更新 v2");
        assertThat(updated.getExpression()).isEqualTo("a + b + c");
        assertThat(updated.getType()).isEqualTo(RuleType.VALIDATION);
        // null enabled -> falls back to existing (true)
        assertThat(updated.getEnabled()).isTrue();
        // null priority -> falls back to existing (42)
        assertThat(updated.getPriority()).isEqualTo(42);
        // blank code -> backfilled from existing
        assertThat(updated.getCode()).isEqualTo("partial.update");
        // updatedAt should be bumped (after createdAt)
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    void update_eventsAndCache_shouldEmitUpdatedEventAndCacheProperly() {
        List<Object> events = new ArrayList<>();
        InMemoryRuleRepository repository = new InMemoryRuleRepository();
        InMemoryRuleCache cache = new InMemoryRuleCache();
        RuleService service = new RuleService(
                repository,
                cache,
                QLExpress.create(),
                events::add);

        RuleDefinition created = service.create(RuleDefinition.builder()
                .code("cache.rule")
                .name("缓存测试")
                .expression("x * 2")
                .type(RuleType.CALCULATION)
                .build());

        // Clear CREATED event
        events.clear();

        // Verify findByCode returns old expression
        assertThat(service.findByCode("cache.rule")).isPresent();
        assertThat(service.findByCode("cache.rule").get().getExpression()).isEqualTo("x * 2");

        // Update expression
        service.update(created.getId(), RuleDefinition.builder()
                .name("缓存测试 v2")
                .expression("x * 3")
                .type(RuleType.CALCULATION)
                .build());

        // Exactly one event: UPDATED
        assertThat(events).hasSize(1);
        RuleChangedEvent event = (RuleChangedEvent) events.get(0);
        assertThat(event.operation()).isEqualTo(RuleChangedEvent.Operation.UPDATED);
        assertThat(event.ruleId()).isEqualTo(created.getId());
        assertThat(event.ruleCode()).isEqualTo("cache.rule");

        // findByCode returns new expression (from cache)
        assertThat(service.findByCode("cache.rule")).isPresent();
        assertThat(service.findByCode("cache.rule").get().getExpression()).isEqualTo("x * 3");

        // After clearCache, findByCode still returns new expression (from repository)
        service.clearCache();
        assertThat(service.findByCode("cache.rule")).isPresent();
        assertThat(service.findByCode("cache.rule").get().getExpression()).isEqualTo("x * 3");
    }
}
