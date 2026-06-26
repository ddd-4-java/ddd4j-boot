package io.ddd4j.boot.monitor;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Ddd4j Monitor 自动配置入口。
 *
 * <p>对齐 3.4.x 的 ddd4j-boot-monitor / Ddd4jMonitorBootAutoConfiguration。
 * 包路径保留 {@code io.ddd4j.boot.monitor} 以与 3.4.x 完全一致。
 *
 * <p>本模块负责：
 * <ul>
 *     <li>启用 Actuator 端点（health/info/metrics/prometheus）</li>
 *     <li>暴露 Prometheus 指标抓取端点</li>
 *     <li>统一 Micrometer 标签命名规范</li>
 * </ul>
 *
 * <p>具体日志/审计/调用链扩展由 ddd4j-boot-extensions 下的 data-logs 等子模块提供。
 */
@Configuration
@EnableConfigurationProperties(Ddd4jMonitorBootProperties.class)
public class Ddd4jMonitorBootAutoConfiguration {

}
