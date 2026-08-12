package io.ddd4j.boot.qlexpress;

import io.ddd4j.extension.qlexpress.model.QLExpressExecutionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QLExpress Boot 配置。
 */
@ConfigurationProperties(prefix = QLExpressProperties.PREFIX)
public class QLExpressProperties {

    public static final String PREFIX = "ddd4j.qlexpress";

    private boolean enabled = true;
    private boolean builtInFunctions = true;
    private boolean allowPrivateAccess;
    private boolean traceExpression;
    private long timeoutMillis = QLExpressExecutionOptions.DEFAULT_TIMEOUT_MILLIS;
    private boolean cache = true;
    private boolean precise;
    private boolean avoidNullPointer;
    private int maxArrayLength = QLExpressExecutionOptions.DEFAULT_MAX_ARRAY_LENGTH;
    private final Rules rules = new Rules();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isBuiltInFunctions() { return builtInFunctions; }
    public void setBuiltInFunctions(boolean builtInFunctions) { this.builtInFunctions = builtInFunctions; }
    public boolean isAllowPrivateAccess() { return allowPrivateAccess; }
    public void setAllowPrivateAccess(boolean allowPrivateAccess) { this.allowPrivateAccess = allowPrivateAccess; }
    public boolean isTraceExpression() { return traceExpression; }
    public void setTraceExpression(boolean traceExpression) { this.traceExpression = traceExpression; }
    public long getTimeoutMillis() { return timeoutMillis; }
    public void setTimeoutMillis(long timeoutMillis) { this.timeoutMillis = timeoutMillis; }
    public boolean isCache() { return cache; }
    public void setCache(boolean cache) { this.cache = cache; }
    public boolean isPrecise() { return precise; }
    public void setPrecise(boolean precise) { this.precise = precise; }
    public boolean isAvoidNullPointer() { return avoidNullPointer; }
    public void setAvoidNullPointer(boolean avoidNullPointer) { this.avoidNullPointer = avoidNullPointer; }
    public int getMaxArrayLength() { return maxArrayLength; }
    public void setMaxArrayLength(int maxArrayLength) { this.maxArrayLength = maxArrayLength; }
    public Rules getRules() { return rules; }

    public static class Rules {
        private boolean enabled = true;
        private String cacheName = "ddd4j:qlexpress:rules";
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getCacheName() { return cacheName; }
        public void setCacheName(String cacheName) { this.cacheName = cacheName; }
    }
}
