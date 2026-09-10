package io.ddd4j.boot.data.external.adapter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 使用真实 Redis 验证字符串往返和 TTL 语义。
 */
@Testcontainers(disabledWithoutDocker = true)
class RedisTemplateRegionCacheIntegrationTest {

    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379);

    private static LettuceConnectionFactory connectionFactory;
    private static RedisTemplateRegionCache cache;

    @BeforeAll
    static void setUpRedisTemplate() {
        connectionFactory = new LettuceConnectionFactory(REDIS.getHost(), REDIS.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();
        StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
        template.afterPropertiesSet();
        cache = new RedisTemplateRegionCache(template);
    }

    @AfterAll
    static void closeConnectionFactory() {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }

    @Test
    void shouldRoundTripAndExpireStringValue() throws InterruptedException {
        cache.set("region:contract", "beijing", Duration.ofSeconds(1));
        assertThat(cache.getString("region:contract")).isEqualTo("beijing");

        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (cache.getString("region:contract") != null && System.nanoTime() < deadline) {
            Thread.sleep(100);
        }
        assertThat(cache.getString("region:contract")).isNull();
    }
}
