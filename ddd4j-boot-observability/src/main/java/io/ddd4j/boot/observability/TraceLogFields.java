package io.ddd4j.boot.observability;

/**
 * 追踪与因果链对外词汇冻结常量（对应家族 design D9 字段命名冻结表）。
 *
 * <p>对应 spec：boot-trace-observability / Causality correlation、Trace context propagation。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class TraceLogFields {

    /**
     * 日志/告警结构化字段：trace 标识（D9 冻结命名，camelCase）。
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 日志/告警结构化字段：关联标识（D9 冻结命名，camelCase）。
     */
    public static final String CORRELATION_ID = "correlationId";

    /**
     * 日志/告警结构化字段：因果标识（D9 冻结命名，camelCase）。
     */
    public static final String CAUSATION_ID = "causationId";

    /**
     * HTTP 追踪头（入站/出站，W3C Trace Context 标准头，不自造）。
     */
    public static final String HEADER_TRACEPARENT = "traceparent";

    /**
     * HTTP 诊断回传头（响应，值 = 当前 trace 标识）。
     */
    public static final String HEADER_X_TRACE_ID = "X-Trace-Id";

    /**
     * 因果关联入站头（可选，与底座 correlationId 因果链对齐）。
     */
    public static final String HEADER_X_CORRELATION_ID = "X-Correlation-Id";

    /**
     * 因果因果入站头（可选，与底座 causationId 因果链对齐）。
     */
    public static final String HEADER_X_CAUSATION_ID = "X-Causation-Id";

    /**
     * 字段无值时的占位呈现（保留字段存在性）。
     */
    public static final String MISSING_VALUE = "-";

    private TraceLogFields() {
    }
}
