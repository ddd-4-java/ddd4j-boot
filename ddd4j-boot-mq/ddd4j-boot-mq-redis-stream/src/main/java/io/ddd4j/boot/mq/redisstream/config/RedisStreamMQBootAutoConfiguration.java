package io.ddd4j.boot.mq.redisstream.config;

import io.ddd4j.boot.mq.redis_stream.autoconfigure.Ddd4jRedisStreamMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import(Ddd4jRedisStreamMQAutoConfiguration.class)
public class RedisStreamMQBootAutoConfiguration {
}
