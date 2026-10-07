package io.ddd4j.boot.observability;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link SensitiveDataRedactor} 单元测试：敏感材料脱敏契约（默认拒绝 + 白名单放行）。
 *
 * <p>对应 spec：boot-trace-observability / Sensitive material redaction；
 * key 匹配集与占位符冻结于家族 design D9。
 */
class SensitiveDataRedactorTest {

    private static final String SECRET = "super-secret-material";

    @Test
    @DisplayName("占位符必须为 D9 冻结值 ***REDACTED***")
    void placeholderMustBeFrozenValue() {
        assertThat(SensitiveDataRedactor.PLACEHOLDER).isEqualTo("***REDACTED***");
    }

    @Test
    @DisplayName("D9 敏感 key 匹配集默认全部拒绝")
    void defaultSensitiveKeySetMustBeDenied() {
        SensitiveDataRedactor redactor = SensitiveDataRedactor.withDefaults();
        for (String key : new String[] {
                "privateKey", "private_key", "mnemonic", "seed",
                "seedPhrase", "secretKey", "keystore"}) {
            assertThat(redactor.isSensitive(key)).as("key %s 应被判定为敏感", key).isTrue();
            assertThat(redactor.redactValue(key, SECRET)).as("key %s 应被脱敏", key)
                    .isEqualTo("***REDACTED***");
        }
    }

    @Test
    @DisplayName("大小写与分隔符变体同样命中（默认拒绝）")
    void caseVariantsShouldBeDenied() {
        SensitiveDataRedactor redactor = SensitiveDataRedactor.withDefaults();
        assertThat(redactor.isSensitive("PRIVATE_KEY")).isTrue();
        assertThat(redactor.isSensitive("user.mnemonic")).isTrue();
        assertThat(redactor.isSensitive("wallet.seed-phrase")).isTrue();
        assertThat(redactor.isSensitive("SecretKey")).isTrue();
    }

    @Test
    @DisplayName("非敏感 key 保持原值")
    void normalKeysShouldPassThrough() {
        SensitiveDataRedactor redactor = SensitiveDataRedactor.withDefaults();
        assertThat(redactor.isSensitive("orderId")).isFalse();
        assertThat(redactor.redactValue("orderId", "123456")).isEqualTo("123456");
        assertThat(redactor.isSensitive("seedling-count")).isTrue();
    }

    @Test
    @DisplayName("白名单放行优先于默认拒绝")
    void allowlistShouldOverrideDenyByDefault() {
        SensitiveDataRedactor redactor = new SensitiveDataRedactor(Set.of("seed"));
        assertThat(redactor.isSensitive("seed")).isFalse();
        assertThat(redactor.redactValue("seed", SECRET)).isEqualTo(SECRET);
        // 未列入白名单的敏感 key 仍被拒绝
        assertThat(redactor.redactValue("privateKey", SECRET)).isEqualTo("***REDACTED***");
    }

    @Test
    @DisplayName("批量 Map 脱敏：命中即占位，其余保留")
    void mapRedactionShouldOnlyTouchSensitiveKeys() {
        SensitiveDataRedactor redactor = SensitiveDataRedactor.withDefaults();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("mnemonic", SECRET);
        attributes.put("traceId", "abc");
        attributes.put("userId", 42L);
        Map<String, Object> redacted = redactor.redact(attributes);
        assertThat(redacted.get("mnemonic")).isEqualTo("***REDACTED***");
        assertThat(redacted.get("traceId")).isEqualTo("abc");
        assertThat(redacted.get("userId")).isEqualTo(42L);
    }
}
