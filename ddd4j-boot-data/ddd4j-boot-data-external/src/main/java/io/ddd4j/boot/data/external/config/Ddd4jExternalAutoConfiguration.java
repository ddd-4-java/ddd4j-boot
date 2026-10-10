package io.ddd4j.boot.data.external.config;

import io.ddd4j.boot.data.external.adapter.RedisTemplateRegionCache;
import io.ddd4j.data.external.ExternalProperties;
import io.ddd4j.data.external.region.*;
import io.ddd4j.data.external.weather.WeatherTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;

/**
 * ddd4j 外部服务（地理位置/天气/行政区划）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-external} 迁入 boot 层。
 * 通用层仅保留纯 Java 的 Template 实现。
 *
 * <p>{@link ExternalProperties} 是上游零 Spring 依赖纯 POJO（不标注 {@code @ConfigurationProperties}），
 * 因此不能用 {@code @EnableConfigurationProperties}（启动期抛 "No ConfigurationProperties annotation found"），
 * 这里用 {@link Binder} 手动绑定。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jExternalAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public Ddd4jExternalAutoConfiguration() {
    }

    /**
     * 手动绑定 {@code ddd4j.data.external.*} 到上游纯 POJO {@link ExternalProperties}。
     *
     * @param environment Spring 环境，提供配置来源
     * @return 绑定完成的外部服务属性实例
     */
    @Bean
    @ConditionalOnMissingBean(ExternalProperties.class)
    public ExternalProperties externalProperties(Environment environment) {
        ExternalProperties properties = new ExternalProperties();
        Binder.get(environment).bind(ExternalProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    /**
     * 注册行政区划区域缓存：容器中存在 {@link StringRedisTemplate} 时用 Redis 实现，否则回落到空实现。
     *
     * @param redisTemplateProvider 可选的 String Redis 模板
     * @return 区域缓存实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RegionCache regionCache(ObjectProvider<StringRedisTemplate> redisTemplateProvider) {
        StringRedisTemplate redisTemplate = redisTemplateProvider.getIfAvailable();
        if (Objects.isNull(redisTemplate)) {
            return RegionCache.none();
        }
        return new RedisTemplateRegionCache(redisTemplate);
    }

    /**
     * 注册默认 IP 归属地模板（空实现，业务可覆盖为真实实现）。
     *
     * @return IP 归属地模板实例
     */
    @Bean
    @ConditionalOnMissingBean
    public IpRegionTemplate ipRegionTemplate() {
        return IpRegionTemplate.none();
    }

    /**
     * 注册百度地理位置模板。
     *
     * @param properties 外部服务属性，提供百度 AK
     * @param regionCache 区域缓存
     * @return 百度地理位置模板实例
     */
    @Bean
    public BaiduRegionTemplate baiduRegionTemplate(ExternalProperties properties, RegionCache regionCache) {
        return new BaiduRegionTemplate(properties.getBaiduAk(), regionCache);
    }

    /**
     * 注册太平洋在线（Pconline）地理位置模板。
     *
     * @param regionCache 区域缓存
     * @return Pconline 地理位置模板实例
     */
    @Bean
    public PconlineRegionTemplate pconlineRegionTemplate(RegionCache regionCache) {
        return new PconlineRegionTemplate(regionCache);
    }

    /**
     * 注册嵌套地理位置模板（IP → Pconline → 百度 逐级回退）。
     *
     * @param regionCache 区域缓存
     * @param ipRegionTemplate IP 归属地模板
     * @param pconlineRegionTemplate Pconline 模板
     * @return 嵌套地理位置模板实例
     */
    @Bean
    public NestedRegionTemplate nestedRegionTemplate(RegionCache regionCache, IpRegionTemplate ipRegionTemplate,
                                                     PconlineRegionTemplate pconlineRegionTemplate) {
        return new NestedRegionTemplate(regionCache, ipRegionTemplate, pconlineRegionTemplate);
    }

    /**
     * 注册天气查询模板。
     *
     * @return 天气模板实例
     */
    @Bean
    public WeatherTemplate weatherTemplate() {
        return new WeatherTemplate();
    }

}
