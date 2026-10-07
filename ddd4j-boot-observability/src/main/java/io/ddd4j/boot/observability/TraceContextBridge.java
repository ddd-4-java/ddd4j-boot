package io.ddd4j.boot.observability;

/**
 * 追踪上下文桥接契约：trace 标识与底座因果链（correlationId/causationId）并列双写。
 *
 * <p>对应家族 design D2（traceId 桥接进既有 TTL ThreadContext，与因果链并列）；
 * 对应 spec：boot-trace-observability / Causality correlation。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public interface TraceContextBridge {

    /**
     * 将 trace 标识与因果链字段同时写入 MDC（日志渲染）与 TTL ThreadContext（线程池透传）。
     *
     * @param traceId       当前 trace 标识
     * @param correlationId 关联标识（可为 null，表示缺失）
     * @param causationId   因果标识（可为 null，表示缺失）
     */
    void attach(String traceId, String correlationId, String causationId);

    /**
     * 清理当前线程的双写上下文，防止线程池复用泄漏。
     */
    void detach();

    /**
     * 读取当前 trace 标识。
     *
     * @return trace 标识；缺失返回 null
     */
    String currentTraceId();

    /**
     * 读取当前关联标识。
     *
     * @return correlationId；缺失返回 null
     */
    String currentCorrelationId();

    /**
     * 读取当前因果标识。
     *
     * @return causationId；缺失返回 null
     */
    String currentCausationId();
}
