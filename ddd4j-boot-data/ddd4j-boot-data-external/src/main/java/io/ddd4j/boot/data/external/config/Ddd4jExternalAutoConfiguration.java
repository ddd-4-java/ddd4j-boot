package io.ddd4j.boot.data.external.config;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import io.ddd4j.boot.data.external.adapter.HiwepyIpRegionTemplateAdapter;
import io.ddd4j.boot.data.external.adapter.RedisOperationRegionCache;
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
import org.springframework.data.redis.core.RedisOperationTemplate;
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

    @Bean
    @ConditionalOnMissingBean(ExternalProperties.class)
    public ExternalProperties externalProperties(Environment environment) {
        ExternalProperties properties = new ExternalProperties();
        Binder.get(environment).bind(ExternalProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public RegionCache regionCache(ObjectProvider<RedisOperationTemplate> redisOperationProvider) {
        RedisOperationTemplate redisOperation = redisOperationProvider.getIfAvailable();
        if (Objects.isNull(redisOperation)) {
            return RegionCache.none();
        }
        return new RedisOperationRegionCache(redisOperation);
    }

    @Bean
    @ConditionalOnMissingBean
    public IpRegionTemplate ipRegionTemplate(ObjectProvider<IP2regionTemplate> ip2RegionTemplateProvider) {
        IP2regionTemplate template = ip2RegionTemplateProvider.getIfAvailable();
        if (Objects.isNull(template)) {
            return IpRegionTemplate.none();
        }
        return new HiwepyIpRegionTemplateAdapter(template);
    }

    @Bean
    public BaiduRegionTemplate baiduRegionTemplate(ExternalProperties properties, RegionCache regionCache) {
        return new BaiduRegionTemplate(properties.getBaiduAk(), regionCache);
    }

    @Bean
    public PconlineRegionTemplate pconlineRegionTemplate(RegionCache regionCache) {
        return new PconlineRegionTemplate(regionCache);
    }

    @Bean
    public NestedRegionTemplate nestedRegionTemplate(RegionCache regionCache, IpRegionTemplate ipRegionTemplate,
                                                     PconlineRegionTemplate pconlineRegionTemplate) {
        return new NestedRegionTemplate(regionCache, ipRegionTemplate, pconlineRegionTemplate);
    }

    @Bean
    public WeatherTemplate weatherTemplate() {
        return new WeatherTemplate();
    }

}
