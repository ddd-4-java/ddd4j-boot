package io.ddd4j.boot.sample.client.order.config;

import io.ddd4j.boot.sample.client.order.api.OrderServiceClient;
import io.ddd4j.boot.sample.client.order.impl.OrderServiceClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * 订单服务客户端自动配置。
 *
 * <p>本项目未启用 Lombok 注解处理器，因此显式使用 SLF4J Logger。</p>
 */
@AutoConfiguration
@EnableConfigurationProperties(OrderClientProperties.class)
public class OrderClientAutoConfiguration {

    /**
     * 构造OrderClientAutoConfiguration对象（默认无参构造，字段由调用方逐个设置）。
     */
    public OrderClientAutoConfiguration() {
    }

    private static final Logger log = LoggerFactory.getLogger(OrderClientAutoConfiguration.class);

    /**
     * 构建订单服务客户端使用的 {@link RestClient}。
     *
     * <p>按配置属性设置连接/读取超时，并记录初始化日志。</p>
     *
     * @param properties 订单服务客户端配置属性
     * @return 配置完成的 RestClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(OrderClientProperties properties) {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        ((SimpleClientHttpRequestFactory) factory).setConnectTimeout(properties.getConnectTimeout());
        ((SimpleClientHttpRequestFactory) factory).setReadTimeout(properties.getReadTimeout());

        RestClient restClient = RestClient.builder()
                .requestFactory(factory)
                .build();

        log.info("订单服务客户端 RestClient 初始化完成，baseUrl: {}", properties.getBaseUrl());
        return restClient;
    }

    /**
     * 注册订单服务客户端 Bean。
     *
     * @param restClient 注入的 RestClient
     * @param properties 订单服务客户端配置属性
     * @return 基于 RestClient 的订单服务客户端实现
     */
    @Bean
    @ConditionalOnMissingBean
    public OrderServiceClient orderServiceClient(
            RestClient restClient,
            OrderClientProperties properties) {
        log.info("订单服务客户端初始化完成，baseUrl: {}", properties.getBaseUrl());
        return new OrderServiceClientImpl(restClient, properties.getBaseUrl());
    }
}
