package io.ddd4j.boot.observability;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * ddd4j-boot 可观测性自动装配（micrometer-tracing + OpenTelemetry bridge + OTLP 导出）。
 *
 * <p>装配门禁：{@code ddd4j.observability.tracing.enabled=true}（默认关闭，
 * 关闭时零装配、对外行为不变 —— 对应 spec Graceful degradation / 默认关闭零影响）。
 * 全部 Bean {@code @ConditionalOnMissingBean} 退让，业务自带 Tracer/Provider 时不干预。
 *
 * <p>对应家族 design D1/D2/D3/D5；对应 spec：boot-trace-observability 全部 Requirement。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@AutoConfiguration
@ConditionalOnClass({Tracer.class, SdkTracerProvider.class})
@ConditionalOnProperty(prefix = ObservabilityProperties.PREFIX, name = "enabled",
        havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(ObservabilityProperties.class)
public class Ddd4jObservabilityAutoConfiguration {

    /**
     * Instrumentation Scope 名（OTel Tracer 实例标识）。
     */
    private static final String INSTRUMENTATION_SCOPE = "io.ddd4j.boot.observability";

    /**
     * 敏感材料脱敏器（默认拒绝 + 白名单）。
     *
     * @param properties 配置属性
     * @return 脱敏器
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveDataRedactor.class)
    public SensitiveDataRedactor sensitiveDataRedactor(final ObservabilityProperties properties) {
        return new SensitiveDataRedactor(properties.getRedactAllowlist());
    }

    /**
     * 起始期脱敏 span 处理器（span 记录前即脱敏，第一道防线）。
     *
     * @param redactor 脱敏器
     * @return span 处理器
     */
    @Bean
    @ConditionalOnMissingBean(SensitiveDataSpanProcessor.class)
    public SensitiveDataSpanProcessor sensitiveDataSpanProcessor(final SensitiveDataRedactor redactor) {
        return new SensitiveDataSpanProcessor(redactor);
    }

    /**
     * MDC + TTL ThreadContext 双写桥接。
     *
     * @return 桥接实现
     */
    @Bean
    @ConditionalOnMissingBean(TraceContextBridge.class)
    public TraceContextBridge traceContextBridge() {
        return new MdcTtlTraceContextBridge();
    }

    /**
     * OTLP 导出器（仅当端点已配置时装配；异步批量导出，后端不可达不阻塞业务）。
     *
     * @param properties 配置属性
     * @return OTLP HTTP 导出器
     */
    @Bean
    @Conditional(OtlpEndpointCondition.class)
    @ConditionalOnMissingBean(OtlpHttpSpanExporter.class)
    public OtlpHttpSpanExporter otlpHttpSpanExporter(final ObservabilityProperties properties) {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(properties.getOtlpEndpoint())
                .build();
    }

    /**
     * OTel SDK TracerProvider：采样器（sampling-rate）+ 起始期脱敏 + 导出期脱敏 OTLP 批量导出。
     *
     * <p>不依赖 Boot 4 tracing 自动装配的导出门禁（management.*），配置语义由 D9 冻结键承载（design D2）。
     *
     * @param properties      配置属性
     * @param startRedaction  起始期脱敏处理器
     * @param extraProcessors 用户自定义 span 处理器（追加，不覆盖）
     * @param otlpExporter    可选 OTLP 导出器（未配置端点时缺省 = 无导出模式）
     * @return SDK TracerProvider
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(SdkTracerProvider.class)
    public SdkTracerProvider ddd4jSdkTracerProvider(final ObservabilityProperties properties,
                                                    final SensitiveDataSpanProcessor startRedaction,
                                                    final ObjectProvider<SpanProcessor> extraProcessors,
                                                    final ObjectProvider<OtlpHttpSpanExporter> otlpExporter) {
        SdkTracerProviderBuilder builder = SdkTracerProvider.builder()
                .setSampler(Sampler.parentBased(
                        Sampler.traceIdRatioBased(clampSamplingRate(properties.getSamplingRate()))))
                .setResource(Resource.getDefault().merge(Resource.create(Attributes.of(
                        AttributeKey.stringKey("service.name"), properties.getServiceName()))))
                // 脱敏先于一切导出（D5）：注册顺序即 onStart 调用顺序，脱敏处理器必须最先
                .addSpanProcessor(startRedaction);
        extraProcessors.orderedStream().forEach(builder::addSpanProcessor);
        // 后端不可达降级：BatchSpanProcessor 异步导出吞掉导出错误，业务零感知（Graceful degradation）
        otlpExporter.ifAvailable(exporter ->
                builder.addSpanProcessor(BatchSpanProcessor.builder(
                                new RedactingSpanExporter(
                                        new SensitiveDataRedactor(properties.getRedactAllowlist()), exporter))
                        .build()));
        return builder.build();
    }

    /**
     * OTel API Tracer（供过滤器等框架中立组件直接使用）。
     *
     * @param provider SDK TracerProvider
     * @return OTel API Tracer
     */
    @Bean
    @ConditionalOnMissingBean(io.opentelemetry.api.trace.Tracer.class)
    public io.opentelemetry.api.trace.Tracer otelApiTracer(final SdkTracerProvider provider) {
        return provider.get(INSTRUMENTATION_SCOPE);
    }

    /**
     * micrometer {@link Tracer}（OtelTracer 桥接；Boot 侧 ObservationRegistry 自动识别并退让）。
     *
     * @param otelTracer OTel API Tracer
     * @return micrometer Tracer
     */
    @Bean
    @ConditionalOnMissingBean(Tracer.class)
    public Tracer ddd4jTracer(final io.opentelemetry.api.trace.Tracer otelTracer) {
        return new OtelTracer(otelTracer, new OtelCurrentTraceContext(), event -> {
        });
    }

    /**
     * RestTemplate 出站追踪拦截器（挂载点：{@code RestTemplate#setInterceptors}）。
     *
     * @return 拦截器
     */
    @Bean
    @ConditionalOnMissingBean(TraceparentRestTemplateInterceptor.class)
    @ConditionalOnClass(name = "org.springframework.http.client.ClientHttpRequestInterceptor")
    public TraceparentRestTemplateInterceptor traceparentRestTemplateInterceptor() {
        return new TraceparentRestTemplateInterceptor();
    }

    /**
     * 入站追踪过滤器（Servlet Web 应用时装配；
     * Filter Bean 由 ServletContextInitializerBeans 自动注册到全部路径）。
     *
     * @param otelTracer OTel API Tracer
     * @param bridge     追踪上下文桥接
     * @return 过滤器
     */
    @Bean
    @ConditionalOnMissingBean(TraceContextFilter.class)
    @ConditionalOnClass(name = "jakarta.servlet.http.HttpServletRequest")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public TraceContextFilter traceContextFilter(final io.opentelemetry.api.trace.Tracer otelTracer,
                                                 final TraceContextBridge bridge) {
        return new TraceContextFilter(otelTracer, bridge);
    }

    /**
     * 采样率夹紧到 [0.0, 1.0]。
     *
     * @param rate 配置值
     * @return 合法采样率
     */
    private double clampSamplingRate(final double rate) {
        if (Double.isNaN(rate) || rate < 0.0D) {
            return 0.0D;
        }
        return Math.min(rate, 1.0D);
    }
}
