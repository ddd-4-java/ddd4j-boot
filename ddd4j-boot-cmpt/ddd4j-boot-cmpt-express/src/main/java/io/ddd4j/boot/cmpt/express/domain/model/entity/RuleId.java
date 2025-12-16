package io.ddd4j.boot.cmpt.express.domain.model.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * 规则ID值对象
 * 值对象：不可变，通过值相等性判断
 */
public final class RuleId implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String value;

    /**
     * 构造函数
     */
    public RuleId(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("RuleId value cannot be null");
        }
        this.value = value;
    }

    /**
     * 从字符串创建RuleId
     */
    public static RuleId valueOf(final String value) {
        return new RuleId(value);
    }

    /**
     * 从Long创建RuleId
     */
    public static RuleId valueOf(final Long value) {
        if (value == null) {
            throw new IllegalArgumentException("RuleId value cannot be null");
        }
        return new RuleId(String.valueOf(value));
    }

    /**
     * 获取值
     */
    public String getValue() {
        return value;
    }

    /**
     * 转换为Long
     */
    public Long toLong() {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleId ruleId = (RuleId) o;
        return Objects.equals(value, ruleId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
