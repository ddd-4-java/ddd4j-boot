package io.ddd4j.boot.monitor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 告警关联字段 golden 验收（命名即规格，对应 spec：boot-trace-observability / Alert correlation）。
 *
 * <p>验收规格：告警渲染输出 MUST 同时含 {@code traceId} 与 {@code correlationId} 字段
 * （D9 冻结命名），无值时以 {@code -} 占位保留字段存在性。
 */
@DisplayName("告警关联字段 golden 验收（*Must*Test：测试名即验收规格）")
class AlertTraceRendererMustTest {

    private final AlertTraceContext renderer = new AlertTraceContext();

    @AfterEach
    void cleanup() {
        MDC.clear();
        io.ddd4j.core.context.ThreadContext.clear();
    }

    @Test
    @DisplayName("mustRenderTraceIdAndCorrelationIdInAlertBody：告警渲染体同时携带 traceId 与 correlationId")
    void mustRenderTraceIdAndCorrelationIdInAlertBody() {
        MDC.put("traceId", "4bf92f3577b34da6a3ce929d0e0e4736");
        MDC.put("correlationId", "corr-1001");
        String alert = renderer.decorate("订单支付失败：超时");
        assertThat(alert).contains("traceId: 4bf92f3577b34da6a3ce929d0e0e4736");
        assertThat(alert).contains("correlationId: corr-1001");
        assertThat(alert).startsWith("订单支付失败：超时");
    }

    @Test
    @DisplayName("mustFallbackToTtlThreadContext：MDC 缺失时从 TTL ThreadContext 兜底取值")
    void mustFallbackToTtlThreadContext() {
        io.ddd4j.core.context.ThreadContext.set("traceId", "aaaabbbbccccddddeeeeffff00001111");
        io.ddd4j.core.context.ThreadContext.set("correlationId", "corr-2002");
        String alert = renderer.render();
        assertThat(alert).contains("traceId: aaaabbbbccccddddeeeeffff00001111");
        assertThat(alert).contains("correlationId: corr-2002");
    }

    @Test
    @DisplayName("mustKeepFieldPresenceWhenContextMissing：上下文缺失时字段仍存在并以 - 占位")
    void mustKeepFieldPresenceWhenContextMissing() {
        String alert = renderer.decorate(null);
        assertThat(alert).contains("traceId: -");
        assertThat(alert).contains("correlationId: -");
    }
}
