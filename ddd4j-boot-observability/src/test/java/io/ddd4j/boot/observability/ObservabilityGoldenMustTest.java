package io.ddd4j.boot.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 链路追踪能力 golden 验收测试（命名即规格，对应 spec：boot-trace-observability 全部 Requirement）。
 *
 * <p>覆盖六大行为域：traceparent 继承/新建、异步线程池透传、敏感材料脱敏、
 * 后端不可用降级、默认关闭零影响、采样率生效。
 */
@DisplayName("链路追踪 golden 验收（*Must*Test：测试名即验收规格）")
class ObservabilityGoldenMustTest {

    private static final String INBOUND_TRACEPARENT =
            "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01";
    private static final String INBOUND_TRACE_ID = "4bf92f3577b34da6a3ce929d0e0e4736";
    private static final Pattern TRACE_ID_PATTERN = Pattern.compile("^[0-9a-f]{32}$");


    /**
     * spring-boot-test 3.x runner 兼容 Web 上下文。
     *
     * <p>Boot 3.x AbstractApplicationContextRunner 在 refresh 前经 getBeanFactory() 探测容器，
     * withBean 预注册更要求 GenericApplicationContext 家族；而 AnnotationConfigWebApplicationContext
     * 属 AbstractRefreshable 家族（refresh 时才建容器），refresh 前调用 getBeanFactory() 必抛
     * IllegalStateException。此处组合 GenericWebApplicationContext（急切容器）与
     * AnnotatedBeanDefinitionReader（@Configuration 处理基座），以 AnnotationConfigRegistry
     * 门面呈现：容器时序与注解处理行为同时满足，等价 AnnotationConfigWebApplicationContext 语义。
     */
    static final class RunnerCompatWebApplicationContext extends GenericWebApplicationContext
            implements AnnotationConfigRegistry {

        /**
         * 注解定义读取器（构造期注册 ConfigurationClassPostProcessor 等注解处理基座）。
         */
        private final AnnotatedBeanDefinitionReader reader;

        RunnerCompatWebApplicationContext() {
            this.reader = new AnnotatedBeanDefinitionReader(this);
        }

        @Override
        public void register(Class<?>... componentClasses) {
            this.reader.register(componentClasses);
        }

        @Override
        public void scan(String... basePackages) {
            // runner 无用例经 scan 装配；按需以 ClassPathBeanDefinitionScanner 执行包扫描
            new ClassPathBeanDefinitionScanner(this).scan(basePackages);
        }
    }

    /** Servlet Web 上下文 runner：验证入站过滤器等仅 Web 环境装配的 Bean。 */
    private final WebApplicationContextRunner enabledRunner = new WebApplicationContextRunner(
            RunnerCompatWebApplicationContext::new)
            .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
            .withPropertyValues("ddd4j.observability.tracing.enabled=true");

    // ------------------------------------------------------------------
    // Requirement: Trace context propagation
    // ------------------------------------------------------------------

