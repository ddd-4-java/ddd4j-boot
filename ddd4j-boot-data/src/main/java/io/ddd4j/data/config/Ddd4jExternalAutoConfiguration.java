package io.ddd4j.data.config;

import io.github.easy4j.ip2region.spring.boot.IP2regionTemplate;
import io.ddd4j.data.external.ExternalProperties;
import io.ddd4j.data.external.region.BaiduRegionTemplate;
import io.ddd4j.data.external.region.NestedRegionTemplate;
import io.ddd4j.data.external.region.PconlineRegionTemplate;
import io.ddd4j.data.external.weather.WeatherTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperationTemplate;
import org.springframework.web.client.RestClient;

/**
 * ddd4j 外部服务（地理位置/天气/行政区划）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-external} 迁入 boot 层。
 * 通用层仅保留纯 Java 的 Template 实现。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ExternalProperties.class)
public class Ddd4jExternalAutoConfiguration {

    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }

    @Bean
    public BaiduRegionTemplate baiduRegionTemplate(ExternalProperties properties, RestClient restClient) {
        return new BaiduRegionTemplate(properties.getBaiduAk(), restClient);
    }

    @Bean
    public PconlineRegionTemplate pconlineRegionTemplate(RestClient restClient) {
        return new PconlineRegionTemplate(restClient);
    }

    @Bean
    public NestedRegionTemplate nestedRegionTemplate(ObjectProvider<RedisOperationTemplate> redisOperationProvider,
                                                     ObjectProvider<IP2regionTemplate> ip2RegionTemplateProvider,
                                                     PconlineRegionTemplate pconlineRegionTemplate) {
        return new NestedRegionTemplate(redisOperationProvider.getIfAvailable(), ip2RegionTemplateProvider.getIfAvailable(), pconlineRegionTemplate);
    }

    @Bean
    public WeatherTemplate weatherTemplate(RestClient restClient) {
        return new WeatherTemplate(restClient);
    }

}
