package io.ddd4j.boot.sample.client.order.config;

import io.ddd4j.boot.sample.client.order.api.OrderServiceClient;
import io.ddd4j.boot.sample.client.order.impl.OrderServiceClientImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 订单服务客户端自动配置
 * 
 * <p>自动配置 RestTemplate 和 OrderServiceClient 实现。</p>
 * 
 * @author DDD4J
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(OrderClientProperties.class)
public class OrderClientAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(OrderClientProperties properties) {
        ClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        ((SimpleClientHttpRequestFactory) factory).setConnectTimeout(properties.getConnectTimeout());
        ((SimpleClientHttpRequestFactory) factory).setReadTimeout(properties.getReadTimeout());
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        log.info("订单服务客户端 RestTemplate 初始化完成，baseUrl: {}", properties.getBaseUrl());
        return restTemplate;
    }
    
    @Bean
    @ConditionalOnMissingBean
    public OrderServiceClient orderServiceClient(
            RestTemplate restTemplate,
            OrderClientProperties properties) {
        log.info("订单服务客户端初始化完成，baseUrl: {}", properties.getBaseUrl());
        return new OrderServiceClientImpl(restTemplate, properties.getBaseUrl());
    }

}

