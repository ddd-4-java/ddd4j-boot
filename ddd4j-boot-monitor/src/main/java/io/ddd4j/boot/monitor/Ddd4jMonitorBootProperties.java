package io.ddd4j.boot.monitor;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ddd4j Monitor 基础配置属性。
 *
 * <p>业务系统可在 application.yml 中通过 {@code ddd4j.monitor.*} 进行配置。
 */
@ConfigurationProperties(prefix = "ddd4j.monitor")
public class Ddd4jMonitorBootProperties {

    /**
     * 是否启用 Ddd4j Monitor 自动配置。
     */
    private boolean enabled = true;

    /**
     * 是否暴露 Prometheus 抓取端点。
     */
    private boolean prometheusEnabled = true;

    /**
     * 健康检查端点路径。
     */
    private String healthPath = "/actuator/health";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPrometheusEnabled() {
        return prometheusEnabled;
    }

    public void setPrometheusEnabled(boolean prometheusEnabled) {
        this.prometheusEnabled = prometheusEnabled;
    }

    public String getHealthPath() {
        return healthPath;
    }

    public void setHealthPath(String healthPath) {
        this.healthPath = healthPath;
    }
}
