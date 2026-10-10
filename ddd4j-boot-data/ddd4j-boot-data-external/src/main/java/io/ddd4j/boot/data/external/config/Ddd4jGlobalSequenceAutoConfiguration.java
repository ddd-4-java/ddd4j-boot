package io.ddd4j.boot.data.external.config;

import cn.hutool.core.util.IdUtil;
import io.ddd4j.data.external.SequenceProperties;
import io.ddd4j.data.external.sequence.GlobalSequence;
import io.ddd4j.kit.lang.IdKit;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * ddd4j 全局序列号（雪花算法 + Redis）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-external} 迁入 boot 层。
 *
 * <p>{@link SequenceProperties} 是上游零 Spring 依赖纯 POJO（不标注 {@code @ConfigurationProperties}），
 * 因此不能用 {@code @EnableConfigurationProperties}（启动期抛 "No ConfigurationProperties annotation found"），
 * 这里用 {@link Binder} 手动绑定。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
public class Ddd4jGlobalSequenceAutoConfiguration {

    /**
     * 显式无参构造器，供 Spring 实例化本配置类。
     */
    public Ddd4jGlobalSequenceAutoConfiguration() {
    }

    private GlobalSequence globalSequence;

    /**
     * 手动绑定 {@code ddd4j.sequence.*} 到上游纯 POJO {@link SequenceProperties}。
     *
     * @param environment Spring 环境，提供配置来源
     * @return 绑定完成的序列属性实例
     */
    @Bean
    @ConditionalOnMissingBean(SequenceProperties.class)
    public SequenceProperties sequenceProperties(Environment environment) {
        SequenceProperties properties = new SequenceProperties();
        Binder.get(environment).bind(SequenceProperties.PREFIX, Bindable.ofInstance(properties));
        return properties;
    }

    /**
     * 创建全局序列生成器：workerId 缺省取本机 IP 尾段，数据中心/时间偏移/随机序列上限均有默认值。
     *
     * @param properties 已绑定的序列属性
     * @return 全局序列（雪花）生成器实例
     */
    @Bean
    public GlobalSequence globalSequence(SequenceProperties properties) {
        long workerId = Objects.isNull(properties.getWorkerId()) ? 0x0000001F & IdKit.getLastIPAddress() : properties.getWorkerId();
        long dataCenterId = Objects.isNull(properties.getDataCenterId()) ? 0L : properties.getDataCenterId();
        long timeOffset = Objects.isNull(properties.getTimeOffset()) ? 5L : properties.getTimeOffset();
        long randomSequenceLimit = Objects.isNull(properties.getRandomSequenceLimit()) ? 0L : properties.getRandomSequenceLimit();
        this.globalSequence = new GlobalSequence(
                workerId,
                dataCenterId,
                properties.isUseSystemClock(),
                timeOffset,
                randomSequenceLimit
        );
        return this.globalSequence;
    }

    /**
     * 容器关闭时优雅停机全局序列生成器。
     */
    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(globalSequence)) {
            globalSequence.shutdown();
        }
    }

    /**
     * 注册 Hutool 雪花 ID 生成器（数据中心/工作机器 ID 由本机网络信息推导）。
     *
     * @return 雪花 ID 生成器实例
     */
    @Bean
    public cn.hutool.core.lang.Snowflake snowflakeIdGenerator() {
        long datacenterId = IdUtil.getDataCenterId(31);
        long workerId = IdUtil.getWorkerId(datacenterId, 31);
        return IdKit.getSnowflake(workerId, datacenterId);
    }

}
