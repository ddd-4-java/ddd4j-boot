package io.ddd4j.boot.monitor;

import ch.qos.logback.classic.LoggerContext;
import io.ddd4j.extension.monitor.config.BaseMonitorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ddd4j-extension-monitor Spring Boot integration.
 *
 * <p>v2.x 重构后，上游 monitor 扩展仅暴露统一的 {@link BaseMonitorProperties} 配置模型，
 * 实际日志告警发送与 Appender 由业务项目根据 {@code ddd4j} 自身运行时 SPI 装配。
 * 本自动配置负责在 Spring Boot 环境下启用 monitor 配置绑定能力，
 * 并提供告警追踪关联渲染器（告警输出携带 traceId + correlationId，D9 冻结字段名；
 * 对应 spec：boot-trace-observability / Alert correlation）。
 */
@AutoConfiguration
@ConditionalOnClass(LoggerContext.class)
public class Ddd4jMonitorBootAutoConfiguration {

    /**
     * 绑定 {@code monitor.*} 配置到 {@link BaseMonitorProperties}。
     */
    @Bean
    @ConditionalOnMissingBean(BaseMonitorProperties.class)
    @ConfigurationProperties(prefix = BaseMonitorProperties.PREFIX)
    public BaseMonitorProperties baseMonitorProperties() {
        return new BaseMonitorProperties();
    }

    /**
     * 告警追踪关联渲染器（业务发送告警时调用 {@link AlertTraceContext#decorate(String)}
     * 即可携带 traceId/correlationId，使告警可一键回溯调用链）。
     */
    @Bean
    @ConditionalOnMissingBean(AlertTraceContext.class)
    public AlertTraceContext alertTraceContext() {
        return new AlertTraceContext();
    }
}
