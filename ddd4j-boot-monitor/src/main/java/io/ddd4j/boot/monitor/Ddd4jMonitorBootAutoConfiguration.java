package io.ddd4j.boot.monitor;

import io.ddd4j.monitor.infras.config.BaseMonitorConfig;
import io.ddd4j.monitor.infras.config.BaseMonitorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * ddd4j-monitor 的 Spring Boot 整合入口：绑定配置属性并导入 Framework 层 {@link BaseMonitorConfig}。
 */
@AutoConfiguration
@Import(BaseMonitorConfig.class)
public class Ddd4jMonitorBootAutoConfiguration {

    /**
     * 绑定 {@code base-monitor.*} 配置到 {@link BaseMonitorProperties}。
     */
    @Bean
    @ConditionalOnMissingBean(BaseMonitorProperties.class)
    @ConfigurationProperties(prefix = "base-monitor")
    public BaseMonitorProperties baseMonitorProperties() {
        return new BaseMonitorProperties();
    }
}
