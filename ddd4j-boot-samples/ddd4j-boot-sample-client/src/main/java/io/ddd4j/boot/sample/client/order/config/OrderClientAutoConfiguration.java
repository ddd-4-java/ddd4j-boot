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

    private static final Logger log = LoggerFactory.getLogger(OrderClientAutoConfiguration.class);

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

    @Bean
    @ConditionalOnMissingBean
    public OrderServiceClient orderServiceClient(
            RestClient restClient,
            OrderClientProperties properties) {
        log.info("订单服务客户端初始化完成，baseUrl: {}", properties.getBaseUrl());
        return new OrderServiceClientImpl(restClient, properties.getBaseUrl());
    }
}
