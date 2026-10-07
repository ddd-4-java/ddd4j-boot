package io.ddd4j.boot.observability;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

/**
 * WebClient 出站追踪过滤器：为出站请求注入 W3C {@code traceparent} 头。
 *
 * <p>对应家族 design D3；对应 spec：boot-trace-observability /
 * Trace context propagation（出站调用透传场景）。
 * 通过 {@code WebClient.Builder#filter} 挂载。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class TraceparentWebClientFilter {

    private TraceparentWebClientFilter() {
    }

    /**
     * 构建注入 traceparent 的交换过滤器。
     *
     * @return ExchangeFilterFunction 实例
     */
    public static ExchangeFilterFunction traceparent() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            ClientRequest mutated = ClientRequest.from(request)
                    .header(TraceLogFields.HEADER_TRACEPARENT, TraceparentCodec.currentOrFreshTraceparent())
                    .build();
            return Mono.just(mutated);
        });
    }
}
