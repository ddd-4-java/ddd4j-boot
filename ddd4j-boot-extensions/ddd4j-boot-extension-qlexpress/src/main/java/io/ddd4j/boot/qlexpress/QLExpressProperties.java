package io.ddd4j.boot.qlexpress;

import io.ddd4j.extension.qlexpress.model.QLExpressExecutionOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * QLExpress Boot 配置。
 *
 * <p>对应 {@code ddd4j.qlexpress.*} 配置前缀，覆盖引擎开关、执行选项与规则缓存子配置。
 */
@ConfigurationProperties(prefix = QLExpressProperties.PREFIX)
public class QLExpressProperties {

    /**
     * 无参构造器，供 Spring 配置绑定反射实例化使用。
     */
    public QLExpressProperties() {
    }

    /** 配置前缀：{@code ddd4j.qlexpress}。 */
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

    /** 获取引擎总开关。
     * @return 是否启用 QLExpress */
    public boolean isEnabled() { return enabled; }
    /** 设置引擎总开关。
     * @param enabled 是否启用 QLExpress */
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    /** 获取内置函数开关。
     * @return 是否加载内置函数 */
    public boolean isBuiltInFunctions() { return builtInFunctions; }
    /** 设置内置函数开关。
     * @param builtInFunctions 是否加载内置函数 */
    public void setBuiltInFunctions(boolean builtInFunctions) { this.builtInFunctions = builtInFunctions; }
    /** 获取私有成员访问开关。
     * @return 是否允许访问私有成员 */
    public boolean isAllowPrivateAccess() { return allowPrivateAccess; }
    /** 设置私有成员访问开关。
     * @param allowPrivateAccess 是否允许访问私有成员 */
    public void setAllowPrivateAccess(boolean allowPrivateAccess) { this.allowPrivateAccess = allowPrivateAccess; }
    /** 获取表达式执行轨迹开关。
     * @return 是否输出执行轨迹 */
    public boolean isTraceExpression() { return traceExpression; }
    /** 设置表达式执行轨迹开关。
     * @param traceExpression 是否输出执行轨迹 */
    public void setTraceExpression(boolean traceExpression) { this.traceExpression = traceExpression; }
    /** 获取执行超时时间。
     * @return 超时毫秒数 */
    public long getTimeoutMillis() { return timeoutMillis; }
    /** 设置执行超时时间。
     * @param timeoutMillis 超时毫秒数 */
    public void setTimeoutMillis(long timeoutMillis) { this.timeoutMillis = timeoutMillis; }
    /** 获取表达式编译缓存开关。
     * @return 是否启用编译缓存 */
    public boolean isCache() { return cache; }
    /** 设置表达式编译缓存开关。
     * @param cache 是否启用编译缓存 */
    public void setCache(boolean cache) { this.cache = cache; }
    /** 获取精确计算开关。
     * @return 是否启用精确计算 */
    public boolean isPrecise() { return precise; }
    /** 设置精确计算开关。
     * @param precise 是否启用精确计算 */
    public void setPrecise(boolean precise) { this.precise = precise; }
    /** 获取空指针规避开关。
     * @return 是否规避空指针 */
    public boolean isAvoidNullPointer() { return avoidNullPointer; }
    /** 设置空指针规避开关。
     * @param avoidNullPointer 是否规避空指针 */
    public void setAvoidNullPointer(boolean avoidNullPointer) { this.avoidNullPointer = avoidNullPointer; }
    /** 获取数组最大长度限制。
     * @return 数组最大长度 */
    public int getMaxArrayLength() { return maxArrayLength; }
    /** 设置数组最大长度限制。
     * @param maxArrayLength 数组最大长度 */
    public void setMaxArrayLength(int maxArrayLength) { this.maxArrayLength = maxArrayLength; }
    /** 获取规则管理子配置。
     * @return 规则管理配置实例 */
    public Rules getRules() { return rules; }

    /**
     * 规则管理子配置（{@code ddd4j.qlexpress.rules.*}），控制规则功能开关与缓存名。
     */
    public static class Rules {

        /**
         * 无参构造器，供 Spring 配置绑定反射实例化使用。
         */
        public Rules() {
        }

        private boolean enabled = true;
        private String cacheName = "ddd4j:qlexpress:rules";
        /** 获取规则功能开关。
         * @return 是否启用规则管理 */
        public boolean isEnabled() { return enabled; }
        /** 设置规则功能开关。
         * @param enabled 是否启用规则管理 */
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        /** 获取规则缓存键前缀。
         * @return 缓存名称 */
        public String getCacheName() { return cacheName; }
        /** 设置规则缓存键前缀。
         * @param cacheName 缓存名称 */
        public void setCacheName(String cacheName) { this.cacheName = cacheName; }
    }
}
