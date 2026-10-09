package io.ddd4j.boot.sample.client.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 订单服务客户端配置属性
 *
 * <p>用于配置订单服务的基础URL等参数。</p>
 *
 * <h3>配置示例：</h3>
 * <pre>{@code
 * order:
 *   service:
 *     base-url: http://localhost:8080
 *     connect-timeout: 5000
 *     read-timeout: 10000
 * }</pre>
 *
 * @author DDD4J
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "order.service")
public class OrderClientProperties {

    /**
     * 订单服务基础URL
     * 例如：http://localhost:8080
     */
    private String baseUrl = "http://localhost:8080";

    /**
     * 连接超时时间（毫秒）
     * 默认：5000ms
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     * 默认：10000ms
     */
    private int readTimeout = 10000;
}

