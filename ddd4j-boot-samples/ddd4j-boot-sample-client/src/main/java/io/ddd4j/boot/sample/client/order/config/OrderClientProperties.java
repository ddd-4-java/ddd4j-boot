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

    /**
     * 构造OrderClientProperties对象（默认无参构造，字段由调用方逐个设置）。
     */
    public OrderClientProperties() {
    }

    /** 订单服务基础地址 */
    private String baseUrl = "http://localhost:8080";
    /** 连接超时时间（毫秒） */
    private int connectTimeout = 5000;
    /** 读取超时时间（毫秒） */
    private int readTimeout = 10000;

    /** 获取订单服务基础地址。
     * @return 订单服务基础地址 */
    public String getBaseUrl() { return baseUrl; }
    /** 设置订单服务基础地址。
     * @param baseUrl 订单服务基础地址（如 http://localhost:8080） */
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    /** 获取连接超时时间（毫秒）。
     * @return 连接超时时间（毫秒） */
    public int getConnectTimeout() { return connectTimeout; }
    /** 设置连接超时时间（毫秒）。
     * @param connectTimeout 连接超时时间（毫秒） */
    public void setConnectTimeout(int connectTimeout) { this.connectTimeout = connectTimeout; }
    /** 获取读取超时时间（毫秒）。
     * @return 读取超时时间（毫秒） */
    public int getReadTimeout() { return readTimeout; }
    /** 设置读取超时时间（毫秒）。
     * @param readTimeout 读取超时时间（毫秒） */
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
}
