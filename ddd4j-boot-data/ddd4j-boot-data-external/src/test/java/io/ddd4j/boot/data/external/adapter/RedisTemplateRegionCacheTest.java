package io.ddd4j.boot.data.external.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link RedisTemplateRegionCache} 行为契约测试。
 */
class RedisTemplateRegionCacheTest {

    @Test
    void constructorShouldRejectNullTemplate() {
        assertThatNullPointerException()
                .isThrownBy(() -> new RedisTemplateRegionCache(null))
                .withMessage("stringRedisTemplate must not be null");
    }

    @Test
    void shouldReadStringThroughValueOperations() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        when(values.get("region:key")).thenReturn("cached-value");

        RedisTemplateRegionCache cache = new RedisTemplateRegionCache(template);

        assertThat(cache.getString("region:key")).isEqualTo("cached-value");
    }

    @Test
    void shouldWriteStringWithTtl() {
        StringRedisTemplate template = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> values = mock(ValueOperations.class);
        when(template.opsForValue()).thenReturn(values);
        Duration ttl = Duration.ofSeconds(30);

        new RedisTemplateRegionCache(template).set("region:key", "cached-value", ttl);

        verify(values).set("region:key", "cached-value", ttl);
    }
}