    @Test
    @DisplayName("mustInheritTraceparentFromInboundRequest：入站携带合法 traceparent 时继承并经 X-Trace-Id 回传")
    void mustInheritTraceparentFromInboundRequest() {
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            TraceContextFilter filter = context.getBean(TraceContextFilter.class);
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/demo");
            request.addHeader(TraceLogFields.HEADER_TRACEPARENT, INBOUND_TRACEPARENT);
            MockHttpServletResponse response = new MockHttpServletResponse();
            AtomicReference<String> traceIdInChain = new AtomicReference<>();
            filter.doFilter(request, response,
                    (req, res) -> traceIdInChain.set(MDC.get(TraceLogFields.TRACE_ID)));
            assertThat(response.getHeader(TraceLogFields.HEADER_X_TRACE_ID))
                    .as("响应必须经 X-Trace-Id 回传入站 trace 标识")
                    .isEqualTo(INBOUND_TRACE_ID);
            assertThat(traceIdInChain.get()).isEqualTo(INBOUND_TRACE_ID);
        });
    }

    @Test
    @DisplayName("mustGenerateNewTraceIdWhenNoTraceparent：无追踪头时新建合法 trace 标识")
    void mustGenerateNewTraceIdWhenNoTraceparent() {
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            TraceContextFilter filter = context.getBean(TraceContextFilter.class);
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/demo");
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request, response, (req, res) -> {
            });
            String traceId = response.getHeader(TraceLogFields.HEADER_X_TRACE_ID);
            assertThat(traceId).as("必须生成新的合法 32 位小写十六进制 traceId").matches(TRACE_ID_PATTERN);
            assertThat(traceId).isNotEqualTo(INBOUND_TRACE_ID);
        });
    }

    @Test
    @DisplayName("mustPropagateTraceContextAcrossExecutor：异步线程池透传（MDC + TTL ThreadContext 双写一致）")
    void mustPropagateTraceContextAcrossExecutor() throws Exception {
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            TraceContextBridge bridge = context.getBean(TraceContextBridge.class);
            Executor decorated = TraceContextPropagatingExecutor.decorate(Executors.newSingleThreadExecutor());
            bridge.attach(INBOUND_TRACE_ID, "corr-9", "cause-3");
            try {
                CompletableFuture<Map<String, String>> future = CompletableFuture.supplyAsync(
                        () -> Map.of(
                                TraceLogFields.TRACE_ID, java.util.Objects.toString(MDC.get(TraceLogFields.TRACE_ID), ""),
                                TraceLogFields.CORRELATION_ID,
                                java.util.Objects.toString(
                                        io.ddd4j.core.context.ThreadContext.get(TraceLogFields.CORRELATION_ID), "")),
                        decorated);
                Map<String, String> seen = future.get(5, TimeUnit.SECONDS);
                assertThat(seen.get(TraceLogFields.TRACE_ID))
                        .as("线程池任务内的 trace 标识必须与提交方一致")
                        .isEqualTo(INBOUND_TRACE_ID);
                assertThat(seen.get(TraceLogFields.CORRELATION_ID)).isEqualTo("corr-9");
            } finally {
                bridge.detach();
            }
        });
    }

    @Test
    @DisplayName("mustInjectTraceparentOnRestTemplateOutbound：HTTP 出站（RestTemplate）注入合法 traceparent")
    void mustInjectTraceparentOnRestTemplateOutbound() throws IOException {
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            ClientHttpRequestInterceptor interceptor = context.getBean(ClientHttpRequestInterceptor.class);
            SdkTracerProvider provider = context.getBean(SdkTracerProvider.class);
            Span span = provider.get("golden").spanBuilder("outbound-call").startSpan();
            try (Scope ignored = span.makeCurrent()) {
                AtomicReference<String> header = new AtomicReference<>();
                MockClientHttpRequest mockRequest = new MockClientHttpRequest();
                interceptor.intercept(mockRequest, new byte[0], (req, body) -> {
                    header.set(req.getHeaders().getFirst(TraceLogFields.HEADER_TRACEPARENT));
                    return (ClientHttpResponse) null;
                });
                assertThat(TraceparentCodec.isValid(header.get())).isTrue();
                assertThat(TraceparentCodec.extractTraceId(header.get()))
                        .as("出站 traceparent 的 trace 标识必须与当前调用一致")
                        .isEqualTo(span.getSpanContext().getTraceId());
            } finally {
                span.end();
            }
        });
    }

    @Test
    @DisplayName("mustInjectTraceparentOnWebClientOutbound：HTTP 出站（WebClient）注入合法 traceparent")
    void mustInjectTraceparentOnWebClientOutbound() {
        ExchangeFilterFunction filterFunction = TraceparentWebClientFilter.traceparent();
        AtomicReference<ClientRequest> sent = new AtomicReference<>();
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api")).build();
        filterFunction.filter(request, req -> {
            sent.set(req);
            return reactor.core.publisher.Mono.empty();
        }).block();
        assertThat(sent.get()).isNotNull();
        assertThat(TraceparentCodec.isValid(
                sent.get().headers().getFirst(TraceLogFields.HEADER_TRACEPARENT))).isTrue();
    }

    // ------------------------------------------------------------------
    // Requirement: Sensitive material redaction
    // ------------------------------------------------------------------

    @Test
    @DisplayName("mustRedactSensitiveMaterialAtSpanStart：span 起始属性中的敏感材料被置为占位符")
    void mustRedactSensitiveMaterialAtSpanStart() {
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            List<SpanData> captured = new CopyOnWriteArrayList<>();
            WebApplicationContextRunner runnerWithCapture = new WebApplicationContextRunner(
                    RunnerCompatWebApplicationContext::new)
                    .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
                    .withPropertyValues("ddd4j.observability.tracing.enabled=true")
                    .withBean("captureProcessor", SpanProcessor.class,
                            () -> SimpleSpanProcessor.create(new CapturingExporter(captured)));
            runnerWithCapture.run(ctx -> {
                assertThat(ctx).hasNotFailed();
                SdkTracerProvider provider = ctx.getBean(SdkTracerProvider.class);
                Span span = provider.get("golden").spanBuilder("sensitive-op")
                        .setAttribute("privateKey", "MIIprivate-key-material")
                        .setAttribute("mnemonic", "abandon abandon ability")
                        .startSpan();
                span.end();
                provider.forceFlush().join(10, TimeUnit.SECONDS);
            });
            assertThat(captured).hasSize(1);
            Map<io.opentelemetry.api.common.AttributeKey<?>, Object> attrs = captured.get(0).getAttributes().asMap();
            assertThat(getAttr(attrs, "privateKey")).as("私钥必须脱敏为占位符").isEqualTo("***REDACTED***");
            assertThat(getAttr(attrs, "mnemonic")).as("助记词必须脱敏为占位符").isEqualTo("***REDACTED***");
        });
    }

    @Test
    @DisplayName("mustRedactSensitiveMaterialBeforeExport：启动后追加的属性在导出前也被拦截脱敏")
    void mustRedactSensitiveMaterialBeforeExport() {
        List<SpanData> exported = new CopyOnWriteArrayList<>();
        RedactingSpanExporter redacting =
                new RedactingSpanExporter(SensitiveDataRedactor.withDefaults(), new CapturingExporter(exported));
        SdkTracerProvider provider = SdkTracerProvider.builder()
                .setSampler(Sampler.alwaysOn())
                .addSpanProcessor(SimpleSpanProcessor.create(redacting))
                .build();
        try {
            Span span = provider.get("golden").spanBuilder("late-attribute-op").startSpan();
            span.setAttribute("seed", "raw-seed-value");
            span.setAttribute("orderId", "123456");
            span.end();
            provider.forceFlush().join(10, TimeUnit.SECONDS);
        } finally {
            provider.close();
        }
        assertThat(exported).hasSize(1);
        Map<io.opentelemetry.api.common.AttributeKey<?>, Object> attrs = exported.get(0).getAttributes().asMap();
        assertThat(getAttr(attrs, "seed")).as("导出前必须完成脱敏").isEqualTo("***REDACTED***");
        assertThat(getAttr(attrs, "orderId")).isEqualTo("123456");
        assertThat(exported.get(0).getAttributes().toString()).doesNotContain("raw-seed-value");
    }

    // ------------------------------------------------------------------
    // Requirement: Graceful degradation
    // ------------------------------------------------------------------

    @Test
    @DisplayName("mustDegradeGracefullyWhenBackendUnreachable：OTLP 后端不可达时业务照常完成")
    void mustDegradeGracefullyWhenBackendUnreachable() {
        new WebApplicationContextRunner(RunnerCompatWebApplicationContext::new)
                .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
                .withPropertyValues(
                        "ddd4j.observability.tracing.enabled=true",
                        "ddd4j.observability.tracing.otlp-endpoint=http://127.0.0.1:1/")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    TraceContextFilter filter = context.getBean(TraceContextFilter.class);
                    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/business");
                    MockHttpServletResponse lastResponse = null;
                    // 重复多次触发批量导出排队，后端始终不可达
                    for (int i = 0; i < 20; i++) {
                        lastResponse = new MockHttpServletResponse();
                        MockHttpServletResponse current = lastResponse;
                        filter.doFilter(request, current, (req, res) -> {
                            // 业务逻辑
                        });
                    }
                    assertThat(lastResponse.getHeader(TraceLogFields.HEADER_X_TRACE_ID))
                            .as("后端不可达时业务响应不受影响，诊断头仍回传")
                            .matches(TRACE_ID_PATTERN);
                });
    }

    @Test
    @DisplayName("mustStayDisabledByDefault：默认关闭时无任何追踪装配，行为不变")
    void mustStayDisabledByDefault() {
        new WebApplicationContextRunner(RunnerCompatWebApplicationContext::new)
                .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).doesNotHaveBean(io.micrometer.tracing.Tracer.class);
                    assertThat(context).doesNotHaveBean(SdkTracerProvider.class);
                    assertThat(context).doesNotHaveBean(TraceContextFilter.class);
                    assertThat(context).doesNotHaveBean(TraceContextBridge.class);
                });
    }

    // ------------------------------------------------------------------
    // Requirement: Bounded overhead
    // ------------------------------------------------------------------

    @Test
    @DisplayName("mustRespectSamplingRate：采样率 0.0 不导出、1.0 全采样，本地标识始终可用")
    void mustRespectSamplingRate() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
                .withPropertyValues(
                        "ddd4j.observability.tracing.enabled=true",
                        "ddd4j.observability.tracing.sampling-rate=0.0")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    SdkTracerProvider provider = context.getBean(SdkTracerProvider.class);
                    Span unsampled = provider.get("golden").spanBuilder("sampled-out").startSpan();
                    assertThat(unsampled.getSpanContext().isSampled()).as("采样率 0.0 时不得采样导出").isFalse();
                    assertThat(unsampled.getSpanContext().getTraceId())
                            .as("本地关联标识仍可用").matches(TRACE_ID_PATTERN);
                    unsampled.end();
                });
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(Ddd4jObservabilityAutoConfiguration.class))
                .withPropertyValues(
                        "ddd4j.observability.tracing.enabled=true",
                        "ddd4j.observability.tracing.sampling-rate=1.0")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    SdkTracerProvider provider = context.getBean(SdkTracerProvider.class);
                    Span sampled = provider.get("golden").spanBuilder("sampled-in").startSpan();
                    assertThat(sampled.getSpanContext().isSampled()).as("采样率 1.0 时必须采样").isTrue();
                    sampled.end();
                });
    }

    @Test
    @DisplayName("mustBoundTracingOverhead：开启追踪的高吞吐路径单次开销有界且基线数据入仓")
    void mustBoundTracingOverhead() throws java.io.IOException {
        int iterations = 20_000;
        // 预热 JIT
        timePlainPath(2_000);
        timeTracingPath(2_000);
        long offNanos = timePlainPath(iterations);
        long onNanos = timeTracingPath(iterations);
        double offMicros = offNanos / 1000.0 / iterations;
        double onMicros = onNanos / 1000.0 / iterations;
        assertThat(onMicros).as("开启追踪单次开销必须 < 100µs（有界）").isLessThan(100.0);
        TraceOverheadBaseline.write("trace-overhead-baseline", iterations, offMicros, onMicros);
    }

    /** 关闭态高吞吐路径：无追踪装配，仅等价的直通请求处理。 */
    private long timePlainPath(int iterations) {
        jakarta.servlet.Filter plainFilter = (req, res, chain) -> chain.doFilter(req, res);
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                plainFilter.doFilter(new MockHttpServletRequest("GET", "/api/high-throughput"),
                        new MockHttpServletResponse(), (rq, rs) -> {
                        });
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return System.nanoTime() - start;
    }

    /** 开启态高吞吐路径：完整追踪过滤器（span 创建 + MDC/TTL 双写 + X-Trace-Id 回传）。 */
    private long timeTracingPath(int iterations) {
        final long[] total = {0L};
        enabledRunner.run(context -> {
            assertThat(context).hasNotFailed();
            TraceContextFilter filter = context.getBean(TraceContextFilter.class);
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                try {
                    filter.doFilter(new MockHttpServletRequest("GET", "/api/high-throughput"),
                            new MockHttpServletResponse(), (rq, rs) -> {
                            });
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            total[0] = System.nanoTime() - start;
        });
        return total[0];
    }

    private static Object getAttr(Map<io.opentelemetry.api.common.AttributeKey<?>, Object> attrs, String key) {
        return attrs.entrySet().stream()
                .filter(e -> e.getKey().getKey().equals(key))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    /** 测试用导出捕获器：记录被导出的 span 快照。 */
    static final class CapturingExporter implements SpanExporter {

        private final List<SpanData> sink;

        CapturingExporter(List<SpanData> sink) {
            this.sink = sink;
        }

        @Override
        public io.opentelemetry.sdk.common.CompletableResultCode export(java.util.Collection<SpanData> spans) {
            sink.addAll(spans);
            return io.opentelemetry.sdk.common.CompletableResultCode.ofSuccess();
        }

        @Override
        public io.opentelemetry.sdk.common.CompletableResultCode flush() {
            return io.opentelemetry.sdk.common.CompletableResultCode.ofSuccess();
        }

        @Override
        public io.opentelemetry.sdk.common.CompletableResultCode shutdown() {
            return io.opentelemetry.sdk.common.CompletableResultCode.ofSuccess();
        }
    }
}
