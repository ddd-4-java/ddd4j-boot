package io.ddd4j.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Ddd4j MQ 基础配置属性。
 *
 * <p>业务系统可在 application.yml 中通过 {@code ddd4j.mq.*} 进行配置。
 */
@ConfigurationProperties(prefix = "ddd4j.mq")
public class Ddd4jMQProperties {

    /**
     * 是否启用 Ddd4j MQ 自动配置。
     */
    private boolean enabled = true;

    /**
     * 默认发布超时（毫秒）。
     */
    private long publishTimeoutMs = 5000L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getPublishTimeoutMs() {
        return publishTimeoutMs;
    }

    public void setPublishTimeoutMs(long publishTimeoutMs) {
        this.publishTimeoutMs = publishTimeoutMs;
    }
}
