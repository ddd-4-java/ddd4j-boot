package io.ddd4j.boot.observability;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * RestTemplate 出站追踪拦截器：为出站请求注入 W3C {@code traceparent} 头。
 *
 * <p>对应家族 design D3（出站透传复用既有拦截器链，W3C traceparent 标准头）；
 * 对应 spec：boot-trace-observability / Trace context propagation（出站调用透传场景）。
 * 通过既有扩展点（{@code RestTemplate#setInterceptors} 或自定义 Customizer）挂载。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class TraceparentRestTemplateInterceptor implements ClientHttpRequestInterceptor {

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
        request.getHeaders().set(TraceLogFields.HEADER_TRACEPARENT,
                TraceparentCodec.currentOrFreshTraceparent());
        return execution.execute(request, body);
    }
}
