package io.ddd4j.boot.monitor;

import io.ddd4j.core.context.ThreadContext;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

/**
 * 告警追踪上下文渲染器：为钉钉/企微/飞书告警输出补齐 trace 关联字段。
 *
 * <p>字段名冻结于家族 design D9（{@code traceId}/{@code correlationId}，camelCase）；
 * 值从 SLF4J MDC 读取（可观测性模块双写），TTL ThreadContext 兜底，
 * 缺失时以 {@code -} 占位保留字段存在性。
 *
 * <p>对应 spec：boot-trace-observability / Alert correlation（告警可回溯场景）；
 * 对应家族 design D4（extension-monitor 输出补 traceId + correlationId）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class AlertTraceContext {

    /**
     * 字段无值占位符。
     */
    private static final String MISSING_VALUE = "-";

    /**
     * 显式无参构造器，供 Spring 以自动装配方式实例化本渲染器。
     */
    public AlertTraceContext() {
    }

    /**
     * 渲染告警关联字段块（追加在告警正文之后）。
     *
     * @return 形如 {@code \ntraceId: xxx\ncorrelationId: yyy} 的文本块
     */
    public String render() {
        return "\n" + TraceLogFieldsAlias.TRACE_ID + ": " + currentTraceId()
                + "\n" + TraceLogFieldsAlias.CORRELATION_ID + ": " + currentCorrelationId();
    }

    /**
     * 装饰告警正文：原文之后追加关联字段块。
     *
     * @param body 告警正文（允许 null）
     * @return 携带 traceId/correlationId 的完整告警文本
     */
    public String decorate(final String body) {
        String origin = StringUtils.hasText(body) ? body : "";
        return origin + render();
    }

    /**
     * 读取当前 trace 标识。
     *
     * @return traceId；缺失返回 {@code -}
     */
    public String currentTraceId() {
        return current(TraceLogFieldsAlias.TRACE_ID);
    }

    /**
     * 读取当前关联标识。
     *
     * @return correlationId；缺失返回 {@code -}
     */
    public String currentCorrelationId() {
        return current(TraceLogFieldsAlias.CORRELATION_ID);
    }

    /**
     * 读取字段：MDC 优先，TTL ThreadContext 兜底。
     *
     * @param field 字段名（D9 冻结命名）
     * @return 字段值；缺失返回 {@code -}
     */
    private String current(final String field) {
        String fromMdc = MDC.get(field);
        if (StringUtils.hasText(fromMdc)) {
            return fromMdc;
        }
        String fromTtl = ThreadContext.get(field);
        return StringUtils.hasText(fromTtl) ? fromTtl : MISSING_VALUE;
    }

    /**
     * D9 字段名别名（避免本模块强依赖可观测性模块的常量类，保持两模块单向解耦）。
     */
    private static final class TraceLogFieldsAlias {

        /**
         * D9 冻结字段：traceId。
         */
        private static final String TRACE_ID = "traceId";

        /**
         * D9 冻结字段：correlationId。
         */
        private static final String CORRELATION_ID = "correlationId";

        private TraceLogFieldsAlias() {
        }
    }
}
