package io.hiwepy.boot.autoconfigure.sequence;

import io.hiwepy.boot.api.sequence.Sequence;
import io.hiwepy.boot.autoconfigure.SequenceProperties;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperationTemplate;

import java.util.Objects;

/**
 * GlobalSequence 自动配置类
 */
//@Configuration
@ConditionalOnClass({RedisOperationTemplate.class, GlobalSequence.class})
@ConditionalOnProperty(prefix = SequenceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(SequenceProperties.class)
public class GlobalSequenceConfiguration {

    private GlobalSequence globalSequence;

    @Bean
    public GlobalSequence globalSequence(ObjectProvider<RedisOperationTemplate> redisOperationProvider,
                                         SequenceProperties properties) {
        long workerId = Objects.isNull(properties.getWorkerId()) ? 0x000000FF & Sequence.getLastIPAddress() : properties.getWorkerId();
        long dataCenterId = Objects.isNull(properties.getDataCenterId()) ? 0L : properties.getDataCenterId();
        long timeOffset = Objects.isNull(properties.getTimeOffset()) ? 5L : properties.getTimeOffset();
        long randomSequenceLimit = Objects.isNull(properties.getRandomSequenceLimit()) ? 0L : properties.getRandomSequenceLimit();
        this.globalSequence = new GlobalSequence(
                redisOperationProvider.getIfAvailable(),
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
        if (globalSequence != null) {
            globalSequence.shutdown();
        }
    }
}