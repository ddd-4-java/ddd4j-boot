package io.ddd4j.boot.mq.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 将 legacy {@code base-mq.*} 配置映射到 {@code ddd4j.mq.*}。
 */
public class Ddd4jMQEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String LEGACY_PREFIX = "base-mq.";
    private static final String TARGET_PREFIX = "ddd4j.mq.";
    private static final String MIGRATED_SOURCE = "ddd4jMqLegacyMigration";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> migrated = new HashMap<>();
        migrateKey(environment, migrated, "enable", "enabled");
        migrateKey(environment, migrated, "impl", "broker");
        migrateKey(environment, migrated, "namespace", "namespace");
        migrateKey(environment, migrated, "default-topic", "defaultTopic");
        migrateKey(environment, migrated, "persist", "persist");
        migrateKey(environment, migrated, "serialization", "serialization");
        migrateKey(environment, migrated, "retries", "retries");
        if (!migrated.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(MIGRATED_SOURCE, migrated));
        }
    }

    private void migrateKey(ConfigurableEnvironment environment, Map<String, Object> target, String legacyKey, String newKey) {
        String legacyFull = LEGACY_PREFIX + legacyKey;
        if (environment.containsProperty(legacyFull) && !environment.containsProperty(TARGET_PREFIX + newKey)) {
            target.put(TARGET_PREFIX + newKey, environment.getProperty(legacyFull));
        }
        if ("impl".equals(legacyKey) && target.containsKey(TARGET_PREFIX + "broker")) {
            Object broker = target.get(TARGET_PREFIX + "broker");
            if (broker instanceof String s && "redisStream".equals(s)) {
                target.put(TARGET_PREFIX + "broker", "redis-stream");
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}
