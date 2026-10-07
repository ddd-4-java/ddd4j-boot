package io.ddd4j.boot.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * 入站追踪过滤器：提取合法 W3C {@code traceparent} 继承 trace 标识，否则新建；
 * 响应经 {@code X-Trace-Id} 回传诊断标识，并把 traceId/correlationId/causationId
 * 双写 MDC + TTL ThreadContext（对应家族 design D2/D4）。
 *
 * <p>对应 spec：boot-trace-observability / Trace context propagation（入站继承/新建场景）、
 * Causality correlation。仅在 {@code ddd4j.observability.tracing.enabled=true} 时装配，
 * 默认关闭时不注册（Graceful degradation）。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class TraceContextFilter implements Filter {

    /**
     * OTel API Tracer（创建 SERVER span）。
     */
    private final Tracer tracer;

    /**
     * 追踪上下文桥接（MDC + TTL ThreadContext 双写）。
     */
    private final TraceContextBridge bridge;

    /**
     * 构建入站追踪过滤器。
     *
     * @param tracer OTel API Tracer
     * @param bridge 追踪上下文桥接
     */
    public TraceContextFilter(final Tracer tracer, final TraceContextBridge bridge) {
        this.tracer = Objects.requireNonNull(tracer, "tracer must not be null");
        this.bridge = Objects.requireNonNull(bridge, "bridge must not be null");
    }

    /**
     * 过滤：继承/新建 trace 标识 → 双写上下文 → 回传 X-Trace-Id → 放行链路 → 清理。
     *
     * @param request  请求
     * @param response 响应
     * @param chain    过滤链
     * @throws IOException      IO 异常透传
     * @throws ServletException Servlet 异常透传
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpRequest)
                || !(response instanceof HttpServletResponse httpResponse)) {
            chain.doFilter(request, response);
            return;
        }
        String inbound = httpRequest.getHeader(TraceLogFields.HEADER_TRACEPARENT);
        SpanBuilder builder = tracer.spanBuilder(spanName(httpRequest)).setSpanKind(SpanKind.SERVER);
        SpanContext remoteParent = TraceparentCodec.toRemoteSpanContext(inbound);
        if (Objects.nonNull(remoteParent)) {
            // 合法追踪头：继承 trace 标识继续传播（SpanContext 需经 Span.wrap 进入上下文）
            builder.setParent(Context.root().with(Span.wrap(remoteParent)));
        } else {
            // 无追踪头或非法：新建根 span（生成新 trace 标识）
            builder.setNoParent();
        }
        Span span = builder.startSpan();
        try (Scope scope = span.makeCurrent()) {
            String traceId = span.getSpanContext().getTraceId();
            bridge.attach(traceId,
                    httpRequest.getHeader(TraceLogFields.HEADER_X_CORRELATION_ID),
                    httpRequest.getHeader(TraceLogFields.HEADER_X_CAUSATION_ID));
            httpResponse.setHeader(TraceLogFields.HEADER_X_TRACE_ID, traceId);
            chain.doFilter(request, response);
        } finally {
            bridge.detach();
            span.end();
        }
    }

    /**
     * 生成本地 span 名（method + URI）。
     *
     * @param request 请求
     * @return span 名
     */
    private String spanName(final HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if (!StringUtils.hasText(uri)) {
            return StringUtils.hasText(method) ? method : "HTTP";
        }
        return StringUtils.hasText(method) ? method + " " + uri : uri;
    }
}
