package io.ddd4j.boot.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 可观测性配置属性（配置键冻结于家族 design D9）。
 *
 * <p>配置键：{@code ddd4j.observability.tracing.*}；
 * 默认关闭语义：{@code ddd4j.observability.tracing.enabled=false}。
 *
 * <p>对应 spec：boot-trace-observability / Graceful degradation、Bounded overhead。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@ConfigurationProperties(prefix = ObservabilityProperties.PREFIX)
public class ObservabilityProperties {

    /**
     * 配置前缀。
     */
    public static final String PREFIX = "ddd4j.observability.tracing";

    /**
     * 追踪总开关（默认 false，默认关闭时对外行为不变）。
     */
    private boolean enabled = false;

    /**
     * 采样率（0.0–1.0，默认 1.0 全采样；配置键 sampling-rate）。
     */
    private double samplingRate = 1.0;

    /**
     * OTLP 导出端点（为空时不装配导出器，本地记录仅保留关联标识）。
     */
    private String otlpEndpoint;

    /**
     * 服务名（span Resource 的 service.name 属性）。
     */
    private String serviceName = "ddd4j-application";

    /**
     * 脱敏白名单（默认拒绝 + 白名单放行，命中白名单的 key 保持原值）。
     */
    private Set<String> redactAllowlist = new LinkedHashSet<>();

    /**
     * 是否启用追踪。
     *
     * @return true 表示启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置追踪总开关。
     *
     * @param enabled true 启用
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 读取采样率。
     *
     * @return 采样率（0.0–1.0）
     */
    public double getSamplingRate() {
        return samplingRate;
    }

    /**
     * 设置采样率。
     *
     * @param samplingRate 采样率
     */
    public void setSamplingRate(final double samplingRate) {
        this.samplingRate = samplingRate;
    }

    /**
     * 读取 OTLP 导出端点。
     *
     * @return 端点 URL；未配置返回 null
     */
    public String getOtlpEndpoint() {
        return otlpEndpoint;
    }

    /**
     * 设置 OTLP 导出端点。
     *
     * @param otlpEndpoint 端点 URL
     */
    public void setOtlpEndpoint(final String otlpEndpoint) {
        this.otlpEndpoint = otlpEndpoint;
    }

    /**
     * 读取服务名。
     *
     * @return 服务名
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置服务名。
     *
     * @param serviceName 服务名
     */
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 读取脱敏白名单。
     *
     * @return 白名单集合
     */
    public Set<String> getRedactAllowlist() {
        return redactAllowlist;
    }

    /**
     * 设置脱敏白名单。
     *
     * @param redactAllowlist 白名单集合
     */
    public void setRedactAllowlist(final Set<String> redactAllowlist) {
        this.redactAllowlist = Objects.isNull(redactAllowlist) ? new LinkedHashSet<>() : redactAllowlist;
    }
}
