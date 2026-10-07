package io.ddd4j.boot.observability;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

import java.util.Map;
import java.util.Objects;

/**
 * span 起始期脱敏处理器：敏感属性在进入 span 记录前即被改写为占位符（第一道防线）。
 *
 * <p>对应家族 design D5（脱敏先于一切导出）；
 * 对应 spec：boot-trace-observability / Sensitive material redaction。
 * 导出期兜底由 {@link RedactingSpanExporter} 承担。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class SensitiveDataSpanProcessor implements SpanProcessor {

    /**
     * 脱敏器。
     */
    private final SensitiveDataRedactor redactor;

    /**
     * 构建起始期脱敏处理器。
     *
     * @param redactor 脱敏器（不可为 null）
     */
    public SensitiveDataSpanProcessor(final SensitiveDataRedactor redactor) {
        this.redactor = Objects.requireNonNull(redactor, "redactor must not be null");
    }

    /**
     * span 开始时改写敏感属性为占位符。
     *
     * @param parentContext 父上下文
     * @param span          可写 span
     */
    @Override
    public void onStart(final Context parentContext, final ReadWriteSpan span) {
        Attributes attributes = ((ReadableSpan) span).getAttributes();
        for (Map.Entry<AttributeKey<?>, Object> entry : attributes.asMap().entrySet()) {
            if (redactor.isSensitive(entry.getKey().getKey())) {
                span.setAttribute(AttributeKey.stringKey(entry.getKey().getKey()), SensitiveDataRedactor.PLACEHOLDER);
            }
        }
    }

    /**
     * span 结束：本处理器不处理结束期（结束期兜底在导出器完成）。
     *
     * @param span 只读 span
     */
    @Override
    public void onEnd(final ReadableSpan span) {
        // 起始期已脱敏，结束期不重复处理
    }

    /**
     * 是否需要起始回调。
     *
     * @return true
     */
    @Override
    public boolean isStartRequired() {
        return true;
    }

    /**
     * 是否需要结束回调。
     *
     * @return false
     */
    @Override
    public boolean isEndRequired() {
        return false;
    }
}
