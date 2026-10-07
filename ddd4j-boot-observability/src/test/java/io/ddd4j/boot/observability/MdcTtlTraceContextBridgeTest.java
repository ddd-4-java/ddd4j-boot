package io.ddd4j.boot.observability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MdcTtlTraceContextBridge} 单元测试：MDC + TTL ThreadContext 双写、因果链并列互查。
 *
 * <p>对应 spec：boot-trace-observability / Causality correlation、Trace context propagation。
 */
class MdcTtlTraceContextBridgeTest {

    private final MdcTtlTraceContextBridge bridge = new MdcTtlTraceContextBridge();

    @AfterEach
    void cleanup() {
        bridge.detach();
        MDC.clear();
        io.ddd4j.core.context.ThreadContext.clear();
    }

    @Test
    @DisplayName("attach 后 MDC 与 TTL ThreadContext 均含 traceId（双写）")
    void attachShouldWriteBothMdcAndTtl() {
        bridge.attach("aaaabbbbccccddddeeeeffff00001111", "corr-1", "cause-1");
        String ttlTraceId = io.ddd4j.core.context.ThreadContext.get(TraceLogFields.TRACE_ID);
        assertThat(MDC.get(TraceLogFields.TRACE_ID)).isEqualTo("aaaabbbbccccddddeeeeffff00001111");
        assertThat(ttlTraceId).isEqualTo("aaaabbbbccccddddeeeeffff00001111");
    }

    @Test
    @DisplayName("因果链并列呈现：traceId/correlationId/causationId 三键可互查")
    void causalityFieldsShouldCoexistAndBeCrossQueryable() {
        bridge.attach("aaaabbbbccccddddeeeeffff00001111", "corr-42", "cause-7");
        String ttlCorrelationId = io.ddd4j.core.context.ThreadContext.get(TraceLogFields.CORRELATION_ID);
        // 给定 traceId 所在线程上下文可定位 correlationId/causationId，反之亦然
        assertThat(bridge.currentTraceId()).isEqualTo("aaaabbbbccccddddeeeeffff00001111");
        assertThat(bridge.currentCorrelationId()).isEqualTo("corr-42");
        assertThat(bridge.currentCausationId()).isEqualTo("cause-7");
        assertThat(MDC.get(TraceLogFields.CORRELATION_ID)).isEqualTo("corr-42");
        assertThat(MDC.get(TraceLogFields.CAUSATION_ID)).isEqualTo("cause-7");
        assertThat(ttlCorrelationId).isEqualTo("corr-42");
    }

    @Test
    @DisplayName("detach 后双写上下文清理，不向线程池泄漏")
    void detachShouldClearBothBackends() {
        bridge.attach("aaaabbbbccccddddeeeeffff00001111", "corr-1", null);
        bridge.detach();
        String ttlTraceId = io.ddd4j.core.context.ThreadContext.get(TraceLogFields.TRACE_ID);
        assertThat(MDC.get(TraceLogFields.TRACE_ID)).isNull();
        assertThat(ttlTraceId).isNull();
        assertThat(bridge.currentTraceId()).isNull();
    }

    @Test
    @DisplayName("缺失字段以 null 呈现，attach 不接受空串污染")
    void missingFieldsShouldBeNullSafe() {
        bridge.attach("aaaabbbbccccddddeeeeffff00001111", null, "");
        assertThat(bridge.currentCorrelationId()).isNull();
        assertThat(bridge.currentCausationId()).isNull();
    }
}
