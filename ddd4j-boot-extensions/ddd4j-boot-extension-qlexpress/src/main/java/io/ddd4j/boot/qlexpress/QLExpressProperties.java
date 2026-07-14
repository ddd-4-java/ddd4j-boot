package io.ddd4j.boot.qlexpress;

import io.ddd4j.extension.qlexpress.model.QLExpressExecutionOptions;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QLExpress Boot 配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = QLExpressProperties.PREFIX)
public class QLExpressProperties {

    public static final String PREFIX = "ddd4j.qlexpress";

    private boolean enabled = true;
    private boolean builtInFunctions = true;
    private boolean allowPrivateAccess = false;
    private boolean traceExpression = false;
    private long timeoutMillis = QLExpressExecutionOptions.DEFAULT_TIMEOUT_MILLIS;
    private boolean cache = true;
    private boolean precise = false;
    private boolean avoidNullPointer = false;
    private int maxArrayLength = QLExpressExecutionOptions.DEFAULT_MAX_ARRAY_LENGTH;
    private final Rules rules = new Rules();

    @Getter
    @Setter
    public static class Rules {

        private boolean enabled = true;
        private String cacheName = "ddd4j:qlexpress:rules";
    }
}
