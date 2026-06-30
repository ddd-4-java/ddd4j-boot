package io.ddd4j.boot.data.external.config;

import cn.hutool.core.util.IdUtil;
import io.ddd4j.data.external.SequenceProperties;
import io.ddd4j.data.external.sequence.GlobalSequence;
import io.ddd4j.kit.lang.IdKit;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * ddd4j 全局序列号（雪花算法 + Redis）Spring Boot 自动配置。
 *
 * <p>从 {@code ddd4j-data-external} 迁入 boot 层。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SequenceProperties.class)
public class Ddd4jGlobalSequenceAutoConfiguration {

    private GlobalSequence globalSequence;

    @Bean
    public GlobalSequence globalSequence(SequenceProperties properties) {
        long workerId = Objects.isNull(properties.getWorkerId()) ? 0x000000FF & IdKit.getLastIPAddress() : properties.getWorkerId();
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

    @PreDestroy
    public void destroy() {
        if (Objects.nonNull(globalSequence)) {
            globalSequence.shutdown();
        }
    }

    @Bean
    public cn.hutool.core.lang.Snowflake snowflakeIdGenerator() {
        long datacenterId = IdUtil.getDataCenterId(31);
        long workerId = IdUtil.getWorkerId(datacenterId, 31);
        return IdKit.getSnowflake(workerId, datacenterId);
    }

}
