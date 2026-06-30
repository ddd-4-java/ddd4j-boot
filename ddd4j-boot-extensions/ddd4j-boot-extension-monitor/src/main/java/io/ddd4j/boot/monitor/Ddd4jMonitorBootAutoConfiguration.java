package io.ddd4j.boot.monitor;

import ch.qos.logback.classic.LoggerContext;
import io.ddd4j.extension.monitor.application.service.DingDingRobotSender;
import io.ddd4j.extension.monitor.application.service.QiWeiRobotSender;
import io.ddd4j.extension.monitor.domain.robot.service.RobotLogbackAppendService;
import io.ddd4j.extension.monitor.infras.config.BaseMonitorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ddd4j-extension-monitor Spring Boot integration.
 */
@AutoConfiguration
@ConditionalOnClass(LoggerContext.class)
@ConditionalOnProperty(prefix = "base-monitor.log", name = "enable", havingValue = "true", matchIfMissing = true)
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

    @Bean
    @ConditionalOnMissingBean(RobotLogbackAppendService.class)
    public RobotLogbackAppendService robotLogbackAppendService() {
        return new RobotLogbackAppendService();
    }

    @Bean
    @ConditionalOnMissingBean(QiWeiRobotSender.class)
    @ConditionalOnProperty(prefix = "base-monitor.log.qiwei", name = "enable", havingValue = "true", matchIfMissing = true)
    public QiWeiRobotSender qiWeiRobotSender(BaseMonitorProperties properties) {
        return new QiWeiRobotSender(properties.getLog().getQiwei().getKey());
    }

    @Bean
    @ConditionalOnMissingBean(DingDingRobotSender.class)
    @ConditionalOnProperty(prefix = "base-monitor.log.dingding", name = "enable", havingValue = "true", matchIfMissing = true)
    public DingDingRobotSender dingDingRobotSender(BaseMonitorProperties properties) {
        return new DingDingRobotSender(
                properties.getLog().getDingding().getToken(),
                properties.getLog().getDingding().getSecret());
    }
}
