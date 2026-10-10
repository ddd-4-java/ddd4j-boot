package io.ddd4j.boot.observability;

import io.ddd4j.core.context.ThreadContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * {@link TraceContextBridge} 默认实现：SLF4J MDC + 底座 TTL {@link ThreadContext} 双写。
 *
 * <p>三键（{@code traceId}/{@code correlationId}/{@code causationId}，D9 冻结命名）并列写入、
 * 互相可查，不另建第二套标识体系（对应家族 design D2）；
 * TTL 侧由底座 TransmittableThreadLocal 天然透传线程池。
 *
 * <p>对应 spec：boot-trace-observability / Causality correlation、Trace context propagation。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class MdcTtlTraceContextBridge implements TraceContextBridge {

    /**
     * 显式无参构造器，供 Spring 实例化桥接 Bean。
     */
    public MdcTtlTraceContextBridge() {
    }

    /**
     * 将三键同时写入 MDC 与 TTL ThreadContext（空值跳过，不污染上下文）。
     *
     * @param traceId       当前 trace 标识
     * @param correlationId 关联标识（可为 null）
     * @param causationId   因果标识（可为 null）
     */
    @Override
    public void attach(final String traceId, final String correlationId, final String causationId) {
        putBoth(TraceLogFields.TRACE_ID, traceId);
        putBoth(TraceLogFields.CORRELATION_ID, correlationId);
        putBoth(TraceLogFields.CAUSATION_ID, causationId);
    }

    /**
     * 清理双写上下文。
     */
    @Override
    public void detach() {
        for (String field : new String[] {
                TraceLogFields.TRACE_ID, TraceLogFields.CORRELATION_ID, TraceLogFields.CAUSATION_ID}) {
            MDC.remove(field);
            ThreadContext.remove(field);
        }
    }

    /**
     * 读取当前 trace 标识。
     *
     * @return trace 标识；缺失返回 null
     */
    @Override
    public String currentTraceId() {
        return current(TraceLogFields.TRACE_ID);
    }

    /**
     * 读取当前关联标识。
     *
     * @return correlationId；缺失返回 null
     */
    @Override
    public String currentCorrelationId() {
        return current(TraceLogFields.CORRELATION_ID);
    }

    /**
     * 读取当前因果标识。
     *
     * @return causationId；缺失返回 null
     */
    @Override
    public String currentCausationId() {
        return current(TraceLogFields.CAUSATION_ID);
    }

    /**
     * 读取字段：MDC 优先，TTL ThreadContext 兜底，二者互为镜像可互查。
     *
     * @param field 字段名（D9 冻结命名）
     * @return 字段值；缺失返回 null
     */
    private String current(final String field) {
        String fromMdc = MDC.get(field);
        return StringUtils.hasText(fromMdc) ? fromMdc : ThreadContext.get(field);
    }

    /**
     * 双写单键：MDC 与 TTL ThreadContext 同时写入，空值时清理残留。
     *
     * @param field 字段名
     * @param value 值（可为 null/空串）
     */
    private void putBoth(final String field, final String value) {
        if (StringUtils.hasText(value)) {
            MDC.put(field, value);
            ThreadContext.set(field, value);
        } else {
            MDC.remove(field);
            ThreadContext.remove(field);
        }
    }
}
