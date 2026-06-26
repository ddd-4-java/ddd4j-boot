package io.ddd4j.data.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ddd4j Data 基础配置属性。
 *
 * <p>对齐 3.4.x 中 ddd4j-boot-data 的 BaseDataProperties 风格，
 * 业务系统可在 application.yml 中通过 {@code ddd4j.data.*} 进行配置。
 */
@ConfigurationProperties(prefix = "ddd4j.data")
public class BaseDataProperties {

    /**
     * 是否启用 Ddd4j Data 自动配置。
     */
    private boolean enabled = true;

    /**
     * 默认分页大小。
     */
    private int defaultPageSize = 10;

    /**
     * 最大分页大小。
     */
    private int maxPageSize = 500;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public int getMaxPageSize() {
        return maxPageSize;
    }

    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }
}
