package io.ddd4j.boot.observability;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.data.DelegatingSpanData;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 导出期脱敏导出器（最后一道防线）：包装真实导出器，导出前重写 span 属性，
 * 命中敏感匹配集的值（含 span 启动后追加的属性）一律置为 {@link SensitiveDataRedactor#PLACEHOLDER}。
 *
 * <p>对应家族 design D5（脱敏先于一切导出）；
 * 对应 spec：boot-trace-observability / Sensitive material redaction。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class RedactingSpanExporter implements SpanExporter {

    /**
     * 脱敏器。
     */
    private final SensitiveDataRedactor redactor;

    /**
     * 被包装的真实导出器。
     */
    private final SpanExporter delegate;

    /**
     * 构建导出期脱敏导出器。
     *
     * @param redactor 脱敏器（不可为 null）
     * @param delegate 真实导出器（不可为 null）
     */
    public RedactingSpanExporter(final SensitiveDataRedactor redactor, final SpanExporter delegate) {
        this.redactor = Objects.requireNonNull(redactor, "redactor must not be null");
        this.delegate = Objects.requireNonNull(delegate, "delegate must not be null");
    }

    /**
     * 导出：先逐 span 脱敏再交给真实导出器。
     *
     * @param spans 待导出 span 快照
     * @return 导出结果
     */
    @Override
    public CompletableResultCode export(final Collection<SpanData> spans) {
        Collection<SpanData> redacted = spans.stream()
                .map(this::redact)
                .collect(Collectors.toList());
        return delegate.export(redacted);
    }

    /**
     * 刷新底层导出器。
     *
     * @return 刷新结果
     */
    @Override
    public CompletableResultCode flush() {
        return delegate.flush();
    }

    /**
     * 关闭底层导出器。
     *
     * @return 关闭结果
     */
    @Override
    public CompletableResultCode shutdown() {
        return delegate.shutdown();
    }

    /**
     * 对单个 span 做导出期脱敏；无敏感属性时原样返回避免包装开销。
     *
     * <p>Attributes 键按 (name, type) 区分：先移除原类型键，再以字符串键写入占位符，防止原值残留。
     *
     * @param span 原始 span 快照
     * @return 脱敏后的 span 快照（或原对象）
     */
    private SpanData redact(final SpanData span) {
        Attributes attributes = span.getAttributes();
        List<AttributeKey<?>> sensitiveKeys = attributes.asMap().keySet().stream()
                .filter(key -> redactor.isSensitive(key.getKey()))
                .collect(Collectors.toList());
        if (sensitiveKeys.isEmpty()) {
            return span;
        }
        AttributesBuilder builder = attributes.toBuilder();
        for (AttributeKey<?> key : sensitiveKeys) {
            builder.remove(key);
            builder.put(AttributeKey.stringKey(key.getKey()), SensitiveDataRedactor.PLACEHOLDER);
        }
        Attributes scrubbed = builder.build();
        return new DelegatingSpanData(span) {
            @Override
            public Attributes getAttributes() {
                return scrubbed;
            }
        };
    }
}
