package io.ddd4j.boot.sample.client.order.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 订单服务客户端配置属性。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式实现 getter/setter，
 * 保持与 Boot 配置绑定兼容。</p>
 */
@ConfigurationProperties(prefix = "order.service")
public class OrderClientProperties {

    private String baseUrl = "http://localhost:8080";
    private int connectTimeout = 5000;
    private int readTimeout = 10000;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }
    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
}
