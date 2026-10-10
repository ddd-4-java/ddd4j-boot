package io.ddd4j.boot.observability;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * OTLP 导出器装配条件：仅当 {@code ddd4j.observability.tracing.otlp-endpoint} 非空时装配导出器。
 *
 * <p>未配置端点 = 无导出模式（Graceful degradation：仅保留本地关联标识）；
 * 对应 spec：boot-trace-observability / Graceful degradation。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public class OtlpEndpointCondition implements Condition {

    /**
     * 显式无参构造器，供 Spring 实例化条件判定对象。
     */
    public OtlpEndpointCondition() {
    }

    /**
     * 判断 OTLP 端点是否已配置。
     *
     * @param context 条件上下文
     * @param metadata 注解元数据
     * @return true 表示端点已配置（非空白）
     */
    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        String endpoint = context.getEnvironment()
                .getProperty(ObservabilityProperties.PREFIX + ".otlp-endpoint");
        return StringUtils.hasText(endpoint);
    }
}
