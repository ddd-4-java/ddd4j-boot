package io.ddd4j.boot.observability;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 敏感材料脱敏器：默认拒绝 + 白名单放行（对应家族 design D5/D9）。
 *
 * <p>key 匹配策略：归一化（小写、剔除 {@code _ - . 空格}）后包含敏感 token 即命中；
 * 命中值以 {@link #PLACEHOLDER} 占位呈现而非删除，保留字段存在性。
 * 对应 spec：boot-trace-observability / Sensitive material redaction。
 *
 * @author <a href="https://github.com/partme-ai">PartMe.AI</a>
 */
public final class SensitiveDataRedactor {

    /**
     * D9 冻结的脱敏占位符。
     */
    public static final String PLACEHOLDER = "***REDACTED***";

    /**
     * D9 首批敏感 key 匹配集（原始形态，供配置参考与文档展示）。
     */
    private static final Set<String> DEFAULT_SENSITIVE_KEYS = Set.of(
            "privateKey", "private_key", "mnemonic", "seed", "seedPhrase", "secretKey", "keystore");

    /**
     * 归一化后的敏感 token 集（contains 语义命中）。
     */
    private static final Set<String> DEFAULT_SENSITIVE_TOKENS = Set.of(
            "privatekey", "mnemonic", "seed", "secretkey", "keystore");

    /**
     * 敏感 token 集。
     */
    private final Set<String> sensitiveTokens;

    /**
     * 白名单（归一化精确匹配，命中即放行）。
     */
    private final Set<String> allowlist;

    /**
     * 以默认敏感集、空白名单构建。
     */
    public SensitiveDataRedactor() {
        this(Collections.emptySet());
    }

    /**
     * 以默认敏感集与指定白名单构建。
     *
     * @param allowlist 白名单 key 集合（允许 null）
     */
    public SensitiveDataRedactor(final Collection<String> allowlist) {
        this.sensitiveTokens = DEFAULT_SENSITIVE_TOKENS;
        this.allowlist = Objects.isNull(allowlist)
                ? Collections.emptySet()
                : allowlist.stream().map(SensitiveDataRedactor::normalize)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * 默认策略实例（默认拒绝、无白名单）。
     *
     * @return 脱敏器实例
     */
    public static SensitiveDataRedactor withDefaults() {
        return new SensitiveDataRedactor();
    }

    /**
     * D9 首批敏感 key 匹配集（只读视图）。
     *
     * @return 原始形态敏感 key 集合
     */
    public static Set<String> defaultSensitiveKeys() {
        return Collections.unmodifiableSet(DEFAULT_SENSITIVE_KEYS);
    }

    /**
     * 判断 key 是否命中敏感匹配集（默认拒绝 + 白名单放行）。
     *
     * @param key 属性/字段名
     * @return true 表示必须脱敏
     */
    public boolean isSensitive(final String key) {
        if (!StringUtils.hasText(key)) {
            return false;
        }
        String normalized = normalize(key);
        if (allowlist.contains(normalized)) {
            return false;
        }
        for (String token : sensitiveTokens) {
            if (normalized.contains(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对单个值脱敏：命中即占位，未命中保持原值。
     *
     * @param key   属性/字段名
     * @param value 原值
     * @return 占位符或原值
     */
    public String redactValue(final String key, final String value) {
        return isSensitive(key) ? PLACEHOLDER : value;
    }

    /**
     * 批量脱敏属性 Map（键保留、命中值置占位符）。
     *
     * @param attributes 原属性
     * @return 脱敏后的新 Map
     */
    public Map<String, Object> redact(final Map<String, Object> attributes) {
        if (Objects.isNull(attributes) || attributes.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new LinkedHashMap<>(attributes.size());
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String text) {
                result.put(entry.getKey(), redactValue(entry.getKey(), text));
            } else {
                result.put(entry.getKey(), value);
            }
        }
        return result;
    }

    /**
     * key 归一化：小写并剔除 {@code _ - . 空白} 分隔符。
     *
     * @param key 原始 key
     * @return 归一化 key
     */
    private static String normalize(final String key) {
        return key.toLowerCase(Locale.ROOT).replaceAll("[_\\-.\\s]", "");
    }
}
