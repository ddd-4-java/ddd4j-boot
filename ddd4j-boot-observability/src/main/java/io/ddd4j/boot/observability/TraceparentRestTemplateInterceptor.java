package io.ddd4j.boot.observability;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * RestTemplate 出站追踪拦截器：为出站请求注入 W3C {@code traceparent} 头。
 *
 * <p>对应家族 design D3（出站透传复用既有拦截器链，W3C traceparent 标准头）；
 * 对应 spec：boot-trace-observability / Trace context propagation（出站调用透传场景）。
 * 通过既有扩展点（{@code RestTemplate#setInterceptors}）挂载。
 *
 * <p>JDK 8 线（Spring Boot 2.3–2.7）最小集：无 OTel 当前 span 可读，
 * trace 标识取自 {@link TraceContextBridge}（MDC + TTL 双写桥），
 * 桥上无值时生成全新 traceparent，保证出站头恒为合法 W3C traceparent。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class TraceparentRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    /**
     * 追踪上下文桥接（读取当前 trace 标识）。
     */
    private final TraceContextBridge bridge;

    /**
     * 以默认 MDC + TTL 双写桥构建。
     */
    public TraceparentRestTemplateInterceptor() {
        this(new MdcTtlTraceContextBridge());
    }

    /**
     * 以指定追踪上下文桥构建。
     *
     * @param bridge 追踪上下文桥接（不可为 null）
     */
    public TraceparentRestTemplateInterceptor(final TraceContextBridge bridge) {
        this.bridge = Objects.requireNonNull(bridge, "bridge must not be null");
    }

    /**
     * 注入 traceparent 头后放行。
     *
     * @param request   出站请求
     * @param body      请求体
     * @param execution 执行链
     * @return 响应
     * @throws IOException IO 异常透传
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {
        String traceId = bridge.currentTraceId();
        String traceparent = StringUtils.hasText(traceId)
                ? TraceparentCodec.generateTraceparent(traceId)
                : TraceparentCodec.generateTraceparent();
        request.getHeaders().set(TraceLogFields.HEADER_TRACEPARENT, traceparent);
        return execution.execute(request, body);
    }
}
